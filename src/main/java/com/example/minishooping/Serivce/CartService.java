package com.example.minishooping.Serivce;

import com.example.minishooping.DTO.CartDTO;
import com.example.minishooping.Entity.CartEntity;
import com.example.minishooping.Entity.MemberEntity;
import com.example.minishooping.Entity.ProductEntity;
import com.example.minishooping.Repository.CartRepository;
import com.example.minishooping.Repository.MemberRepository;
import com.example.minishooping.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    //연관테이블의 레포지토리
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    //장바구니에 상품추가
    public CartDTO addToCart(CartDTO cartDTO){
        //장바구니 저장 전에 연관테이블의 데이터존재여부를 반드시 확인
        //회원존재
        MemberEntity member = memberRepository.findById(cartDTO.getMemberId())
                .orElseThrow(()->new IllegalStateException("회원이 존재하지 않습니다."));
        //상품 존재
        ProductEntity product = productRepository.findById(cartDTO.getProductId())
                .orElseThrow(()->new IllegalStateException("상품이 존재하지 않습니다."));
        //재고 확인
        if(product.getStockQuantity()<cartDTO.getQuantity()){
            throw new IllegalStateException("구매할 재고가 부족합니다.");
        }
        //이미 장바구니에 해당상품의 존재여부
        Optional<CartEntity> existingCart = cartRepository.findByMemberIdAndProductId(
                cartDTO.getMemberId(), cartDTO.getProductId()
        );

        CartEntity cartEntity;
        if(existingCart.isPresent()){//이미 장바구니에 상품이 존재하면
            cartEntity = existingCart.get();//존재하는 정보로 저장
            //기존 장바구니에 수량만 추가
            int newQuantity = cartEntity.getQuantity()+cartDTO.getQuantity();
            if(product.getStockQuantity()<newQuantity){//추가수량이 재고량을 확인
                throw new IllegalStateException("구매할 재고 수량이 부족합니다.");
            }
            cartEntity.setQuantity(newQuantity);//추가수량을 등록
        }else {//장바구니에 해당상품이 존재하지 않으면
            cartEntity = CartEntity.builder()
                    .member(member)// 구매회원정보 저장
                    .product(product) //상품정보 저장
                    .quantity(cartDTO.getQuantity()) //구매수량
                    .build();
        }
        CartEntity saved = cartRepository.save(cartEntity);
        return mapToDTO(saved);
    }
    //회원의 장바구니 조회
    @Transactional(readOnly = true)
    public List<CartDTO> getCartByMember(Long memberId){
        //회원존재
        if(!memberRepository.existsById(memberId)){
            throw new IllegalStateException("회원이 존재하지 않습니다.");
        }
        return cartRepository.findByMemberId(memberId)
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    //개별읽기
    @Transactional(readOnly = true)
    public CartDTO getCart(Long id){
        CartEntity cartEntity = cartRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("장바구니가 존재하지 않습니다."));
        return mapToDTO(cartEntity);
    }
    //장바구니의 수량 변경
    public CartDTO updateQuantity(Long id, Integer quantity){
        if(quantity<1){
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        CartEntity cartEntity = cartRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("장바구니가 존재하지 않습니다."));
        //재고확인
        if(cartEntity.getProduct().getStockQuantity()<quantity){
            throw new IllegalStateException("재고가 부족합니다.");
        }
        cartEntity.setQuantity(quantity);
        CartEntity updated = cartRepository.save(cartEntity);
        return mapToDTO(updated);
    }
    //장바구니 개별 삭제
    public boolean deleteCart(Long id){
        if(cartRepository.existsById(id)){
            cartRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //회원의 장바구니 전체 삭제
    public boolean clearCart(Long memberId){
        if(!memberRepository.existsById(memberId)){
            throw new IllegalStateException("회원이 존재하지 않습니다.");
        }
        cartRepository.deleteByMemberId(memberId);
        return true;
    }

    //장바구니의 총금액 계산
    @Transactional(readOnly = true)
    public BigDecimal getTotalPrice(Long memberId){
        List<CartEntity> carts = cartRepository.findByMemberId(memberId);
        return carts.stream()
                .map(cart -> cart.getProduct().getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    //장바구니 항목 개수조회
    @Transactional(readOnly = true)
    public int getCartCount(Long memberId){
        return cartRepository.countByMemberId(memberId);
    }

    //공통 변화
    private CartDTO mapToDTO(CartEntity cart) {
        CartDTO dto = modelMapper.map(cart, CartDTO.class);
        //회원정보(연관Entity에 해당하는 id)
        //getEntity->getEntity내 변수명
        dto.setMemberId(cart.getMember().getId());
        dto.setMemberName(cart.getMember().getName());
        //상품정보
        dto.setProductId(cart.getProduct().getId());
        dto.setProductName(cart.getProduct().getName());
        dto.setProductPrice(cart.getProduct().getPrice());
        dto.setProductImageUrl(cart.getProduct().getImageUrl());

        //총금액 계산(가격*수량)
        dto.setTotalPrice(
                cart.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(cart.getQuantity()))
        );
        return dto;

    }
}
