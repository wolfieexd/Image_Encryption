import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class ImageDecryptor {
    private static JLabel fileNameLabel;
    private static Connection dbConnection;

    public static void main(String[] args) {
        initDatabaseConnection();

        JFrame frame = new JFrame("🔓 Goofy Image Decryptor 🎭");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new FlowLayout());
        frame.getContentPane().setBackground(Color.MAGENTA);

        Font goofyFont = new Font("Comic Sans MS", Font.BOLD, 14);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select encrypted image file");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File encryptedFile = fileChooser.getSelectedFile();
            fileNameLabel = new JLabel("Selected file: " + encryptedFile.getName());
            fileNameLabel.setFont(goofyFont);
            fileNameLabel.setForeground(Color.GREEN);
            frame.add(fileNameLabel);

            String password = JOptionPane.showInputDialog("Enter decryption password:");

            if (password != null && !password.isEmpty()) {
                try {
                    File decryptedFile = new File(encryptedFile.getParent(),
                            "decrypted_" + encryptedFile.getName().replace(".enc", ".jpg"));
                    ImageEncryption.decryptImage(encryptedFile, decryptedFile, password);
                    JOptionPane.showMessageDialog(frame,
                            "Image decrypted successfully!\nSaved as: " + decryptedFile.getAbsolutePath());

                    // Store decryption attempt in database
                    String sql = "INSERT INTO decryption_attempts (file_name, success) VALUES (?, ?)";
                    try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                        pstmt.setString(1, encryptedFile.getName());
                        pstmt.setBoolean(2, true);
                        pstmt.executeUpdate();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Error during decryption. Make sure the password is correct.");
                    // Store failed decryption attempt in database
                    String sql = "INSERT INTO decryption_attempts (file_name, success) VALUES (?, ?)";
                    try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                        pstmt.setString(1, encryptedFile.getName());
                        pstmt.setBoolean(2, false);
                        pstmt.executeUpdate();
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Decryption canceled. No password entered.");
            }
        }

        JButton viewHistoryButton = new JButton("📜 View Decryption History");
        viewHistoryButton.setFont(goofyFont);
        viewHistoryButton.setBackground(Color.YELLOW);
        viewHistoryButton.addActionListener(e -> viewDecryptionHistory(frame));
        frame.add(viewHistoryButton);

        frame.setVisible(true);
    }

    private static void initDatabaseConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/image_encryption";
            String user = "your_username";
            String password = "your_password";
            dbConnection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
        }
    }

    private static void viewDecryptionHistory(JFrame parentFrame) {
        JFrame historyFrame = new JFrame("🕰️ Decryption History");
        historyFrame.setSize(400, 300);
        historyFrame.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(listModel);
        historyList.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));

        try {
            String sql = "SELECT file_name, success, attempt_date FROM decryption_attempts ORDER BY attempt_date DESC";
            try (Statement stmt = dbConnection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String fileName = rs.getString("file_name");
                    boolean success = rs.getBoolean("success");
                    Timestamp attemptDate = rs.getTimestamp("attempt_date");
                    String status = success ? "✅ Success" : "❌ Failed";
                    listModel.addElement(String.format("%s - %s - %s", fileName, status, attemptDate));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Error retrieving decryption history.");
        }

        JScrollPane scrollPane = new JScrollPane(historyList);
        historyFrame.add(scrollPane, BorderLayout.CENTER);

        historyFrame.setVisible(true);
    }
}