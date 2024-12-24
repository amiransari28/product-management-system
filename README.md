# Quarkus Reactive Product Management System

This is a Quarkus-based REST API application designed to perform CRUD operations on a product management system. It uses Quarkus Reactive to handle asynchronous and non-blocking database interactions.

## Features

1. **Product Entity:**
   - Attributes: `id`, `name`, `description`, `price`, `quantity`.
   
2. **CRUD Operations:**
   - Create a new product.
   - Get a list of all products and the details of a specific product by its `id`.
   - Update the details of an existing product by `id`.
   - Delete a product by `id`.

3. **Additional Features:**
   - **Check stock availability:** An endpoint to check if a product has a specified quantity available.
   - **Get products sorted by price:** Endpoint to retrieve all products ordered by price in ascending order.

## Prerequisites

- Java 17 or later
- Maven
- Docker (to run PostgreSQL DB)

## Setup

### 1. Clone the repository

1. `git clone https://github.com/your-repo/product-management-system.git`
2. `cd product-management-system`

### 2. Build and Run the Application
1. This project uses Quarkus DevServices, which automatically manages the PostgreSQL database for you during development. You don't need to manually configure a PostgreSQL container.

2. You can build and run the application with Maven:
`mvn compile quarkus:dev` or
`mvn quarkus:dev`

This will start the application in development mode at http://localhost:8080, and Quarkus DevServices will automatically spin up a temporary PostgreSQL database for you.

### 3. Access Swagger UI
Once the application is running, you can open Swagger UI at the following URL to explore and test the API:

http://localhost:8080/product/swagger/

### 4. Testing
Unit tests for the API endpoints have been implemented using Quarkus' test framework. To run the tests, use the following Maven command:

`mvn test`
This will execute all the tests and show the results in the terminal.

### 5. Additional Development Aspects
1. Reactive Database Access: The application uses Quarkus Reactive with Hibernate ORM to ensure non-blocking database operations, improving performance for large-scale applications.
2. API Documentation: The API documentation is automatically generated using OpenAPI and can be explored using Swagger UI.
3. Error Handling: The API handles common errors like resource not found or invalid input with appropriate HTTP status codes.
4. DevServices: With DevServices enabled (quarkus.datasource.devservices.enabled=true), Quarkus automatically provisions and manages the PostgreSQL database during development, removing the need for manual database setup.