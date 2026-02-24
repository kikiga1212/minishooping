package com.example.minishooping.Repository;

import com.example.minishooping.Entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends
        JpaRepository<CartEntity, Long> {
    //회원Id로 장바구니 목록조회
    //private MemberEntity member;==>MemberId(해당테이블의 id)
    //Member테이블에 ID변수로 조회
     List<CartEntity> findByMemberId(Long memberId);

    //상품Id로 장바구니 조회
    //private ProductEntity product;
    List<CartEntity> findByProductId(Long productId);

    //회원id와 상품id로 장바구니 항목 조회
    List<CartEntity> findByMemberIdAndProductId(Long memberId, Long productId);

    //회원id의 장바구니 전체 삭제
    void deleteByMemberId(Long memberId);

    //회원의 장바구니 항목 갯수 조회
    int countByMemberId(Long memberId);

    //회원id와 상품id 로 장바구니 존재 여부
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

}
