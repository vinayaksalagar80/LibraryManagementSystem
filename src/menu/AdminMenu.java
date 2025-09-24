package menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import db.DBConnection;

public class AdminMenu {

    public static void showAdminMenu() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        do {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. View All Books");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addBook(sc);
                    break;
                case 2:
                    removeBook(sc);
                    break;
                case 3:
                    viewBooks();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 4);
    }

    // Add a book
    private static void addBook(Scanner sc) {
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author Name: ");
        String author = sc.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO books (title, author, available) VALUES (?, ?, true)");
            ps.setString(1, title);
            ps.setString(2, author);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Book added successfully!");
            } else {
                System.out.println("Failed to add the book.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    // Remove a book
    private static void removeBook(Scanner sc) {
        System.out.print("Enter Book ID to remove: ");
        int bookId = sc.nextInt();

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?");
            ps.setInt(1, bookId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Book removed successfully!");
            } else {
                System.out.println("Book not found or failed to remove.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
    }

    // View all books (Admin can see all books)
    private static void viewBooks() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM books");
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- All Books ---");
            System.out.printf("%-5s %-30s %-20s %-10s\n", "ID", "Title", "Author", "Available");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean available = rs.getBoolean("available");
                System.out.printf("%-5d %-30s %-20s %-10s\n", id, title, author, available ? "Yes" : "No");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
    }
}
