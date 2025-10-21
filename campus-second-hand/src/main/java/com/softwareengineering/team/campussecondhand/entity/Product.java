package com.softwareengineering.team.campussecondhand.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private Integer level;
    @Column(length=1024)
    private String remark;
    private Integer sortOrder;
    private Integer count;
    private Integer display;
    private Integer transactionType;
    private Integer sales;
    private Long uid;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if(level==null) level=5;
        if(count==null) count=1;
        if(display==null) display=1;
        if(sales==null) sales=0;
    }
    @PreUpdate
    public void preUpdate(){ updatedAt = LocalDateTime.now(); }
}