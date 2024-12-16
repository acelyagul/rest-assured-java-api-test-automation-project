# Rest-Assured Java API Automation Project

This project is designed for **API testing** using the **Rest-Assured library** in Java. It follows a modular and organized structure, leveraging reusable services, test classes, utilities, and JSON schemas for validating API requests and responses.

## Project Structure

The project follows a dynamic structure to ensure clarity, maintainability, and reusability:

```plaintext
src
 └── test
     ├── java
     │   └── com.api
     │       ├── services
     │       │   ├── AuthService.java          # Handles authentication service calls
     │       │   ├── BookingService.java       # Handles booking-related API calls
     │       │   ├── PingService.java          # Handles health-check API calls
     │       │
     │       ├── tests
     │       │   ├── AuthTest.java             # Tests for AuthService
     │       │   ├── BookingTest.java          # Tests for BookingService
     │       │   ├── PingTest.java             # Tests for PingService
     │       │
     │       ├── utils
     │       │   ├── RestUtils.java            # Utility methods for REST API calls
     │
     ├── resources
     │   └── schemas
     │       ├── createBookingSchema.json     # Schema for POST Create Booking API
     │       ├── getBookingSchema.json        # Schema for GET Booking API
     │       ├── patchBookingSchema.json      # Schema for PATCH Update Booking API
     │       ├── updateBookingSchema.json     # Schema for PUT Update Booking API
     │
     └── pom.xml                              # Maven dependencies
```

# Tool stack

* **Rest Assured Framework** - Development Framework
* **Java** - Development Language
* **IntelliJ IDE** - Development IDE
* **Maven** - Package Management

# Installations

* To install the necessary libraries , the following command is run in the project directory.
  ```
  mvn clean install
  ```

# Run Test

```
mvn clean test
```