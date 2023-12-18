package client;




public class OrderResponse {
    public int getOrderId() {
        return orderId;
    }
    
    @Override
    public String toString() {
        return "OrderResponse{" +
                       "orderId=" + orderId +
                       ", productId='" + productId + '\'' +
                       ", status='" + status + '\'' +
                       '}';
    }
    
    public OrderResponse() {
    }
    
    public OrderResponse(int orderId, String productId, String status) {
        this.orderId = orderId;
        this.productId = productId;
        this.status = status;
    }
    
    public OrderResponse setOrderId(int orderId) {
        this.orderId = orderId;
        return this;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public OrderResponse setProductId(String productId) {
        this.productId = productId;
        return this;
    }
    
    public String getStatus() {
        return status;
    }
    
    public OrderResponse setStatus(String status) {
        this.status = status;
        return this;
    }
    
    private int orderId;
    private String productId;
    private String status;
}
