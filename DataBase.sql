CREATE DATABASE imageEncryptionDB;

USE imageEncryptionDB;

CREATE TABLE image_metadata (
    id INT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255) NOT NULL,
    encryption_key TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
