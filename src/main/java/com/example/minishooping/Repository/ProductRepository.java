package com.example.minishooping.Repository;

import com.example.minishooping.Entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends
        JpaRepository<ProductEntity, Long> {
    //상품명으로 검색
    List<ProductEntity> findByNameContaining(String name);
    //카테고리로 상품 조회
    List<ProductEntity> findByCategory(String category);
    //가격 범위로 조회전달한 두 값의 사이이에 속하는 데이터를 조회)
    List<ProductEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    //재고가 있는 상품 조회(입력한 수보다 큰 데이터만 조회)
    List<ProductEntity> findByStockQuantityGreaterThan(Integer quantity);
    //카테고리와 상태로 조회(카테고리와 상태가 모두 동일한 데이터만 조회)
    List<ProductEntity> findByCategoryAndStatus(String category, String status);
}
