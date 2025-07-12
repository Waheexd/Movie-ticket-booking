import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SeatBookingPage extends JFrame {

    boolean[] seats = new boolean[20];
    String movieTitle;
    int movieId;
    int userId;
    List<Integer> selectedSeats = new ArrayList<>();
    JButton[] seatButtons = new JButton[20];
    JComboBox<String> showtimeComboBox;

    final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    final String DB_USER = "root";
    final String DB_PASS = "";

    public SeatBookingPage(int movieId, String movieTitle, int userId) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.userId = userId;
        setTitle("Book Seats - " + movieTitle);
        setSize(450, 400); // Reduced size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with title and combo box
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Select Showtime and Seats", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 144, 255)); // Light Blue
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        topPanel.add(titleLabel);

        JPanel comboPanel = new JPanel();
        comboPanel.add(new JLabel("Showtime:"));
        showtimeComboBox = new JComboBox<>(new String[]{"10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM"});
        showtimeComboBox.addActionListener(e -> loadBookedSeats());
        comboPanel.add(showtimeComboBox);
        topPanel.add(comboPanel);

        add(topPanel, BorderLayout.NORTH);

        // Seat grid
        JPanel seatPanel = new JPanel(new GridLayout(4, 5, 10, 10));
        for (int i = 0; i < 20; i++) {
            JButton btn = new JButton("Seat " + (i + 1));
            int index = i;
            seatButtons[i] = btn;
            btn.setBackground(Color.GREEN);

            btn.addActionListener(e -> {
                if (seats[index]) {
                    JOptionPane.showMessageDialog(this, "Seat already booked!");
                    return;
                }

                if (selectedSeats.contains(index)) {
                    selectedSeats.remove((Integer) index);
                    btn.setBackground(Color.GREEN);
                } else {
                    selectedSeats.add(index);
                    btn.setBackground(Color.YELLOW);
                }
            });

            seatPanel.add(btn);
        }
        add(seatPanel, BorderLayout.CENTER);

        // Confirm Booking button (blue & rectangular)
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBackground(new Color(0, 120, 215)); // Blue color
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setPreferredSize(new Dimension(180, 35));

        confirmButton.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one seat!");
                return;
            }
            String showtime = (String) showtimeComboBox.getSelectedItem();
            new Payment(this, movieId, movieTitle, selectedSeats, showtime, userId).setVisible(true);
            selectedSeats.clear();
            loadBookedSeats();
        });

        JPanel bottom = new JPanel();
        bottom.add(confirmButton);
        add(bottom, BorderLayout.SOUTH);

        loadBookedSeats();
    }

    private void loadBookedSeats() {
        Arrays.fill(seats, false);
        String showtime = (String) showtimeComboBox.getSelectedItem();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT seat_number FROM bookings WHERE movie_id = ? AND showtime = ?");
            stmt.setInt(1, movieId);
            stmt.setString(2, showtime);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int s = rs.getInt("seat_number");
                if (s >= 1 && s <= 20) {
                    seats[s - 1] = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < seatButtons.length; i++) {
            if (seats[i]) {
                seatButtons[i].setBackground(Color.RED);
            } else {
                seatButtons[i].setBackground(Color.GREEN);
            }
        }
    }
}
