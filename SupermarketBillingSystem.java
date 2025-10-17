import java.sql.*;
import java.util.Scanner;

public class SupermarketBillingSystem {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/YOUR_DATABASE";
        String user = "root";
        String password = "Meena@9496";
        Scanner sc = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // Display product list
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ProductID, ProductName, Price, StockQuantity FROM Products");

            System.out.println("Available Products:");
            while (rs.next()) {
                System.out.printf("%d. %s - ₹%.2f (Stock: %d)\n",
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("Price"),
                        rs.getInt("StockQuantity"));
            }

            // Select products for billing (simple cart logic)
            int invoiceId = (int)(System.currentTimeMillis() / 1000); // unique invoice
            double subtotal = 0;
            while (true) {
                System.out.print("Enter Product ID to add to cart (0 to finish): ");
                int pid = sc.nextInt();
                if (pid == 0) break;
                System.out.print("Enter quantity: ");
                int quantity = sc.nextInt();

                PreparedStatement pstmt = conn.prepareStatement(
                        "SELECT Price, StockQuantity FROM Products WHERE ProductID = ?");
                pstmt.setInt(1, pid);
                ResultSet prs = pstmt.executeQuery();
                if (!prs.next()) {
                    System.out.println("Invalid Product ID.");
                    continue;
                }
                double price = prs.getDouble("Price");
                int stock = prs.getInt("StockQuantity");
                if (quantity > stock) {
                    System.out.println("Not enough stock!");
                    continue;
                }

                // Add to invoice items
                PreparedStatement addInv = conn.prepareStatement(
                    "INSERT INTO InvoiceItems (InvoiceID, ProductID, Quantity, Price) VALUES (?, ?, ?, ?)");
                addInv.setInt(1, invoiceId);
                addInv.setInt(2, pid);
                addInv.setInt(3, quantity);
                addInv.setDouble(4, price);
                addInv.executeUpdate();

                subtotal += price * quantity;

                // Optional: Reduce stock quantity
                PreparedStatement updStock = conn.prepareStatement(
                    "UPDATE Products SET StockQuantity = StockQuantity - ? WHERE ProductID = ?");
                updStock.setInt(1, quantity);
                updStock.setInt(2, pid);
                updStock.executeUpdate();

                System.out.println("Added to cart.");
            }

            // Print Invoice
            double tax = subtotal * 0.05; // 5% tax
            double total = subtotal + tax;
            System.out.println("\n----- Invoice -----");
            PreparedStatement showInv = conn.prepareStatement(
                "SELECT Products.ProductName, InvoiceItems.Quantity, InvoiceItems.Price FROM InvoiceItems JOIN Products ON InvoiceItems.ProductID = Products.ProductID WHERE InvoiceID = ?");
            showInv.setInt(1, invoiceId);
            ResultSet irs = showInv.executeQuery();
            while (irs.next()) {
                String pname = irs.getString("ProductName");
                int qty = irs.getInt("Quantity");
                double price = irs.getDouble("Price");
                System.out.printf("%s x %d = ₹%.2f\n", pname, qty, price * qty);
            }
            System.out.printf("Subtotal: ₹%.2f\n", subtotal);
            System.out.printf("Tax (5%%): ₹%.2f\n", tax);
            System.out.printf("Total: ₹%.2f\n", total);
            System.out.println("------------------");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}