# Two JPA/R2DBC data sources configuration

## About the project
This project demonstrates how to configure two data sources in a Spring Boot application: one using JPA and the other
using R2DBC specification. For this example, we use MySQL and in-memory H2 respectively. The project also features:
- OpenAPI specs to generate APIs and DTOs
- APIs for some operations on the two data sources
- Sample schema and data for both data sources (which will be generated automatically on application startup)
- A simple event-driven pattern (with `Sinks` in the reactive implementation) and observer pattern in the JPA one, to
demonstrate the process when data is updated in one data source and being "reflected" in the other
- Some unit and integration tests

## Prerequisites
- JDK 17 or later
- Maven
- A running MySQL instance

## Steps

### Clone the repository
#### JPA data sources implementation
```bash
git clone https://github.com/TruongLe26/device-ip.git
```
#### R2DBC data sources implementation
```bash
git clone -b reactive https://github.com/TruongLe26/device-ip.git
```
### Generate APIs and DTOs
Run this in the root directory of the project:
```bash
mvn generate-sources
```
### Fill the data sources properties

For the JPA one: We will need a database named `devices` in the MySQL instance (I haven't configured it so we have to
do it manually for now).

Then, we will need to fill the MySQL instance properties in the `application.yml` files, and put a non-empty password in the
`spring.datasource.ips.password` (JPA data source) and `spring.r2dbc.ip.password` (R2DBC data source) properties.

For the tests (only the reactive one has tests for now), the properties should also be filled in
`application-integration-test.yml` and you should do the same thing as above. In addition, please navigate to
`src/test/java/dev/rlet/reactivedevice/testcontainers/BaseContainersTest.java` and fill a non-empty password in this part 
(inside the quotation marks):
```java
registry.add("spring.r2dbc.ip.password", () -> "");
```

### Run the application
Navigate to the root directory of the project and run:
```bash
mvn spring-boot:run
```