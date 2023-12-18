package orderservice;





public class Order {
    public Order() {
    }
    
    @Override
    public String toString() {
        return "Order{" +
                       "id=" + id +
                       ", customerId=" + customerId +
                       ", itemId='" + itemId + '\'' +
                       ", date='" + date + '\'' +
                       '}';
    }
    
    public Integer getId() {
        return id;
    }
    
    public Order setId(Integer id) {
        this.id = id;
        return this;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public Order setCustomerId(Integer customerId) {
        this.customerId = customerId;
        return this;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public Order setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }
    
    public String getDate() {
        return date;
    }
    
    public Order setDate(String date) {
        this.date = date;
        return this;
    }
    
    public Order(Integer id, Integer customerId, String itemId, String date) {
        this.id = id;
        this.customerId = customerId;
        this.itemId = itemId;
        this.date = date;
    }
    
    private Integer id;
    private Integer customerId;
    private String itemId;
    private String date;

}
