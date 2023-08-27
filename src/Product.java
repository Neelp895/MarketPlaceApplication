import java.io.Serializable;

public class Product implements Serializable {
    private String product;
    private String store;  //is store seller name?
    private String description;
    private int quantity;
    private double price;
    private int quantitySold;


    //can you add variables for revenue
    public Product(String product, String store, String description,
                   int quantity, double price) {
        this.product = product;
        this.store = store;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }
    public String getProduct() {
        return product;
    }
    public String getStore() {
        return store;
    }
    public String getDescription() {
        return description;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getPrice() {
        return price;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantitySold(int quantitySold){
        this.quantitySold = quantitySold;
    }

    public String toString(){
        String output;

        output = product + ";" + store + ";";
        output = output + description + ";" + quantity + ";" + price + ";" +quantitySold + ";";

        return output;
    }

    public String toString(int quantityBought){
        String output;

        output = product + ";" + store + ";";
        output = output + description + ";" + quantityBought + ";" + price + ";";

        return output;
    }


}
