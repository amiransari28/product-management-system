package org.apibanking.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends PanacheEntity {

	@Column(nullable = false, length = 100)
	private String name;

	@Column(length = 1000)
	private String description;

	@Column(nullable = false)
	private Double price;

	@Column(nullable = false)
	private Long quantity;
}
