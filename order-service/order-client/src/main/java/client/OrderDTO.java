package client;



public class OrderDTO {

    private int customerId;
    private String itemId;
    
    @Override
    public String toString() {
        return "OrderDTO{" +
                       "customerId=" + customerId +
                       ", itemId='" + itemId + '\'' +
                       '}';
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public OrderDTO setCustomerId(int customerId) {
        this.customerId = customerId;
        return this;
    }
    
    public String getItemId() {
        return itemId;
    }
    
    public OrderDTO setItemId(String itemId) {
        this.itemId = itemId;
        return this;
    }
    
    public OrderDTO() {
    }
    
    public OrderDTO(int customerId, String itemId) {
        this.customerId = customerId;
        this.itemId = itemId;
    }
}
