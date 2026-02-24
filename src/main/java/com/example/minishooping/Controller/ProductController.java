package com.example.minishooping.Controller;

import com.example.minishooping.DTO.ProductDTO;
import com.example.minishooping.Serivce.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

//400번(Mapping확인), 405번(Return확인) 오류
@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService producteService;

    //상품 목록
    @GetMapping
    public String listProducts(Model model) {
        List<ProductDTO> products = producteService.getAllProducts();
        model.addAttribute("products", products);
        return "product/list";
    }

    //상품 등록 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new ProductDTO());
        return "product/create";
    }

    //상품 등록 처리
    @PostMapping("/new")
    public String createProduct(ProductDTO productDTO) {
        try {
            producteService.createProduct(productDTO);
            return "redirect:/products";
        } catch(IllegalArgumentException e) {
            return "redirect:/product/new?error=" + e.getMessage();
        }
    }

    //상품 상세 페이지
    @GetMapping("/{id}")
    public String viewroduct(@PathVariable Long id, Model model) {
        ProductDTO product = producteService.getProduct(id);
        model.addAttribute("product", product);
        return "product/view";
    }

    //상품 수정폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        ProductDTO product = producteService.getProduct(id);
        model.addAttribute("product", product);
        return "product/edit";
    }

    //상품 수정 처리
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, ProductDTO productDTO) {
        try {
            producteService.updatedProduct(id, productDTO);
            return  "redirect:/products/" + id;
        } catch(IllegalArgumentException e) {
            return "redirect:/products/edit/" + id+ "?error=" + e.getMessage();
        }
    }

    //상품 삭제
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        producteService.deleteProduct(id);
        return "redirect:/products";
    }

    //카테고리별 상품 조회
    @GetMapping("/category/{category}")
    public String getProcuctsByCategory(@PathVariable String category,
                                        Model model) {
        List<ProductDTO> products = producteService.getProductsByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        return "product/list";
    }
    //다양한 검색을 통한 조회
    //상품 검색
    @GetMapping("/search") //keyword오류 발생시 @RequestParam을 붙인다.
    public String searchProducts(String keyword, Model model) {
        List<ProductDTO> products = producteService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "product/list";
    }
    //가격 범위로 상품조회
    @GetMapping("/price-range")
    public String getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice,
                                          Model model){
        List<ProductDTO> products = producteService.getProductsByPriceRange(minPrice, maxPrice);
        model.addAttribute("products", products);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "product/list";
    }
    //재고 있는 상품만 조회
    @GetMapping("/available")
    public String getAvailableProducts(Model model) {
        List<ProductDTO> products = producteService.getAvailableProducts();
        model.addAttribute("products", products);
        return "product/list";
    }
    //재고 관리 폼
    @GetMapping("/stock/{id}")
    public String stockManagementForm(@PathVariable Long id, Model model) {
        ProductDTO product = producteService.getProduct(id);
        model.addAttribute("product", product);
        return "product/stock";
    }
    //재고 증가
    @PostMapping("/stock/increase/{id}")
    public String increaseStock(@PathVariable Long id, Integer quantity) {
        producteService.increaseStock(id, quantity);
        return "redirect:/products/"+id;
    }
    //재고 감소
    @PostMapping("/stock/decrease/{id}")
    public String decreaseStock(@PathVariable Long id, Integer quantity) {
        try {
            producteService.decreaseStock(id, quantity);
            return "redirect:/products/"+ id;
        } catch(IllegalStateException e) {
            return "redirect:/products/stock/"+id+"?error="+e.getMessage();

        }
    }
}
