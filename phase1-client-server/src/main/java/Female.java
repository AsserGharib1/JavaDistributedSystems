import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class Female implements Runnable {
    private final Socket cSocket;
    private static final ArrayList<Product> Product = new ArrayList<>();

    static {
        Product.add(new Product("F100000", 'M', 1200, 10));
        Product.add(new Product("F100001", 'L', 2500, 7));
        Product.add(new Product("F100002", 'S', 800, 15));
        Product.add(new Product("F100003", 'M', 1500, 12));
        Product.add(new Product("F100004", 'L', 1700, 8));
        Product.add(new Product("F100005", 'S', 900, 14));
    }

    public Female(Socket cSocket) {
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
            System.out.println(clientName + " requested barcode: " + barcode + " from Women's Warehouse");

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
            System.out.println("Female Error: " + e.getMessage());
        }
    }

    private Product searchItem(String barcode) {
        return Product.stream().filter(i -> i.getBarcode().equals(barcode)).findFirst().orElse(null);
    }

    public static void main(String[] args) throws IOException {
        ServerSocket Male = new ServerSocket(4000);
        System.out.println("Women's Warehouse listening on port 4000");

        while (true) {
            Socket clientSocket = Male.accept();
            new Thread(new Female(clientSocket)).start();
        }
    }
}