package com.example.minishooping.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="member")
@Getter
@Setter
@Builder
@ToString(exclude="carts") //toString시 제외필드
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) //날짜 자동생성
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //일련번호
    @Column(length=50, nullable=false, unique=true)
    private String username; //아이디
    @Column(length=100, nullable=false)
    private String password; //비밀번호
    @Column(length=50, nullable = false)
    private String name; //이름
    @Column(length=100, nullable=false, unique=true)
    private String email; //이메일
    @Column(length=20, nullable=false)
    private String phone; //전화번호
    @Column(length=200)
    private String address; //주소
    @Column(nullable=false, updatable = false)
    @CreatedDate //생성할때만 등록
    private LocalDateTime createdAt; //가입일자
    @LastModifiedDate //마지막 수정일자 등록
    private LocalDateTime updatedAt; //수정일자
    @Column(length=20, nullable=false)
    @Builder.Default //builder사용시 기본값
    private String status = "ACTIVE"; //회원상태(활성(ACTIVE), 비활성(INACTIVE), 삭제(DELETED)

    //자식테이블과의 관계
    @OneToMany(mappedBy="member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartEntity> carts = new ArrayList<>();
}
