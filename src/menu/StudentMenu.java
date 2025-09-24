package menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import db.DBConnection;

public class StudentMenu {

    public static void showStudentMenu() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        do {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    viewBooks();
                    break;
                case 2:
                    borrowBook(sc);
                    break;
                case 3:
                    returnBook(sc);
                    break;
                case 4:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 4);
    }

    // View all books
    private static void viewBooks() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM books");
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Available Books ---");
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

    // Borrow a book
    private static void borrowBook(Scanner sc) {
        System.out.print("Enter Book ID to borrow: ");
        int bookId = sc.nextInt();

        try (Connection conn = DBConnection.getConnection()) {
            // Check if book is available
            PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE id=?");
            check.setInt(1, bookId);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                boolean available = rs.getBoolean("available");
                if (!available) {
                    System.out.println("Sorry, this book is already borrowed.");
                    return;
                }
            } else {
                System.out.println("Book not found!");
                return;
            }

            // Update book as borrowed
            PreparedStatement ps = conn.prepareStatement("UPDATE books SET available=false WHERE id=?");
            ps.setInt(1, bookId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Book borrowed successfully!");
            } else {
                System.out.println("Failed to borrow the book.");
            }

        } catch (SQLException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    // Return a book
    private static void returnBook(Scanner sc) {
        System.out.print("Enter Book ID to return: ");
        int bookId = sc.nextInt();

        try (Connection conn = DBConnection.getConnection()) {
            // Check if book exists
            PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE id=?");
            check.setInt(1, bookId);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                boolean available = rs.getBoolean("available");
                if (available) {
                    System.out.println("This book is not borrowed.");
                    return;
                }
            } else {
                System.out.println("Book not found!");
                return;
            }

            // Update book as returned
            PreparedStatement ps = conn.prepareStatement("UPDATE books SET available=true WHERE id=?");
            ps.setInt(1, bookId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Book returned successfully!");
            } else {
                System.out.println("Failed to return the book.");
            }

        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }
}
