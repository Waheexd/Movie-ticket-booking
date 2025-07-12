import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

public class MovieSelectionPage extends JFrame {

    class Movie {
        int id;
        String title, releaseDate, genre, theatre, imagePath;

        Movie(int id, String title, String releaseDate, String genre, String theatre, String imagePath) {
            this.id = id;
            this.title = title;
            this.releaseDate = releaseDate;
            this.genre = genre;
            this.theatre = theatre;
            this.imagePath = imagePath;
        }
    }

    ArrayList<Movie> movieList = new ArrayList<>();
    final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    final String DB_USER = "root";
    final String DB_PASS = "";

    int userId;
    String username;

    public MovieSelectionPage(String username, int userId) {
        this.username = username;
        this.userId = userId;

        setTitle("Welcome " + username + " - Movie Booking App");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Welcome To Movies App", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(new Color(0, 120, 215)); // 🔵 Blue color
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        loadMoviesFromDB();

        JPanel movieGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        for (Movie movie : movieList) {
            movieGrid.add(createMovieCard(movie));
        }

        JScrollPane scrollPane = new JScrollPane(movieGrid);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadMoviesFromDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM movies");
            while (rs.next()) {
                movieList.add(new Movie(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("release_date"),
                    rs.getString("genre"),
                    rs.getString("theatre"),
                    rs.getString("image_path")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setBackground(Color.WHITE);

        ImageIcon icon = new ImageIcon(movie.imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(imageLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        infoPanel.add(new JLabel("Title: " + movie.title));
        infoPanel.add(new JLabel("Release: " + movie.releaseDate));
        infoPanel.add(new JLabel("Genre: " + movie.genre));
        infoPanel.add(new JLabel("Theatre: " + movie.theatre));
        JLabel priceLabel = new JLabel("Price: ₹250");
        priceLabel.setForeground(new Color(0, 128, 0)); // ✅ Green price text
        infoPanel.add(priceLabel);

        JButton bookButton = new JButton("Book Now");
        bookButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookButton.setBackground(new Color(0, 120, 215)); // ✅ Blue button
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setFont(new Font("Arial", Font.BOLD, 12));
        bookButton.addActionListener(e -> {
            new SeatBookingPage(movie.id, movie.title, userId).setVisible(true);
            dispose();
        });

        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(bookButton);

        card.add(infoPanel);
        return card;
    }
}
