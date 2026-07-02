
import java.io.Serializable;

public class Product implements Serializable {
    private final String barcode;
    private final char size;
    private final double price;
    private int stock;

    public Product(String barcode, char size, double price, int stock) {
        this.barcode = barcode;
        this.size = size;
        this.price = price;
        this.stock = stock;
    }

    public String getBarcode() { return barcode; }
    public char getSize() { return size; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void updateStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
        } else {
            System.out.println("Stock not sufficient for " + barcode);
        }
    }
    @Override
    public String toString() {
        return "Item{barcode:'" + barcode + "', size:" + size + ", pric:=" + price + ", stock:" + stock + "}";
    }

}
