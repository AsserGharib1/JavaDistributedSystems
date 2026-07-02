import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class Male implements Runnable {
    private final Socket cSocket;
    private static final ArrayList<Product> Product = new ArrayList<>();

    static {
        Product.add(new Product("M100000", 'M', 500, 8));
        Product.add(new Product("M100001", 'L', 650, 5));
        Product.add(new Product("M100002",'S', 800, 10));
        Product.add(new Product("M100003", 'M', 1200, 8));
        Product.add(new Product("M100004", 'L', 3000, 5));
        Product.add(new Product("M100005", 'S', 2300, 10));
    }
    public Male(Socket cSocket) {
        this.cSocket = cSocket;
    }
    @Override
    public void run() {
        try (
            ObjectOutputStream output = new ObjectOutputStream(cSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(cSocket.getInputStream())
        ) {
            String clientName = input.readUTF().trim();
            String barcode = input.readUTF().trim();
            System.out.println(clientName + " requested barcode: " + barcode + " from Men's Warehouse");

            Product product = searchItem(barcode);

            if (product != null) {
                product.updateStock(1);
                System.out.println(clientName + " purchased " + barcode + ". New stock: " + product.getStock());
            } else {
                System.out.println(clientName + " requested an Product that was not found.");
            }
            output.writeObject(product);
            output.flush();
        } catch (Exception e) {
            System.out.println("Male Error: " + e.getMessage());
        }
    }

    private Product searchItem(String barcode) {
        return Product.stream().filter(i -> i.getBarcode().equals(barcode)).findFirst().orElse(null);
    }

    public static void main(String[] args) throws IOException {
        ServerSocket Male = new ServerSocket(3000);
        System.out.println("Men's Warehouse listening on port 3000");
        while (true) {
            Socket clientSocket = Male.accept();
            new Thread(new Male(clientSocket)).start();
        }
    }
}