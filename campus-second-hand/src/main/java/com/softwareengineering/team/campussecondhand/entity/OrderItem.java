package com.softwareengineering.team.campussecondhand.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long orderId;
    private Long sid; // 商品ID
    private Integer quantity;
    private Double price;
    private String productName;
    private String productImage;
}