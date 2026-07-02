import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class IntermediateServer {
    private static final int PORT = 5000;
    private static final BlockingQueue<Socket> queue = new LinkedBlockingQueue<>();
    private static final Object mutex = new Object();

    public static void main(String[] args) throws IOException {
        ServerSocket coordinator = new ServerSocket(PORT);
        System.out.println("Intermediate Server is listening on port" + PORT);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        while (true) {
            Socket clientSocket = coordinator.accept();
            System.out.println("New client connected, added to queue.");
            queue.add(clientSocket);
            executor.execute(() -> processClient());
        }
    }

    private static void processClient() {
        Socket cSocket;
        synchronized (mutex) {
            cSocket = queue.poll(); 
        }
        
        if (cSocket == null) return;

        try (
            ObjectOutputStream output = new ObjectOutputStream(cSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(cSocket.getInputStream())
        ) {
            String clientName = input.readUTF().trim();
            String option = input.readUTF().trim().toLowerCase();
            System.out.println(clientName + " selected category: " + option);

            Socket warehouseSocket = null;

            switch (option) {
                case "male":
                    warehouseSocket = new Socket("localhost", 3000);
                    break;
                case "female":
                    warehouseSocket = new Socket("localhost", 4000);
                    break;
                default:
                    System.out.println(clientName + " selected an invalid category.");
                    output.writeObject(null);
                    output.flush();
                    return;
            }

            try (
                ObjectOutputStream warehouseOut = new ObjectOutputStream(warehouseSocket.getOutputStream());
                ObjectInputStream warehouseIn = new ObjectInputStream(warehouseSocket.getInputStream())
            ) {
                String barcode = input.readUTF().trim();
                System.out.println(clientName + " requested barcode: " + barcode);

                warehouseOut.writeUTF(clientName);
                warehouseOut.flush();
                warehouseOut.writeUTF(barcode);
                warehouseOut.flush();
                System.out.println(clientName + " barcode request sent to Warehouse");

                Object receivedObject = warehouseIn.readObject();
                Product product = (Product) receivedObject;

                output.writeObject(product);
                output.flush();

                if (product != null) {
                    System.out.println(clientName + " received Product: " + product);
                } else {
                    System.out.println(clientName + " requested an Product that was not found.");
                }
            }
            warehouseSocket.close();
            cSocket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}