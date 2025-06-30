import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

public class MedicalStoreApp {
    public static void main(String[] args) {
        new LoginPage();
    }
}

// Database connection helper
class DBHelper {
    static final String DB_URL = "jdbc:mysql://localhost:3306/medical_store";
    static final String USER = "root";
    static final String PASS = "tiger";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}

// Login Page
class LoginPage extends JFrame {
    JButton adminButton, userButton;

    LoginPage() {
        setTitle("Medical Store Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));

        JLabel title = new JLabel("Medical Store", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title);

        adminButton = new JButton("Admin Login");
        userButton = new JButton("Customer Login");

        add(adminButton);
        add(userButton);

        adminButton.addActionListener(e -> {
            dispose();
            new AdminLoginPage();
        });

        userButton.addActionListener(e -> {
            dispose();
            new UserLoginPage();
        });

        setVisible(true);
    }
}

// Admin Login Page
class AdminLoginPage extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton, backButton;

    AdminLoginPage() {
        setTitle("Admin Login");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        backButton = new JButton("Back");
        add(loginButton);
        add(backButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            if (username.equals("admin") && password.equals("admin")) {
                JOptionPane.showMessageDialog(this, "Admin login successful!");
                dispose();
                new AdminDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        setVisible(true);
    }
}

// Admin Dashboard
class AdminDashboard extends JFrame {
    JButton addButton, updateButton, deleteButton, searchButton, viewButton, logoutButton;

    AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 1, 10, 10));

        addButton = new JButton("Add Medicine");
        updateButton = new JButton("Update Medicine");
        deleteButton = new JButton("Delete Medicine");
        searchButton = new JButton("Search Medicine");
        viewButton = new JButton("View All Medicines");
        logoutButton = new JButton("Logout");

        add(addButton);
        add(updateButton);
        add(deleteButton);
        add(searchButton);
        add(viewButton);
        add(logoutButton);

        addButton.addActionListener(e -> {
            dispose();
            new AddMedicinePage();
        });

        updateButton.addActionListener(e -> {
            dispose();
            new UpdateMedicinePage();
        });

        deleteButton.addActionListener(e -> {
            dispose();
            new DeleteMedicinePage();
        });

        searchButton.addActionListener(e -> {
            dispose();
            new SearchMedicinePage();
        });

        viewButton.addActionListener(e -> {
            dispose();
            new ViewMedicinesPage();
        });

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        setVisible(true);
    }
}

// Add Medicine Page
class AddMedicinePage extends JFrame {
    JTextField nameField, manufacturerField, priceField, quantityField;
    JButton addButton, backButton;

    AddMedicinePage() {
        setTitle("Add Medicine");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Medicine Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Manufacturer:"));
        manufacturerField = new JTextField();
        add(manufacturerField);

        add(new JLabel("Price:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        add(quantityField);

        addButton = new JButton("Add Medicine");
        backButton = new JButton("Back");
        add(addButton);
        add(backButton);

        addButton.addActionListener(e -> addMedicine());
        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        setVisible(true);
    }

    private void addMedicine() {
        String name = nameField.getText();
        String manufacturer = manufacturerField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        try (Connection con = DBHelper.getConnection()) {
            String sql = "INSERT INTO medicines (name, manufacturer, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, manufacturer);
            pst.setDouble(3, price);
            pst.setInt(4, quantity);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Medicine added successfully!");
            nameField.setText("");
            manufacturerField.setText("");
            priceField.setText("");
            quantityField.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding medicine.");
        }
    }
}

// User Login / Registration Page
class UserLoginPage extends JFrame {
    JTextField usernameField;
    JButton loginButton, registerButton, backButton;

    UserLoginPage() {
        setTitle("Customer Login / Register");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Enter Username:"));
        usernameField = new JTextField();
        add(usernameField);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        backButton = new JButton("Back");

        add(loginButton);
        add(registerButton);
        add(backButton);

        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        setVisible(true);
    }

    private void loginUser() {
        String username = usernameField.getText();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                JOptionPane.showMessageDialog(this, "Login successful! Welcome " + username);
                dispose();
                new CustomerDashboard(userId, username);
            } else {
                JOptionPane.showMessageDialog(this, "User not found. Please register first.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during login.");
        }
    }

    private void registerUser() {
        String username = usernameField.getText();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "INSERT INTO users (username) VALUES (?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during registration.");
        }
    }
}

// Customer Dashboard
class CustomerDashboard extends JFrame {
    int userId;
    String username;
    JButton browseButton, cartButton, orderHistoryButton, logoutButton;
    static ArrayList<CartItem> cart = new ArrayList<>();

