import java.sql.*;

public class DatabaseHelper {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/imageEncryptionDB";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "password";

    // Save metadata (filename and encryption key) into the database
    public static void saveMetadata(String filename, String encryptionKey) throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        String query = "INSERT INTO image_metadata (filename, encryption_key) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, filename);
        statement.setString(2, encryptionKey);
        statement.executeUpdate();
        statement.close();
        connection.close();
        System.out.println("Metadata saved to the database.");
    }

    // Retrieve encryption key for a given file
    public static String getEncryptionKey(String filename) throws SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        String query = "SELECT encryption_key FROM image_metadata WHERE filename = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, filename);
        ResultSet resultSet = statement.executeQuery();
        
        String encryptionKey = null;
        if (resultSet.next()) {
            encryptionKey = resultSet.getString("encryption_key");
        }

        resultSet.close();
        statement.close();
        connection.close();
        return encryptionKey;
    }
}
