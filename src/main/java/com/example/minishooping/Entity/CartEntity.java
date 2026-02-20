package com.example.minishooping.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="cart",
uniqueConstraints = {
        //member_id와 product_id는 유일한 키로만 존재(생략가능)
        @UniqueConstraint(columnNames={"member_id","product_id"})
})
@Getter
@Setter
@ToString(exclude={"member", "product"}) //toString사용시 2개의 필드는 제외, 무한반복 방지
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Builder.Default
    private Integer quantity=1; //수량
    @Column(nullable=false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    //이용할 테이블(부모테이블)
    //회원테이블과 연관(하나의 회원은 여러장바구니에 존재가 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable=false)
    private MemberEntity member;

    //상품테이블과 연관(하나의 상품은 여러장바구니에 존재가 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", nullable=false)
    private ProductEntity product;
}
