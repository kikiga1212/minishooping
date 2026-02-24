package com.example.minishooping.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@ToString @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long id;//장바구니 id번호
    private Long memberId;//소유회원의 id번호
    private Long productId;//구매상품의 id번호
    private Integer quantity=1; //구매수량
    private LocalDateTime createdAt;//등록일자

    //조회시 사용
    private String memberName;//회원이름
    private String productName;//상품명
    private BigDecimal productPrice;//상품가격
    private BigDecimal totalPrice;//총가격
    private  String productImageUrl;//상품이미지
}

/*
Entity는 부모의 테이블을 지정(데이터베이스에 id저장)
DTO에서는 부모테이블의 참조된 id번호를 지정
 */