package org.apibanking;

import java.util.List;

import org.apibanking.dto.ProductDto;
import org.apibanking.dto.StockDto;
import org.apibanking.entity.Product;
import org.apibanking.service.ProductService;
import org.jboss.resteasy.reactive.RestResponse;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/product")
@ApplicationScoped
public class ProductResource {

	@Inject
	private ProductService productService;

	@GET
	@Path("/{id}")
	public Uni<RestResponse<ProductDto>> getSingle(@PathParam("id") Long id) {

		return productService.getProductById(id).onItem().transform(productDto -> 
			 RestResponse.ok(productDto));
	}

	@GET
	public Uni<RestResponse<List<ProductDto>>> getAllProducts(@QueryParam("sortByPrice") boolean sortByPrice) {

		return productService.getAllProducts(sortByPrice)
	            .onItem().transform(productDtoList -> RestResponse.ok(productDtoList));
	}

	@POST
	public Uni<RestResponse<Product>> create(ProductDto productDTO) {

		return productService.createProduct(productDTO).onItem().transform(product -> 

			 RestResponse.status(Response.Status.CREATED, product));

	}

	@PUT
	@Path("/{id}")
	public Uni<RestResponse<Product>> update(@PathParam("id") Long id, ProductDto productDTO) {

		return productService.updateProduct(id, productDTO).onItem().transform(updatedProduct -> 
			 RestResponse.ok(updatedProduct));
		
	}

	@DELETE
	@Path("/{id}")
	public Uni<RestResponse<Void>> delete(@PathParam("id") Long id) {

		return productService.deleteProduct(id).onItem().transform(updatedProduct -> 
			 RestResponse.status(Response.Status.NO_CONTENT, updatedProduct));
	}

	@GET
	@Path("/{id}/check-stock")
	public Uni<RestResponse<StockDto>> checkStockAvailability(@PathParam("id") Long productId,
			@QueryParam("count") @DefaultValue("0") Long count) {

		return productService.checkStockAvailability(productId, count)
				.onItem().transform(stock -> RestResponse.ok(stock));
	}
}
