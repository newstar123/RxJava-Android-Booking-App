package app.core.uber.product.entity;


import java.util.List;

public class UberProductsResponse {
    private List<UberProductResponse> products;

    public UberProductsResponse(List<UberProductResponse> products) {
        this.products = products;
    }

    public List<UberProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<UberProductResponse> products) {
        this.products = products;
    }
}
