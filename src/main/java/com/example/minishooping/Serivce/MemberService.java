package com.example.minishooping.Serivce;

import com.example.minishooping.DTO.MemberDTO;
import com.example.minishooping.Entity.MemberEntity;
import com.example.minishooping.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//회원관리
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    //회원가입(저장)
    public MemberDTO createMember(MemberDTO dto) {
        //유일한 값의 존재여부확인
        if(memberRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디명입니다.");
        }
        if(memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        MemberEntity entity = modelMapper.map(dto, MemberEntity.class);
        //비밀번호 암호화해서 저장
        entity.setStatus("ACTIVE");  //추가정보를 수동으로 등록

        MemberEntity saved = memberRepository.save(entity); //저장
        return mapToDTO(saved);
    }
    //개별조회
    @Transactional(readOnly = true) //읽기용
    public MemberDTO getMember(Long id) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("회원이 존재하지 않습니다."));

        return mapToDTO(member);
    }
    //전체조회
    @Transactional(readOnly = true)
    public List<MemberDTO> getAllMembers(){
        return memberRepository.findAll()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    //상태별 조회
    @Transactional(readOnly = true)
    public List<MemberDTO> getmembersByStatus(String status) {
        return memberRepository.findByStatus(status)
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    //수정
    public MemberDTO updateMember(Long id, MemberDTO dto) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        //기존 메일주소와 수정된 메일주소가 다르면서 기존 이메일이 존재하면
        if(!member.getEmail().equals(dto.getEmail()) && memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        //비밀번호 수정시 변경된 비밀번호 적용
        if(dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            member.setPassword(dto.getPassword());
        }

        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setAddress(dto.getAddress());
        if(dto.getStatus()!= null) {
            member.setStatus(dto.getStatus());
        }
        MemberEntity updated = memberRepository.save(member);
        return mapToDTO(updated);
    }
    //삭제(소프트 삭제-상태만 삭제)
    public boolean deleteMember(Long id) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        member.setStatus("DELETED");
        memberRepository.save(member);
        return true;
    }
    //삭제(실제 삭제
    public boolean hardDeleteMember(Long id){
        if(memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }
    //로그인 검증(security사용시 로그인 검증은 클래스를 따로)
    public MemberDTO login(String username, String password) {
        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("아이디가 일치하지 않습니다."));

        if(!password.equals(member.getPassword())){
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        if(!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        return mapToDTO(member);
    }
    //각 메소드에서 반복처리되는 부분을 공통 메소드로
    private MemberDTO mapToDTO(MemberEntity member) {
        MemberDTO dto = modelMapper.map(member, MemberDTO.class);

        dto.setPassword(null); //민감한 데이터 제외

        if(member.getCarts() != null) { //회원에 장바구니가 존재하면
            dto.setCartItemCount(member.getCarts().size()); //장바구니갯수를 추가저장
        }

        return dto;
    }
}
