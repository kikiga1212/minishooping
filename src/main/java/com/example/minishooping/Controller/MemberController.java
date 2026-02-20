package com.example.minishooping.Controller;

import com.example.minishooping.DTO.MemberDTO;
import com.example.minishooping.Serivce.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members") //url localhost:8080/첫번째요청명/두번쨰세부요청
public class MemberController {
    private final MemberService memberService;

    //회원목록
    @GetMapping
    public String listMembers(Model model) {
        List<MemberDTO> members = memberService.getAllMembers();
        model.addAttribute("members", members);
        return "member/list";
    }
    //회원가입폼
    @GetMapping("/register")
    public String registerForm(Model model){
        //new memberDTO로 빈 DTO를 전달하면 검증처리, object로 form을 사용가능
        model.addAttribute("member", new MemberDTO());
        return "member/register";
    }
    //회원가입처리
    @PostMapping("/register")
    public String registerMember(MemberDTO memberDTO) {
        try { //가입후 로그인 페이지로 이동
            memberService.createMember(memberDTO);
            return "redirect:/members/login";
        /*} catch(IllegalArgumentException e) { //서비스에서 전달한 오류메시지
            return "redirect:/members/register?error="+e.getMessage();
        } catch(IllegalStateException e) { //오류 종류가 여러개이면 catch를 여러번 사용
            return "redirect:/members/register?error="+e.getMessage();*/
        } catch(Exception e) { //오류종류와 상관없이 모든 오류를 받아서 처리
            return "redirect:/members/register?error="+e.getMessage();
        }
    }
    //로그인 폼
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }
    //로그인처리(Security사용시 필요없음)
    //보내는 변수와 받는 변수가 동일하면 requestParam은 생략 가능
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        Model model) {
        try{ //정상적인 로그인 성공
            MemberDTO member = memberService.login(username, password);
            return "redirect:/";
        } catch(IllegalStateException e) { //로그인 실패
            model.addAttribute("error", e.getMessage());
            return "member/login";
        }
    }
    //회원정보 조회
    @GetMapping("/{id}")
    public String viewMember(@PathVariable Long id, Model model) {
        MemberDTO member = memberService.getMember(id);
        model.addAttribute("member", member);
        return "member/view";
    }
    //회원정보 수정폼
    // {} 가 있으면 pathvariable로 연계
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        MemberDTO member = memberService.getMember(id);
        model.addAttribute("member", member);

        return "member/edit";
    }
    //회원정보 수정처리
    //DTO에 오류가 발생하면 ModelAttribute를 붙인다.
    @PostMapping("/edit/{id}")
    public String updatemember(@PathVariable Long id, MemberDTO memberDTO) {
        memberService.updateMember(id, memberDTO);

        return "redirect:/members/"+id;
    }
    //회원탈퇴(소프트 삭제)
    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return "redirect:/members";
    }
    //마이페이지
    @GetMapping("/mypage/{id}")
    public String myPage(@PathVariable Long id, Model model) {
        MemberDTO member = memberService.getMember(id);
        model.addAttribute("member", member);
        return "member/mypage";
    }
    //상태별 회원 조회
    @GetMapping("/status/{status}")
    public String getMembersByStatus(@PathVariable String status, Model model) {
        List<MemberDTO> members = memberService.getmembersByStatus(status);
        model.addAttribute("members", members);
        model.addAttribute("status", status);
        return "member/list";
    }
}
