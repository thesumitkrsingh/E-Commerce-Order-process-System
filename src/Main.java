//E-Commerce Order process System Using Java Core
import java.time.LocalDateTime;
import java.util.*;
import java.lang.*;
enum OrderStatus{
PENDING,PROCESSED,FAILED;
        }
class InSufficientStockException extends Exception{
    public InSufficientStockException(String massage){
        super(massage);
    }
}
class InvalidOrderException extends RuntimeException{
    public InvalidOrderException(String massage){
        super(massage);
    }
}
class Order{
    private Integer id;
    private List<Product> items;
    private Double amount;
    private LocalDateTime time;
    private OrderStatus status;
//constructor
    public Order(Integer id,Double amount,List<Product> items){
        this.id=id;
        this.items=items;
        this.amount=amount;
        this.time=LocalDateTime.now();
        this.status=OrderStatus.PENDING;
    }
    private double calculateTotal(){
        double total=0.0;
        for(Product p:items){
            total+=p.getPrice();
        }
        return total;
    }
    //getter and setter method
    public Integer getID(){return id;}

    public List<Product> getItems() {
        return items;
    }

    public Double getAmount(){return amount;}
    public LocalDateTime getTime(){return time;}
    public OrderStatus getStatus(){ return status;}
    public void setStatus(OrderStatus status){this.status=status;}
}
abstract class Product{
    private Integer id;
    private String name;
    private Double price;
    private Integer stock;
    public Product(Integer id,String name,Double price,Integer stock){
        this.id=id;
        this.name=name;
        this.price=price;
        this.stock=stock;
    }
    public Integer getId(){return id;}
    public String getName(){return name;}
    public Double getPrice(){return price;}
    public Integer getStock(){return stock;}
    public void setStock(Integer stock){this.stock=stock;}
}
class Electronics extends Product{
    private Integer WarrantyMonth;
    public Electronics(Integer id,String name,Double price,Integer stock,Integer WarrantyMonth){
       super(id,name,price,stock);
       this.WarrantyMonth=WarrantyMonth;
    }

}
class Clothing extends Product{
    private String size;
    public Clothing(Integer id,String name,Double price,Integer stock,String size){
        super(id,name,price,stock);
        this.size=size;
    }

}
interface OrderProcessor{
  void process(Order order) throws InSufficientStockException;
}
class DefaultOrderProcessor implements OrderProcessor{
   @Override
    public void process(Order order) throws InSufficientStockException{
       System.out.println("---------Processing order System---------");
       if(order.getItems()==null||order.getItems().isEmpty()){
           order.setStatus(OrderStatus.FAILED);
           throw new InvalidOrderException("order validation failed :cart cannot be empty");
       }
       for(Product product: order.getItems()){
           if(product.getStock()<=0){
               order.setStatus(OrderStatus.FAILED);
               throw new InSufficientStockException("Stock error: '" + product.getName() + "' is out of stock!");
           }
       }
       for(Product product: order.getItems()){
           product.setStock(product.getStock()-1);
           System.out.println("Deducted Stocks for"+product.getName()+"remaining"+product.getStock());
       }
       order.setStatus(OrderStatus.PROCESSED);
       System.out.println("Order Status updated to: " + order.getStatus());
       System.out.println("--------------------------------");

   }
}


public class Main{
    private static Map<Integer,Product> inventory=new HashMap<>();
    private static List<Order> orderhistory=new ArrayList<>();
    public static void main(String[] args){
      Product laptop=new Electronics(1,"acer",99.0,12,24);
        Product lp=new Electronics(2,"HP",99.0,12,24);
                inventory.put(lp.getId(), laptop);
        inventory.put(laptop.getId(), laptop);
        System.out.println(inventory.size());
        List<Product> cartItem=new ArrayList<>();
        cartItem.add(inventory.get(1));
        cartItem.add(inventory.get(2));
        Order order1=new Order(101,99.0,cartItem);
        OrderProcessor processor = new DefaultOrderProcessor();
        try {
            processor.process(order1);
            if (order1.getStatus() == OrderStatus.PROCESSED) {
                orderhistory.add(order1);
            }
        }catch(InSufficientStockException e){
            System.out.println("order failed"+e.getMessage());
        }catch(InvalidOrderException e){
            System.out.println("Validation error"+e.getMessage());
        }finally {
            System.out.println(orderhistory.size());
        }

    }
}