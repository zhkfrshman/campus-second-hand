package com.softwareengineering.team.campussecondhand.service.impl;

import com.softwareengineering.team.campussecondhand.entity.Product;
import com.softwareengineering.team.campussecondhand.repository.ProductRepository;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.util.FileStorageUtil;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
}
