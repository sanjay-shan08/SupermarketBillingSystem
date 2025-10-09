import java.util.*;

public class SupermarketBillingSystem 
{
    static class Product 
    {
        String name;
        double price;

        Product(String name, double price) 
        {
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() 
        {
            return name;
        }

       
        @Override
        public boolean equals(Object o) 
        {
            if (this == o) return true;
            if (!(o instanceof Product)) return false;
            Product p = (Product) o;
            return Objects.equals(name, p.name) && Double.compare(price, p.price) == 0;
        }

        @Override
        public int hashCode() 
        {
            return Objects.hash(name, price);
        }
    }

    private final Map<Integer, Product> productCatalog = new HashMap<>();
    private final Map<Product, Integer> cart = new LinkedHashMap<>();
    private final double TAX_RATE = 0.05; // 5% tax

    public SupermarketBillingSystem() 
    {
        productCatalog.put(1, new Product("Milk", 24.50));
        productCatalog.put(2, new Product("Bread", 15.00));
        productCatalog.put(3, new Product("Eggs (dozen)", 60.00));
        productCatalog.put(4, new Product("Butter", 40.00));
        productCatalog.put(5, new Product("Rice (1kg)", 50.00));
    }

    void displayProducts() 
    {
        System.out.println("Available Products:");
        for (Map.Entry<Integer, Product> entry : productCatalog.entrySet()) 
        {
            System.out.printf("%d. %s - ₹%.2f%n", entry.getKey(), entry.getValue().name, entry.getValue().price);
        }
    }

    void addToCart(int productId, int quantity) 
    {
        Product product = productCatalog.get(productId);
        if (product != null) 
        {
            if (quantity <= 0) 
            {
                System.out.println("Quantity must be positive.");
                return;
            }
            cart.put(product, cart.getOrDefault(product, 0) + quantity);
            System.out.printf("Added %d x %s to cart.%n", quantity, product.name);
        } 
        else 
        {
            System.out.println("Invalid product ID.");
        }
    }

    void generateBill() 
    {
        System.out.println("\n------ Invoice ------");
        double subtotal = 0;
        for (Map.Entry<Product, Integer> entry : cart.entrySet()) 
        {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double totalPrice = product.price * quantity;
            subtotal += totalPrice;
            System.out.printf("%s x %d = ₹%.2f%n", product.name, quantity, totalPrice);
        }
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;
        System.out.printf("Subtotal: ₹%.2f%n", subtotal);
        System.out.printf("Tax (5%%): ₹%.2f%n", tax);
        System.out.printf("Total: ₹%.2f%n", total);
        System.out.println("--------------------");
    }

    public static void main(String[] args) 
    {
        Scanner sc = new Scanner(System.in);
        SupermarketBillingSystem system = new SupermarketBillingSystem();

        while (true) 
        {
            system.displayProducts();
            System.out.print("Enter product ID to add to cart (or 0 to checkout): ");
            if (!sc.hasNextInt()) 
            {
                System.out.println("Please enter a valid integer.");
                sc.next(); 
                continue;
            }
            int productId = sc.nextInt();
            if (productId == 0) 
            {
                break;
            }
            System.out.print("Enter quantity: ");
            if (!sc.hasNextInt()) 
            {
                System.out.println("Please enter a valid integer for quantity.");
                sc.next(); 
                continue;
            }
            int quantity = sc.nextInt();
            system.addToCart(productId, quantity);
        }

        system.generateBill();
        sc.close();
    }
}