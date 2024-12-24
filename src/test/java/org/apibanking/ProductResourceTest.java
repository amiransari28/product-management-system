package org.apibanking;


import static io.restassured.RestAssured.given;

import org.apibanking.dto.ProductDto;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class ProductResourceTest {	
    static final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig());

    private final String name =  "Product1";
    private final String description =  "Desc1";
    private final Double price = 100.5;
    private final Long quantity = 50L;
    private final Long invalidId = 99999L;
    @Test
    @Order(1)
    public void createProduct_shouldCreate() {
    	
        ProductDto request = createRequest(name, description, price, quantity);
        		
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(jsonb.toJson(request))
                .post("/product")
                .then()
                .statusCode(201);
    }
    
    @Test
    public void createProduct_shouldGiveBadRequest_whenNameIsEmpty() {
    	
        ProductDto product = createRequest("", description, price, quantity);

        createProduct(product, 400, "Name is required.");
    }
    
    @Test
    public void createProduct_shouldGiveBadRequest_whenNameIsNull() {
    	
        ProductDto request = createRequest(null, description, price, quantity);
        createProduct(request, 400, "Name is required.");
    }
    
    @Test
    public void createProduct_shouldGiveBadRequest_whenNameExceedsLimit() {
    	
    	String name = "nwmfkbaagcmnnkepqkmbvxfsosdaprjpkkkxwdneimiaqdiabzmcrezdsiyxibidsstavfodixsnhzudypubxlcbgupvxmbzloqpg";
        ProductDto request = createRequest(name, description, price, quantity);

        createProduct(request, 400, "Name should be between 1 and 100 characters.");
    }
    
    @Test
    public void createProduct_shouldGiveBadRequest_whenInvalidPrice() {
    	
        ProductDto request = createRequest(name, description, -1D, quantity);
        createProduct(request, 400, "Price should be greater than or equal to 0.");
    }

    @Test
    public void createProduct_shouldGiveBadRequest_whenInvalidQuantity() {
    	
        ProductDto request = createRequest(name, description, price, -5L);
        createProduct(request, 400, "Quantity should be greater than or equal to 0.");
    }
    
    @Test
    public void createProduct_shouldGiveBadRequest_whenDescExceedsLimit() {
        
        String desc = "sqsHgq8iiQcnogGFXCSwZd40QoKyMYMChbuffOQGMElwdiwOlpg0tXBrxYhe4uk8g5Xx7URECsKI0dUBJ2SUNOAuVa8zcyBHgJSLx9lRDqg3M1BQ9fzgvmfCI8vhavOyFtj9NcRP8kBvUGIcySKlsaOJvmEryRlxroPpvyGqZ8DIPaoPz8GKVs8JtQA2XAWUlnEKm3NXFEJh5xmpywypARzVtI9UUarKwalqSSDZEJ3PetZoPMjMQnIW8hLoVUF9blYingfOjhIKRWtJOXu7LizgDWz9C4NWeavwZhYyhPAiDLri6al77hHCFjX01zpYHutxus0kocP8TfGMb7xsrM5m6p0fqooidt44sVUOv7auSMgLPO3H2BByyk2AHjc6ec4sZS7zi4UHbKdRlyvhX9BMCviSONvXzWxfrLGgit5Vd7NesJdoblsVVNjqVp2N02mbyJ7ErtAkpn8q1G0KWhl1AZUxqxCqD6tFUGOcjLR1ii3ukEXjE6o30Lkk8RtXSHeeVS6dYl7HjQCi1s55w8Mtd5tGIQYAR4LttvEINgquiwpWov27U9sRlWtvcCNU9lXpERYm7UwQFNu7QyId3j5LYzD2wTeI7f6QcHbUQQ8Gbx0DCaYu49XA6zqRh8ojdMRZZNArxvfjNQ1KOyQKeS7VnTwVjOTo8CbFgdfC06CLK35XMPrpBuDUrGPGL5qnoIjgL3CK0YnnNHbrrjnDrUc11AC4BKHut0eFwxRt7HbugIMLwPTpBxizgbwQu1BLXQ9gdpY4TkX6zdU3MYN2GVOvG7Up9Gyntx0LXIyIGSyLmUX53rZg8muzogKuXYlgOF5pb9i7Lg51yrSTS7Nz2YxZswP0lYIhubeiAKfKvJ9q6KzdBMmYqpHs0QCuDlyLusKHkZpZTc1z2JA53BpiWD6ce1cZSKQDRMRO6uxclJKvWFSl2mtrZg1nw5f1seUdrIwFAWicIuvjthU2O1gthpWcCtp284NSCwPErzzyu";
        ProductDto request = createRequest(name, desc, price, quantity);
        
        createProduct(request, 400, "Description should be between 1 and 1000 characters.");
    }
    
    @Test
    @Order(2)
    public void getProductById_shouldReturn_whenProductExist() {
    	
        given()
                .when()
                .get("/product/{id}", 1)
                .then()
                .statusCode(200) 
                .body("id", equalTo(1))
                .body("name", equalTo(name))  
                .body("description", equalTo(description))  
                .body("price", equalTo(100.5F)) 
                .body("quantity", equalTo(50));
    }
    
    @Test
    public void getProductById_shouldGiveNotFound_whenProductNotPresent() {
        Long productId = 999L;

        given()
                .when()
                .get("/product/{id}", productId)
                .then()
                .statusCode(404) // Not Found
                .body("message", equalTo("Product not found with ID: " + productId));
    }

    @Test
    public void getProductById_shouldGiveBadRequest_whenInvalidProductId() {
        
        given()
                .when()
                .get("/product/{id}", -1)
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid product ID. ID must be greater than 0"));
    }
    
    @Test
    @Order(3)
    public void getAllProducts_shouldReturn_whenNoSortApplied() {

    	ProductDto product2 = createRequest("Product2", "Desc2", 50D, 100L);
    	// creating one more product
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(jsonb.toJson(product2))
                .post("/product");

        given()
                .when()
                .get("/product?sortByPrice=false")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].name", equalTo("Product1"))
                .body("[1].name", equalTo("Product2"));
               }

    @Test
    @Order(4)
    public void getAllProducts_shouldReturnSorted_whenSortByPriceApplied() {

    	given()
                .when()
                .get("/product?sortByPrice=true")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("[0].name", equalTo("Product2"))  // Product2 with price 50 should be first
                .body("[1].name", equalTo("Product1"));  // Product1 with price 100.5 should be second
    }
    
    @Test
    @Order(5)
    public void checkStockAvailability_shouldReturnTrue_whenStockIsSufficient() {

        given()
                .when()
                .get("/product/1/check-stock?count=5")
                .then()
                .statusCode(200)
                .body("available", is(true));
    }

    
    @Test
    @Order(6)
    public void checkStockAvailability_shouldReturnFalse_whenStockIsInsufficient() {
      
        given()
                .when()
                .get("/product/1/check-stock?count=52")
                .then()
                .statusCode(200)
                .body("available", is(false));
    }

    @Test
    @Order(7)
    public void checkStockAvailability_shouldReturnTrue_whenCountIsMissing() {

    	given()
                .when()
                .get("/product/1/check-stock")
                .then()
                .statusCode(200)
                .body("available", is(true));
    }
    
    @Test
    public void checkStockAvailability_shouldReturn404_whenProductNotFound() {

    	given()
                .when()
                .get("/product/"+ invalidId+ "/check-stock?count=10")
                .then()
                .statusCode(404);
    }
    
    @Test
    @Order(8)
    public void updateProduct_shouldUpdate_whenValidRequest() {

    	ProductDto request = createRequest("Updated Product Name", "Updated Description", 150.0, 100L);

        given()
                .when()
                .header("Content-Type", "application/json")
                .body(jsonb.toJson(request))
                .put("/product/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Product Name"))
                .body("description", equalTo("Updated Description"))
                .body("price", equalTo(150.0f))
                .body("quantity", equalTo(100));
    }

    @Test
    public void updateProduct_shouldReturn404_whenProductDoesNotExist() {
    	
    	ProductDto request = createRequest("Updated Product Name", "Updated Description", 150.0, 100L);

    	given()
                .when()
                .header("Content-Type", "application/json")
                .body(jsonb.toJson(request))
                .put("/product/"+invalidId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Product with ID " + invalidId + " not found"));
    }
    
    @Test
    public void updateProduct_shouldReturn400_whenProductIdIsInvalid() {
    	
    	ProductDto request = createRequest("Updated Product Name", "Updated Description", 150.0, 100L);

        given()
                .when()
                .header("Content-Type", "application/json")
                .body(jsonb.toJson(request))
                .put("/product/0")
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid product ID. ID must be greater than 0"));
    }
    
    @Test
    @Order(9)
    public void deleteProduct_shouldDelete_whenProductExists() {

        given()
                .when()
                .delete("/product/1")
                .then()
                .statusCode(204);
    }

    @Test
    public void deleteProduct_shouldReturn404_whenProductDoesNotExist() {

    	given()
                .when()
                .delete("/product/"+invalidId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Product with ID " + invalidId +" not found"));
    }

    @Test
    public void deleteProduct_shouldReturn400_whenProductIdIsInvalid() {

    	given()
                .when()
                .delete("/product/0")
                .then()
                .statusCode(400)
                .body("message", equalTo("Invalid product ID. ID must be greater than 0"));
    }
    
    private ProductDto createRequest(String name, String desc, Double price, Long quantity) {
    	
    	return ProductDto
        .builder()
        .name(name)
        .description(desc)
        .price(price)
        .quantity(quantity)
        .build();
    }
    
    private void createProduct(ProductDto request, int status,String message) {
    	
		given()
    .when()
    .header("Content-Type", "application/json")
    .body(jsonb.toJson(request))
    .post("/product")
    .then()
    .statusCode(status);
}
}
