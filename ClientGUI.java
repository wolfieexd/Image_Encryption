import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientGUI extends JFrame {
    private JTextField passwordField;
    private JButton chooseFileButton, encryptButton, sendButton;
    private JLabel fileNameLabel;
    private File selectedFile, encryptedFile;
    private Connection dbConnection;

    public ClientGUI() {
        // Set up the GUI window
        setTitle("🔒 Goofy Image Encryption Client 🎨");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));
        getContentPane().setBackground(Color.PINK);

        // Create UI components with goofy fonts and colors
        Font goofyFont = new Font("Comic Sans MS", Font.BOLD, 14);
        
        chooseFileButton = new JButton("🖼️ Choose Image");
        chooseFileButton.setFont(goofyFont);
        chooseFileButton.setBackground(Color.YELLOW);
        
        passwordField = new JTextField();
        passwordField.setFont(goofyFont);
        passwordField.setBackground(Color.CYAN);
        
        encryptButton = new JButton("🔐 Encrypt");
        encryptButton.setFont(goofyFont);
        encryptButton.setBackground(Color.GREEN);
        
        sendButton = new JButton("📤 Encrypt & Send");
        sendButton.setFont(goofyFont);
        sendButton.setBackground(Color.ORANGE);
        
        fileNameLabel = new JLabel("Selected File: None");
        fileNameLabel.setFont(goofyFont);
        fileNameLabel.setForeground(Color.BLUE);

        // Add components to the window
        add(new JLabel("Select Image:")).setForeground(Color.RED);
        add(chooseFileButton);
        add(fileNameLabel);
        add(new JLabel("Enter Password:")).setForeground(Color.RED);
        add(passwordField);
        add(encryptButton);
        add(sendButton);

        // Button listeners
        chooseFileButton.addActionListener(e -> selectFile());
        encryptButton.addActionListener(e -> {
            if (selectedFile != null && !passwordField.getText().isEmpty()) {
                encryptFile(selectedFile, passwordField.getText());
            } else {
                JOptionPane.showMessageDialog(null, "Please select a file and enter a password.");
            }
        });
        sendButton.addActionListener(e -> {
            if (encryptedFile != null) {
                sendFile(encryptedFile);
            } else {
                JOptionPane.showMessageDialog(null, "Please encrypt the file before sending.");
            }
        });

        // Initialize database connection
        initDatabaseConnection();

        // Show the window
        setVisible(true);
    }

    private void initDatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/image_encryption";
            String user = "root";
            String password = "root";
            dbConnection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
        }
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            encryptedFile = null;
            fileNameLabel.setText("Selected File: " + selectedFile.getName());
        }
    }

    private void encryptFile(File file, String password) {
        try {
            encryptedFile = new File("client_encrypted_image.enc");
            ImageEncryption.encryptImage(file, encryptedFile, password);
            JOptionPane.showMessageDialog(null, "File encrypted successfully!");
            
            // Store encryption info in database
            String sql = "INSERT INTO encrypted_images (file_name, encrypted_file_path) VALUES (?, ?)";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                pstmt.setString(1, file.getName());
                pstmt.setString(2, encryptedFile.getAbsolutePath());
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error during encryption.");
        }
    }

    private void sendFile(File file) {
        // ... (keep the existing sendFile method)
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}