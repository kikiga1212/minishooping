package com.example.minishooping.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProductDTO {
    private Long id; //번호
    private String name; //상품명
    private String description;
    private BigDecimal price; //가격
    private Integer stockQuantity; //재고수량
    private String category; //카테고리
    private String imageUrl; //이미지파일
    private LocalDateTime createdAt; //등록날짜
    private LocalDateTime updatedAt; //수정날짜
    private String status; //상품상태(판매(AVAILABLE), 소진(SOLD_OUT), 할인(DISCOUNTINUED)

    private Integer cartCount; //장바구니에 담긴 횟수
}