    CustomerDashboard(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Customer Dashboard - " + username);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        browseButton = new JButton("Browse & Search Medicines");
        cartButton = new JButton("View Cart & Checkout");
        orderHistoryButton = new JButton("View Order History");
        logoutButton = new JButton("Logout");

        add(browseButton);
        add(cartButton);
        add(orderHistoryButton);
        add(logoutButton);

        browseButton.addActionListener(e -> {
            dispose();
            new BrowseMedicinesPage(userId, username);
        });

        cartButton.addActionListener(e -> {
            dispose();
            new CartPage(userId, username);
        });

        orderHistoryButton.addActionListener(e -> {
            dispose();
            new OrderHistoryPage(userId, username);
        });

        logoutButton.addActionListener(e -> {
            cart.clear();
            dispose();
            new LoginPage();
        });

        setVisible(true);
    }
}

// Cart Item
class CartItem {
    int medicineId;
    String name;
    double price;
    int quantity;

    CartItem(int medicineId, String name, double price, int quantity) {
        this.medicineId = medicineId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}

// Update Medicine Page
class UpdateMedicinePage extends JFrame {
    JTextField idField, priceField, quantityField;
    JButton updateButton, backButton;

    UpdateMedicinePage() {
        setTitle("Update Medicine");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Medicine ID:"));
        idField = new JTextField();
        add(idField);

        add(new JLabel("New Price:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("New Quantity:"));
        quantityField = new JTextField();
        add(quantityField);

        updateButton = new JButton("Update");
        backButton = new JButton("Back");
        add(updateButton);
        add(backButton);

        updateButton.addActionListener(e -> updateMedicine());
        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        setVisible(true);
    }

    private void updateMedicine() {
        int id = Integer.parseInt(idField.getText());
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        try (Connection con = DBHelper.getConnection()) {
            String sql = "UPDATE medicines SET price=?, quantity=? WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setDouble(1, price);
            pst.setInt(2, quantity);
            pst.setInt(3, id);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Medicine updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Medicine not found!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating medicine.");
        }
    }
}

// Delete Medicine Page
class DeleteMedicinePage extends JFrame {
    JTextField idField;
    JButton deleteButton, backButton;

    DeleteMedicinePage() {
        setTitle("Delete Medicine");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 2, 10, 10));

        add(new JLabel("Medicine ID:"));
        idField = new JTextField();
        add(idField);

        deleteButton = new JButton("Delete");
        backButton = new JButton("Back");
        add(deleteButton);
        add(backButton);

        deleteButton.addActionListener(e -> deleteMedicine());
        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        setVisible(true);
    }

    private void deleteMedicine() {
        int id = Integer.parseInt(idField.getText());

        try (Connection con = DBHelper.getConnection()) {
            String sql = "DELETE FROM medicines WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Medicine deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Medicine not found!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting medicine.");
        }
    }
}

// Search Medicine Page
class SearchMedicinePage extends JFrame {
    JTextField nameField;
    JButton searchButton, backButton;
    JTextArea resultArea;

    SearchMedicinePage() {
        setTitle("Search Medicine");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 3, 10, 10));
        nameField = new JTextField();
        searchButton = new JButton("Search");
        backButton = new JButton("Back");
        topPanel.add(new JLabel("Medicine Name:"));
        topPanel.add(nameField);
        topPanel.add(searchButton);
        topPanel.add(backButton);

        add(topPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchMedicine());
        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        setVisible(true);
    }

    private void searchMedicine() {
        String name = nameField.getText();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT * FROM medicines WHERE name LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + name + "%");
            ResultSet rs = pst.executeQuery();
            resultArea.setText("");
            while (rs.next()) {
                resultArea.append(
                    "ID: " + rs.getInt("id") + 
                    ", Name: " + rs.getString("name") + 
                    ", Manufacturer: " + rs.getString("manufacturer") +
                    ", Price: " + rs.getDouble("price") + 
                    ", Quantity: " + rs.getInt("quantity") + "\n"
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching medicines.");
        }
    }
}

// View All Medicines Page
class ViewMedicinesPage extends JFrame {
    JTextArea resultArea;
    JButton backButton;

    ViewMedicinesPage() {
        setTitle("View All Medicines");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        resultArea = new JTextArea();
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        backButton = new JButton("Back");
        add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            dispose();
            new AdminDashboard();
        });

        loadMedicines();
        setVisible(true);
    }

    private void loadMedicines() {
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT * FROM medicines";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            resultArea.setText("");
            while (rs.next()) {
                resultArea.append(
                    "ID: " + rs.getInt("id") + 
                    ", Name: " + rs.getString("name") + 
                    ", Manufacturer: " + rs.getString("manufacturer") +
                    ", Price: " + rs.getDouble("price") + 
                    ", Quantity: " + rs.getInt("quantity") + "\n"
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading medicines.");
        }
    }
}

