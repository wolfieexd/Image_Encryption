# Image Encryption System

![Java](https://img.shields.io/badge/Language-Java-red)
![GUI](https://img.shields.io/badge/Frontend-Java%20Swing-blue)
![Database](https://img.shields.io/badge/Database-MySQL-blue)
![Encryption](https://img.shields.io/badge/Security-AES%20Encryption-brightgreen)
![License](https://img.shields.io/badge/License-Academic%20Use-lightgrey)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux-green)

This project implements a Java-based image encryption and transfer system. It includes GUI components for user interaction, secure encryption/decryption modules, and database integration to manage user data and encryption logs. The system ensures secure transmission of images using encryption and provides tools to view, encrypt, decrypt, and manage image files.

## 🚀 Features
- Java Swing GUI for client-side operations
- Secure AES-based image encryption and decryption
- Client-server architecture for image transfer
- MySQL database support with enhanced database helper
- Configuration via `config.properties`
- Modular design with reusable components (SecurityUtils, ImageTransfer, etc.)

## 📁 Project Structure
```
IMAGE ENCRYPTION/
├── Documentations
|   ├── Image Encryption Final Report.pdf
|   ├── REVIEW-1.pptx
|   ├── REVIEW-2.pptx
└── image encryption/
    ├── ClientGUI.java
    ├── EnhancedImageEncryption.java
    ├── ImageEncryption.java
    ├── ImageDecryptor.java
    ├── ImageTransferClient.java
    ├── ImageTransferServer.java
    ├── Server.java
    ├── EnhancedDatabaseHelper.java
    ├── config.properties
    ├── DataBase.sql
    ├── SecurityUtils.java
    ├── lib/
    │   ├── commons-dbcp2-2.13.0.jar
    │   ├── commons-io-2.11.0.jar
    │   └── commons-pool2-2.9.0.jar
    └── .vscode/settings.json
```

## ⚙️ How to Run

### 1. Set up the Database
- Import `DataBase.sql` into your MySQL database.
- Update credentials in `config.properties`.

### 2. Compile Java Files
Include the JAR files in the `lib` directory in your classpath.

Example (Linux/Mac):
```bash
javac -cp "lib/*" *.java
```

### 3. Run the Server
```bash
java -cp ".:lib/*" Server
```

### 4. Run the Client GUI
```bash
java -cp ".:lib/*" ClientGUI
```

## 📦 Dependencies
- Java 8 or above
- MySQL
- External Libraries:
  - commons-dbcp2-2.13.0
  - commons-io-2.11.0
  - commons-pool2-2.9.0

## 👨‍💻 Author
Sujan S    
🎓 SRM Institute of Science and Technology    
📧 [sujans1411@gmail.com]   
🔗 [Portfolio](https://wolfieexd.github.io/portfolio/)


## 📝 License
This project is for academic purposes and not licensed for commercial use.
