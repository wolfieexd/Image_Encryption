import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ImageEncryption {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_SIZE = 256;
    private static final int SALT_LENGTH = 16;
    private static final int ITERATION_COUNT = 65536;
    private static final int IV_SIZE = 16;

    private static Connection dbConnection;

    static {
        initDatabaseConnection();
    }

    private static void initDatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/image_encryption";
            String user = "your_username";
            String password = "your_password";
            dbConnection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the database.");
        }
    }

    public static void encryptImage(File inputFile, File outputFile, String password) throws Exception {
        byte[] salt = generateSalt();
        byte[] iv = generateIV();

        SecretKey secretKey = deriveKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        FileInputStream inputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(salt);
        outputStream.write(iv);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

        // Log encryption in database
        String sql = "INSERT INTO encrypted_images (file_name, encrypted_file_path) VALUES (?, ?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
            pstmt.setString(1, inputFile.getName());
            pstmt.setString(2, outputFile.getAbsolutePath());
            pstmt.executeUpdate();
        }
    }

    public static void decryptImage(File inputFile, File outputFile, String password) throws Exception {
        FileInputStream inputStream = new FileInputStream(inputFile);

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_SIZE];
        inputStream.read(salt);
        inputStream.read(iv);

        SecretKey secretKey = deriveKeyFromPassword(password, salt);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] inputBytes = new byte[(int) inputFile.length() - SALT_LENGTH - IV_SIZE];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();
    }

    // Derive key from password using PBKDF2
    private static SecretKey deriveKeyFromPassword(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    // Generate a random salt
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // Generate a random IV
    private static byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}