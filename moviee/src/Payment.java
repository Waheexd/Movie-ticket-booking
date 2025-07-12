import java.awt.*;
import java.sql.*;
import java.util.List;
import javax.swing.*;

public class Payment extends JDialog {

    final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    final String DB_USER = "root";
    final String DB_PASS = "";

    public Payment(JFrame parent, int movieId, String movieTitle, List<Integer> seatNumbers, String showtime, int userId) {
        super(parent, "Payment", true);
        setSize(400, 320);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        int pricePerSeat = 250;
        int totalAmount = seatNumbers.size() * pricePerSeat;

        StringBuilder seatDisplay = new StringBuilder();
        for (int seat : seatNumbers) {
            seatDisplay.append(seat + 1).append(", ");
        }
        String seatsStr = seatDisplay.substring(0, seatDisplay.length() - 2);

        JTextArea area = new JTextArea(
            "🎬 Movie: " + movieTitle + 
            "\n🕒 Showtime: " + showtime +
            "\n💺 Seats: " + seatsStr +
            "\n💰 Total: ₹" + totalAmount
        );
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(area, BorderLayout.CENTER);

        JButton payButton = new JButton("Pay ₹" + totalAmount);
        payButton.setFont(new Font("Arial", Font.BOLD, 16));
        payButton.setBackground(new Color(0, 123, 255));
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);

        payButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                conn.setAutoCommit(false); // Begin transaction

                // Insert each seat into bookings table
                PreparedStatement bookingStmt = conn.prepareStatement(
                    "INSERT INTO bookings (movie_id, movie_title, showtime, seat_number, user_id) VALUES (?, ?, ?, ?, ?)"
                );
                for (int seat : seatNumbers) {
                    bookingStmt.setInt(1, movieId);
                    bookingStmt.setString(2, movieTitle);
                    bookingStmt.setString(3, showtime);
                    bookingStmt.setInt(4, seat + 1);
                    bookingStmt.setInt(5, userId);
                    bookingStmt.addBatch();
                }
                bookingStmt.executeBatch();

                // Insert into payment table
                PreparedStatement paymentStmt = conn.prepareStatement(
                    "INSERT INTO payment (movie_id, movie_title, showtime, seat_number, price, user_id) VALUES (?, ?, ?, ?, ?, ?)"
                );
                paymentStmt.setInt(1, movieId);
                paymentStmt.setString(2, movieTitle);
                paymentStmt.setString(3, showtime);
                paymentStmt.setString(4, seatsStr);
                paymentStmt.setInt(5, totalAmount);
                paymentStmt.setInt(6, userId);
                paymentStmt.executeUpdate();

                conn.commit(); // Commit transaction

                JOptionPane.showMessageDialog(this,
                    "✅ Payment Successful!\n\n" +
                    "🎬 Movie: " + movieTitle +
                    "\n🕒 Showtime: " + showtime +
                    "\n💺 Seats: " + seatsStr +
                    "\n💰 Paid: ₹" + totalAmount,
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "❌ Payment Failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        buttonPanel.add(payButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
