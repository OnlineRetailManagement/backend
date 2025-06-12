# Online Retail Management (Backend)

A Spring Boot REST API for online management system.

## Features

- Product management
- Customer registration and profile management
- Order placement, tracking, and processing
- RESTful endpoints following best practices
- Integrated with Spring Data JPA and H2/MySQL

⚙ Prerequisites
Java  17
Maven 3.9+
MySQL 
(Optional) Postman or HTTP client for testing

🔧 Setup & Run
Clone the repo
git clone https://github.com/OnlineRetailManagement/backend.git
cd backend
Configure database
For MySQL: edit src/main/resources/application.properties:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/retaildb
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASS
spring.jpa.hibernate.ddl-auto=update
Build & run

./mvnw clean package
./mvnw spring-boot:run
The server runs on http://localhost:8080.

🛡 Security
Methods secured via Spring Security
JWT-based authentication
Roles: ADMIN, USER and VENDOR with restricted access
