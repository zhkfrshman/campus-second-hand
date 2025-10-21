package com.softwareengineering.team.campussecondhand.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_password")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPassword {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long uid;
    private String passwordHash;
}