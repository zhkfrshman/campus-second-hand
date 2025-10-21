package com.softwareengineering.team.campussecondhand.service;

import com.softwareengineering.team.campussecondhand.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    Page<Product> listAvailable(int page, int size);
    Product findById(Long id);
    Product createProduct(String name, Double price, String remark, MultipartFile imageFile, Long uid) throws Exception;
}