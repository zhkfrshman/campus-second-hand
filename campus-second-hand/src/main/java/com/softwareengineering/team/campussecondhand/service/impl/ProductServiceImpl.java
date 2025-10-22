package com.softwareengineering.team.campussecondhand.service.impl;

import com.softwareengineering.team.campussecondhand.entity.Product;
import com.softwareengineering.team.campussecondhand.repository.ProductRepository;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.util.FileStorageUtil;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FileStorageUtil fileStorageUtil;

    public ProductServiceImpl(ProductRepository productRepository,
                              FileStorageUtil fileStorageUtil) {
        this.productRepository = productRepository;
        this.fileStorageUtil = fileStorageUtil;
    }

    @Override
    public Page<Product> listAvailable(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findByDisplay(1, pageable);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Product createProduct(String name, Double price, String remark, MultipartFile imageFile, Long uid) throws Exception {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setRemark(remark);
        p.setUid(uid);
        // 保存图片并设置 url
        if (imageFile != null && !imageFile.isEmpty()) {
            String url = fileStorageUtil.store(imageFile);
            p.setImage(url);
        } else {
            p.setImage("/static/default-product.png");
        }
        p.setDisplay(1);
        return productRepository.save(p);
    }

    @Override
    public List<Product> findAllActiveProducts() {
        return productRepository.findByDisplayOrderByCreatedAtDesc(true);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        // 确保设置了必要的字段
        if (product.getDisplay() == null) {
            product.setDisplay(1); // 1表示展示，0表示不展示
        }
        if (product.getSortOrder() == null) {
            product.setSortOrder(0);
        }
        if (product.getCount() == null) {
            product.setCount(1);
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        
        // 保存商品
        return productRepository.save(product);
    }
    
    @Override
    public List<Product> findByUserId(Long userId) {
        return productRepository.findByUid(userId);
    }
}
