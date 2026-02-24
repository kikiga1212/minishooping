package com.example.minishooping.Repository;

import com.example.minishooping.Entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends
        JpaRepository<MemberEntity, Long> {
    //아이디(사용자)명으로 회원조회
    Optional<MemberEntity> findByUsername(String username);
    //이메일로 회원조회
    Optional<MemberEntity> findByEmail(String email);
    //회원상태 조회
    Optional<MemberEntity> findByStatus(String status);
    //회원이름 검색(이름이 포함된 모든 데이터를 조회)
    List<MemberEntity> findByNameContaining(String name);
    //아이디 중복 체크(존재하면 true, 존재하지 않으면 false)
    boolean existsByUsername(String username);
    //이메일 중복 체크
    boolean existsByEmail(String email);
    //아이디와 이메일은 유일한 값으로 설정해서 중복이 불가능
}
