package com.example.minishooping.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="product")
@Getter
@Setter
@ToString(exclude = "carts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //번호
    @Column(length=100, nullable=false) //length=255자까지
    private String name; //상품명
    @Column(columnDefinition = "TEXT") //65535자
    private String description;
    //integer=2진수 > 10진수
    //precision 자리수(10자리), scale 소숫점(2자리)
    @Column(precision = 10, scale=2, nullable=false)
    private BigDecimal price; //가격
    @Column(nullable=false)
    @Builder.Default
    private Integer stockQuantity=0; //재고수량
    @Column(length=50, nullable=false)
    private String category; //카테고리
    @Column(length=200)
    private String imageUrl; //이미지파일
    @Column(nullable=false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt; //등록날짜
    @LastModifiedDate
    private LocalDateTime updatedAt; //수정날짜
    @Column(length=20, nullable=false)
    @Builder.Default
    private String status="AVAILABLE"; //상품상태(판매(AVAILABLE), 소진(SOLD_OUT), 할인(DISCOUNTINUED)

    @OneToMany(mappedBy="product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartEntity> carts = new ArrayList<>();
}