// Browse Medicines (For Customer)
class BrowseMedicinesPage extends JFrame {
    JTextField searchField;
    JButton searchButton, addButton, backButton;
    JTextArea resultArea;
    int userId;
    String username;
    ArrayList<CartItem> searchResults = new ArrayList<>();

    BrowseMedicinesPage(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Browse & Search Medicines");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        searchField = new JTextField();
        searchButton = new JButton("Search");
        addButton = new JButton("Add to Cart");
        backButton = new JButton("Back");
        topPanel.add(new JLabel("Medicine Name:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addButton);
        topPanel.add(backButton);

        add(topPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchMedicines());
        addButton.addActionListener(e -> addToCart());
        backButton.addActionListener(e -> {
            dispose();
            new CustomerDashboard(userId, username);
        });

        setVisible(true);
    }

    private void searchMedicines() {
        String keyword = searchField.getText();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT * FROM medicines WHERE name LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            searchResults.clear();
            resultArea.setText("");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                searchResults.add(new CartItem(id, name, price, quantity));

                resultArea.append(
                    "ID: " + id + 
                    ", Name: " + name +
                    ", Price: " + price +
                    ", Available: " + quantity + "\n"
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching medicines.");
        }
    }

    private void addToCart() {
        String inputId = JOptionPane.showInputDialog(this, "Enter Medicine ID to add to cart:");
        int medicineId = Integer.parseInt(inputId);

        for (CartItem item : searchResults) {
            if (item.medicineId == medicineId) {
                CustomerDashboard.cart.add(new CartItem(item.medicineId, item.name, item.price, 1));
                JOptionPane.showMessageDialog(this, "Added to cart!");
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Invalid Medicine ID.");
    }
}

// View Cart and Place Order
class CartPage extends JFrame {
    JTextArea cartArea;
    JButton placeOrderButton, backButton;
    int userId;
    String username;

    CartPage(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Shopping Cart");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        cartArea = new JTextArea();
        add(new JScrollPane(cartArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        placeOrderButton = new JButton("Place Order");
        backButton = new JButton("Back");
        bottomPanel.add(placeOrderButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        placeOrderButton.addActionListener(e -> placeOrder());
        backButton.addActionListener(e -> {
            dispose();
            new CustomerDashboard(userId, username);
        });

        displayCart();
        setVisible(true);
    }

    private void displayCart() {
        cartArea.setText("");
        for (CartItem item : CustomerDashboard.cart) {
            cartArea.append(
                "ID: " + item.medicineId +
                ", Name: " + item.name +
                ", Price: " + item.price +
                ", Quantity: " + item.quantity + "\n"
            );
        }
    }

    private void placeOrder() {
        if (CustomerDashboard.cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        String contact = JOptionPane.showInputDialog(this, "Enter Contact Number:");
        String address = JOptionPane.showInputDialog(this, "Enter Delivery Address:");

        try (Connection con = DBHelper.getConnection()) {
            for (CartItem item : CustomerDashboard.cart) {
                String sql = "INSERT INTO orders (user_id, medicine_id, quantity, status) VALUES (?, ?, ?, 'Placed')";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, userId);
                pst.setInt(2, item.medicineId);
                pst.setInt(3, item.quantity);
                pst.executeUpdate();

                // Decrease medicine quantity from stock
                String updateSql = "UPDATE medicines SET quantity = quantity - ? WHERE id = ?";
                PreparedStatement pstUpdate = con.prepareStatement(updateSql);
                pstUpdate.setInt(1, item.quantity);
                pstUpdate.setInt(2, item.medicineId);
                pstUpdate.executeUpdate();
            }
            CustomerDashboard.cart.clear();
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
            dispose();
            new CustomerDashboard(userId, username);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error placing order.");
        }
    }
}

// Order History Page
class OrderHistoryPage extends JFrame {
    JTextArea historyArea;
    JButton backButton;
    int userId;
    String username;

    OrderHistoryPage(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Order History");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        historyArea = new JTextArea();
        add(new JScrollPane(historyArea), BorderLayout.CENTER);

        backButton = new JButton("Back");
        add(backButton, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            dispose();
            new CustomerDashboard(userId, username);
        });

        loadOrderHistory();
        setVisible(true);
    }

    private void loadOrderHistory() {
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT o.id, m.name, o.quantity, o.status FROM orders o JOIN medicines m ON o.medicine_id = m.id WHERE o.user_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            historyArea.setText("");
            while (rs.next()) {
                historyArea.append(
                    "Order ID: " + rs.getInt("id") +
                    ", Medicine: " + rs.getString("name") +
                    ", Quantity: " + rs.getInt("quantity") +
                    ", Status: " + rs.getString("status") + "\n"
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order history.");
        }
    }
}
