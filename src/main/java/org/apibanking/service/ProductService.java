package org.apibanking.service;

import java.util.List;

import org.apibanking.dto.ProductDto;
import org.apibanking.dto.StockDto;
import org.apibanking.entity.Product;
import org.apibanking.exception.BadRequestException;
import org.apibanking.exception.NotFoundException;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductService {

	public Uni<ProductDto> getProductById(Long id) {
        
		Log.infof("Attempting to fetch product with ID: %d", id);
		validateId(id);

		Uni<Product> uniProduct = Product.findById(id);
		return uniProduct.onItem().transform(product -> {
			if (product != null) {
				
				Log.infof("Product with ID %d successfully retrieved.", id);
				return toProductDto(product);
			} else {
				
				Log.warnf("Product with ID %d not found in the database.", id);
				throw new NotFoundException("Product not found with ID: " + id);
			}
		});
	}
	
	public Uni<List<ProductDto>> getAllProducts(boolean sortByPrice) {
		
		Log.infof("Fetching all products, sortByPrice = %b", sortByPrice);
		Uni<List<Product>> uniProducts;

		if (sortByPrice) {
			uniProducts = Product.listAll(Sort.by("price"));
		} else {
			uniProducts = Product.listAll();
		}
		
		return uniProducts.onItem().transform(products -> {
                    Log.infof("Found %d products", products.size());
                    return products.stream()
                                   .map(product -> toProductDto(product))
                                   .toList();
                });
	}

	public Uni<Product> createProduct(ProductDto productDto) {

		Log.infof("Creating a new product");
		validateProductDto(productDto);
		Product product = new Product(productDto.getName(), productDto.getDescription(), productDto.getPrice(),
				productDto.getQuantity());
		
        Log.infof("Product object created: %s", product);
		return Panache.withTransaction(product::persist).replaceWith(product);

	}

	public Uni<Product> updateProduct(Long id, ProductDto productDto) {

        Log.infof("Updating product with ID: %d", id);

		validateId(id);
		validateProductDto(productDto);
		Uni<Product> uniProduct = Product.findById(id);

		return uniProduct.onItem().transformToUni(product -> {
			if (product == null) {
				throw new NotFoundException("Product with ID " + id + " not found");
			}

			product.setName(productDto.getName());
			product.setDescription(productDto.getDescription());
			product.setPrice(productDto.getPrice());
			product.setQuantity(productDto.getQuantity());

			Log.infof("Product with ID %d updated successfully", id);
			return Panache.withTransaction(product::persist).replaceWith(product);
		});
	}

	public Uni<Void> deleteProduct(Long id) {

        Log.infof("Attempting to delete product with ID: %d", id);

		validateId(id);
		Uni<Product> uniProduct = Product.findById(id);
		return uniProduct.onItem().transformToUni(product -> {
			if (product == null) {
				Log.warnf("Product with ID %d not found", id);
				throw new NotFoundException("Product with ID " + id + " not found");
			}

			Uni<Void> deleted = Panache.withTransaction(product::delete);
			Log.infof("Product with ID %d deleted successfully", id);
			return deleted;
		});
	}
	
	public Uni<StockDto> checkStockAvailability(Long id, Long count) {
		
		validateId(id);
		Uni<Product> uniProduct = Product.findById(id);
		
        return uniProduct  
            .onItem().transform(product -> {
                if (product == null) {
                    throw new NotFoundException("Product with ID " + id + " not found");
                }
                boolean isAvailable = product.getQuantity() >= count;
                return new StockDto(isAvailable);
            });
    }

	private ProductDto toProductDto(Product product) {
        return new ProductDto(
            product.id,
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity()
        );
    }

	private void validateProductDto(ProductDto productDTO) {
		StringBuilder errorMessage = new StringBuilder();

		if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
			errorMessage.append("Name is required.");
		} else if (productDTO.getName().length() < 1 || productDTO.getName().length() > 100) {
			errorMessage.append("Name should be between 1 and 100 characters.");
		}

		if (productDTO.getDescription() != null && productDTO.getDescription().length() > 1000) {
			errorMessage.append("Description should be between 1 and 1000 characters.");
		}

		if (productDTO.getPrice() == null || productDTO.getPrice() < 0) {
			errorMessage.append("Price should be greater than or equal to 0.");
		}

		if (productDTO.getQuantity() == null || productDTO.getQuantity() < 0) {
			errorMessage.append("Quantity should be greater than or equal to 0.");
		}

		if (errorMessage.length() > 0) {
			throw new BadRequestException(errorMessage.toString());
		}
	}
	
	private void validateId(Long id) {
		if (id == null || id <= 0) {
			throw new BadRequestException("Invalid product ID. ID must be greater than 0");
		}
	}
}
