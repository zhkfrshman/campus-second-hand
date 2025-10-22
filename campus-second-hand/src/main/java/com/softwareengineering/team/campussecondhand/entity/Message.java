package com.softwareengineering.team.campussecondhand.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long uid; // 用户ID
    private Long sid; // 商品ID
    private String content; // 留言内容
    private LocalDateTime createdAt;
    private Integer display = 1; // 1表示显示，0表示隐藏
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", insertable = false, updatable = false)
    private User user;
    
    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }
}