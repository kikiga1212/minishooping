package com.example.minishooping.Serivce;

import com.example.minishooping.DTO.ProductDTO;
import com.example.minishooping.Entity.ProductEntity;
import com.example.minishooping.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

//500번오류
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    //상품 등록(재고와 가격은 반드시 존재)
    public ProductDTO createProduct(ProductDTO dto) {
        //가격 유효성 검증
        if(dto.getPrice().compareTo(BigDecimal.ZERO)<0) {
            //전달받은 값에 문제가 생겨서 처리가 불가능
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
            //repository로 데이터베이스 처리시 문제가 발생하면
            //throw new IllegalStateException()
        }
        //재고 유효성 검증
        if(dto.getStockQuantity()<0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }
        //DTO->Entity변환
        ProductEntity entity = modelMapper.map(dto, ProductEntity.class);
        //저장
        ProductEntity saved = productRepository.save(entity);

        return mapToDTO(saved); //저장된 결과를 Controller에 전달
    }
    //개별조회
    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("상품이 존재하지 않습니다."));

        return mapToDTO(product);
    }
    //전체조회
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts(){
        return productRepository.findAll().stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    //카테고리별 조회
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    //카테고리와 상태별 조회
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategoryAndStatus(String category,String status) {
        return productRepository.findByCategoryAndStatus(category, status).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }
    //가격 범위로 조회
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal minPrice,
                                                    BigDecimal maxPrice){
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }
    //상품명 조회
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String name) {
        return productRepository.findByNameContaining(name).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }
    //재고있는 상품 조회
    @Transactional(readOnly = true)
    public List<ProductDTO> getAvailableProducts() {
        return productRepository.findByStockQuantityGreaterThan(0)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
    //수정
    public ProductDTO updatedProduct(Long id, ProductDTO dto) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("상품이 존재하지 않습니다."));

        //가격과 재고의 유효성 검증
        if(dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO)<0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
        if(dto.getStockQuantity() != null && dto.getStockQuantity()<0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());

        if(dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }
        ProductEntity updated = productRepository.save(product);
        return mapToDTO(updated);
    }
    //재고량 증가
    public ProductDTO increaseStock(Long id, Integer quantity) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("상품이 존재하지 않습니다."));
        product.setStockQuantity(product.getStockQuantity()+quantity);
        ProductEntity updated = productRepository.save(product);
        return mapToDTO(updated);
    }
    //재고량 감소
    public ProductDTO decreaseStock(Long id, Integer quantity) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("상품이 존재하지 않습니다."));
        if(product.getStockQuantity() < quantity) {
            throw  new IllegalStateException("재고가 부족하빈다.");
        }
        product.setStockQuantity(product.getStockQuantity()-quantity);
        ProductEntity updated = productRepository.save(product);
        return mapToDTO(updated);
    }
    //삭제(소프트 삭제)
    public boolean deleteProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(()->new IllegalStateException("상품이 존재하지 않습니다."));

        product.setStatus("DISCONTINUED");
        productRepository.save(product);
        return true;
    }
    //삭제(실제 삭제)
    public boolean hardDeleteProduct(Long id) {
        if(productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    //Entity->DTO로 변환하는 공용 메소드
    private ProductDTO mapToDTO(ProductEntity product) {
        ProductDTO dto = modelMapper.map(product, ProductDTO.class);

        //장바구니의 담긴 횟수 추가
        if(product.getCarts() != null) { //현재상품과 연관된 장바구니가 존재하면
            dto.setCartCount(product.getCarts().size());
        }
        return dto;
    }
}
