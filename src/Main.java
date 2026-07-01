//E-Commerce Order process System Using Java Core

import java.time.LocalDateTime;
import java.util.*;
import java.lang.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

enum OrderStatus {
    PENDING, PROCESSED, FAILED;
}

class InSufficientStockException extends Exception {
    public InSufficientStockException(String massage) {
        super(massage);
    }
}

class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String massage) {
        super(massage);
    }
}

class Order {
    private Integer id;
    private List<Product> items;
    private Double amount;
    private LocalDateTime time;
    private OrderStatus status;

    //constructor
    public Order(Integer id, Double amount, List<Product> items) {
        this.id = id;
        this.items = items;
        this.amount = amount;
        this.time = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    private double calculateTotal() {
        double total = 0.0;
        for (Product p : items) {
            total += p.getPrice();
        }
        return total;
    }

    //getter and setter method
    public Integer getID() {
        return id;
    }

    public List<Product> getItems() {
        return items;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}

abstract class Product {
    private Integer id;
    private String name;
    private Double price;
    private Integer stock;

    public Product(Integer id, String name, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}

class Electronics extends Product {
    private Integer WarrantyMonth;

    public Electronics(Integer id, String name, Double price, Integer stock, Integer WarrantyMonth) {
        super(id, name, price, stock);
        this.WarrantyMonth = WarrantyMonth;
    }

}

class Clothing extends Product {
    private String size;

    public Clothing(Integer id, String name, Double price, Integer stock, String size) {
        super(id, name, price, stock);
        this.size = size;
    }

}

interface OrderProcessor {
    void process(Order order) throws InSufficientStockException;
}

class DefaultOrderProcessor implements OrderProcessor {
    @Override
    public void process(Order order) throws InSufficientStockException {
        System.out.println("\n [Worker Thread"+Thread.currentThread().getName()+"] Prcessing Order"+order.getID());

        if (order.getItems() == null || order.getItems().isEmpty()) {
            order.setStatus(OrderStatus.FAILED);
            throw new InvalidOrderException("order validation failed :cart cannot be empty");
        }
        for (Product product : order.getItems()) {
            synchronized(product){
            if (product.getStock() <= 0) {
                order.setStatus(OrderStatus.FAILED);
                throw new InSufficientStockException("Stock error: '" + product.getName() + "' is out of stock!");
            }
            }
        }
        for (Product product : order.getItems()) {
            product.setStock(product.getStock() - 1);
            System.out.println("Deducted Stocks for" + product.getName() + "remaining" + product.getStock());
        }
        order.setStatus(OrderStatus.PROCESSED);
        System.out.println("Order Status updated to: " + order.getStatus());
        System.out.println("--------------------------------");

    }
}
class OrderPlacer implements Runnable{
    private final BlockingQueue<Order> orderQueue;
    private final Map<Integer,Product> sharedInventory;
    private final  int coustomerId;
    public OrderPlacer(BlockingQueue<Order> orderQueue,Map<Integer,Product> sharedInventory,int coustomerId){
          this.orderQueue=orderQueue;
          this.sharedInventory=sharedInventory;
          this.coustomerId=coustomerId;
    }

    @Override
    public void run() {
        try {
            System.out.println("👤 Customer Thread #" + coustomerId + " started browsing...");

            // Simulating customer thinking/browsing time
            Thread.sleep((long) (Math.random() * 1000));

            // Grab item from shared inventory safely to create a dynamic order
            List<Product> items = new ArrayList<>();
            Product product = sharedInventory.get(1); // Look up item ID 1

            if (product != null) {
                items.add(product);

                // Construct a randomized Order payload
                int randomOrderId = (int) (Math.random() * 9000) + 1000;
                Order newOrder = new Order(randomOrderId, product.getPrice(), items);

                System.out.println("🛒 Customer #" + coustomerId + " placed Order #" + randomOrderId + " into the queue.");

                // Add order to the thread-safe shared queue
                orderQueue.put(newOrder);
            }
        } catch (InterruptedException e) {
            System.out.println("Customer Thread #" + coustomerId + " was interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}


public class Main {
    private static Map<Integer, Product> inventory = new HashMap<>();
    private static List<Order> orderhistory = Collections.synchronizedList(new ArrayList<>());
    private static BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
    public static Optional<Product> findProductById(Integer Id) {
        Product foundProduct = inventory.get(Id);
        return Optional.ofNullable(foundProduct);

    }

    public static void genrateAnalyticsReport() {
        System.out.println("\n====================================");
        System.out.println("E-Commerce Real Time Metrics");
        System.out.println("=======================================");
        double totalRevenue = orderhistory.stream()
                .filter(order -> order.getStatus() == OrderStatus.PROCESSED)
                .mapToDouble(Order::getAmount)
                .sum();
        System.out.println("Total Revenue $" + totalRevenue);
        System.out.println("=========================");

        Map<OrderStatus, Long> statusCount = orderhistory.stream()
                .collect(Collectors.groupingBy(Order::getStatus,
                        Collectors.counting()));
        System.out.println("-------Order Status Breaakdown---------");
        statusCount.forEach((status, count) ->
                System.out.println(" * " + status + ": " + count + " order(s)"));
        long totalProductSold =orderhistory.stream()
                .filter(order->order.getStatus()==OrderStatus.PROCESSED)
                .flatMap(order->order.getItems().stream())
                .count();
        System.out.println("\nTotal individual items sold across store: " + totalProductSold);
        System.out.println("========================================\n");
    }


    public static void main(String[] args) {
        Product laptop = new Electronics(1, "Acer Laptop", 500.0, 2, 24); // Only 2 in stock!
        inventory.put(laptop.getId(), laptop);

        System.out.println("--- Booting Backend Processing Engine ---");

        // 2. Start 3 Customer Threads to drop orders into the queue concurrently
        for (int i = 1; i <= 3; i++) {
            new Thread(new OrderPlacer(orderQueue, inventory, i)).start();
        }

        // 3. Create an ExecutorService pool of 2 Consumer Worker Threads
        ExecutorService processingPool = Executors.newFixedThreadPool(2);
        OrderProcessor processor = new DefaultOrderProcessor();

        // Runnable task for consumer workers to process items from the queue
        Runnable fulfillmentTask = () -> {
            try {
                // Keep checking the queue for 3 seconds
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 3000) {
                    // Pull an order out of the queue (waits up to 500ms if empty)
                    Order incomingOrder = orderQueue.poll(500, TimeUnit.MILLISECONDS);

                    if (incomingOrder != null) {
                        try {
                            processor.process(incomingOrder);
                            orderhistory.add(incomingOrder);
                        } catch (InSufficientStockException e) {
                            System.out.println("❌ Order " + incomingOrder.getID() + " Processing Blocked: " + e.getMessage());
                            orderhistory.add(incomingOrder); // Track failed orders too
                        } catch (Exception e) {
                            System.out.println("⚠️ Unexpected Error: " + e.getMessage());
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // Submit fulfillment tasks to both worker threads in the pool
        processingPool.submit(fulfillmentTask);
        processingPool.submit(fulfillmentTask);

        // 4. Clean Shutdown Sequence
        try {
            processingPool.shutdown();
            // Wait up to 5 seconds for background workers to wrap up
            if (processingPool.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("\n--- All orders handled. Workers shutting down cleanly. ---");
                // Run final Day 8 stream metrics!
                genrateAnalyticsReport();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}