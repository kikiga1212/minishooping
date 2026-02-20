package com.example.minishooping.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private Long id; //일련번호
    private String username; //아이디
    private String password; //비밀번호
    private String name; //이름
    private String email; //이메일
    private String phone; //전화번호
    private String address; //주소
    private LocalDateTime createdAt; //가입일자
    private LocalDateTime updatedAt; //수정일자
    private String status; //회원상태(활성(ACTIVE), 비활성(INACTIVE), 삭제(DELETED)

    private Integer cartItemCount; //장바구니 항목 수
}
