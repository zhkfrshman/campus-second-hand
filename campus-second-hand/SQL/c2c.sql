CREATE DATABASE IF NOT EXISTS c2c CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE c2c;

-- 用户表
CREATE TABLE IF NOT EXISTS user_account (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            phone VARCHAR(20) NOT NULL UNIQUE,
    username VARCHAR(50),
    real_name VARCHAR(50),
    sno VARCHAR(20),
    dormitory VARCHAR(50),
    gender VARCHAR(10),
    avatar VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- 密码表（单独表，也可合并）
CREATE TABLE IF NOT EXISTS user_password (
                                             id INT AUTO_INCREMENT PRIMARY KEY,
                                             uid INT NOT NULL,
                                             password_hash VARCHAR(255) NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uid) REFERENCES user_account(id) ON DELETE CASCADE
    );

-- 分类表
CREATE TABLE IF NOT EXISTS category (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL
    );

-- 商品表
CREATE TABLE IF NOT EXISTS product (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    `level` INT DEFAULT 5,
    remark VARCHAR(1024),
    sort INT DEFAULT 0,
    count INT DEFAULT 1,
    display TINYINT DEFAULT 1,
    transaction_type INT DEFAULT 0,
    sales INT DEFAULT 0,
    uid INT,
    image VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (uid) REFERENCES user_account(id) ON DELETE SET NULL
    );

-- 留言表
CREATE TABLE IF NOT EXISTS message (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       sid INT NOT NULL,
                                       uid INT NOT NULL,
                                       content VARCHAR(512),
    display TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sid) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (uid) REFERENCES user_account(id) ON DELETE CASCADE
    );

-- 购物车
CREATE TABLE IF NOT EXISTS cart_item (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         uid INT NOT NULL,
                                         sid INT NOT NULL,
                                         quantity INT DEFAULT 1,
                                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                         FOREIGN KEY (uid) REFERENCES user_account(id) ON DELETE CASCADE,
    FOREIGN KEY (sid) REFERENCES product(id) ON DELETE CASCADE
    );
