//E-Commerce Order process System Using Java Core
import java.time.LocalDateTime;
import java.util.*;
import java.lang.*;
enum OrderStatus{
PENDING,PROCESSED,FAILED;
        }
class Order{
    private Integer id;
    private Double amount;
    private LocalDateTime time;
    private OrderStatus status;
//constructor
    public Order(Integer id,Double amount){
        this.id=id;
        this.amount=amount;
        this.time=LocalDateTime.now();
        this.status=OrderStatus.PENDING;
    }
    //getter and setter method
    public Integer getID(){return id;}
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

}

public class Main{
    public static void main(String[] args){
      //
    }
}