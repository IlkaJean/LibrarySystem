# Library Management System

A comprehensive library management system built with Spring Boot and Thymeleaf, featuring role-based access control, secure authentication with JWT, and protection against SQL injection and deadlocks.

## Features

### User Management
- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **User Roles**: Admin, Employee, and Customer with different permissions
- **Secure Registration**: Customer self-registration with email validation

### Book Management
- **Comprehensive Book Catalog**: Track books with multiple copies, authors, and topics
- **Advanced Search**: Search by title, author, or topic
- **Book Status Tracking**: Monitor availability and rental status
- **Inventory Management**: Add, edit, and delete books and their copies

### Rental Management
- **Book Rental System**: Track book borrowing and returns
- **Overdue Management**: Automatic late fee calculation and overdue notifications
- **Rental History**: Complete rental history for customers and books
- **Return Processing**: Handle returns with invoice generation for late fees

### Reservation System
- **Study Room Booking**: Reserve study rooms for group activities
- **Availability Checking**: Real-time room availability verification
- **Conflict Prevention**: Prevents double-booking of study rooms
- **Reservation Management**: View, edit, and cancel reservations

### Event Management
- **Event Organization**: Create and manage exhibitions and seminars
- **Author Invitations**: Invite authors to seminars
- **Exhibition Registration**: Customers can register for exhibitions
- **Sponsorship Management**: Track seminar sponsorships

### Reporting & Analytics
- **Rental Statistics**: Monthly rental trends and popular books
- **Customer Insights**: Top customers and rental patterns
- **Financial Reports**: Invoice and payment tracking
- **System Overview**: Dashboard with key metrics

### Security Features
- **SQL Injection Protection**: Prepared statements and JPA
- **Deadlock Prevention**: Transaction management and isolation levels
- **Password Security**: BCrypt password hashing
- **CORS Protection**: Configurable cross-origin policies
- **JWT Authentication**: Secure token-based authentication

## Technology Stack

- **Backend**: Spring Boot 3.2.3
- **Database**: Oracle Database 21c
- **Frontend**: Thymeleaf, Bootstrap 5, jQuery
- **Security**: Spring Security, JWT
- **API Documentation**: REST APIs
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Oracle Database 21c
- Maven 3.6+
- Git

## Database Setup

1. Create an Oracle database schema:
```sql
CREATE USER library_user IDENTIFIED BY library_password;
GRANT CONNECT, RESOURCE TO library_user;
GRANT CREATE SESSION TO library_user;
GRANT UNLIMITED TABLESPACE TO library_user;
```

2. Run the provided DDL/DML scripts to create tables and load sample data.

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/library-management-system.git
cd library-management-system
```

2. Configure the database:
```properties
# application.properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=library_user
spring.datasource.password=library_password
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080/library`

## Default Credentials

### Admin User
- Username: admin
- Password: admin123

### Employee User
- Username: employee
- Password: employee123

### Customer User
- Username: customer
- Password: customer123

## API Endpoints

### Authentication
- POST `/api/auth/signin` - Login user
- POST `/api/auth/signup` - Register new user
- GET `/api/auth/me` - Get current user details

### Books
- GET `/api/books` - Get all books
- GET `/api/books/{id}` - Get book by ID
- POST `/api/books` - Create new book (Admin/Employee)
- PUT `/api/books/{id}` - Update book (Admin/Employee)
- DELETE `/api/books/{id}` - Delete book (Admin)

### Customers
- GET `/api/customers` - Get all customers (Admin/Employee)
- GET `/api/customers/{id}` - Get customer by ID
- POST `/api/customers` - Create new customer (Admin/Employee)
- PUT `/api/customers/{id}` - Update customer
- DELETE `/api/customers/{id}` - Delete customer (Admin)

### Rentals
- GET `/api/rentals` - Get all rentals (Admin/Employee)
- GET `/api/rentals/{id}` - Get rental by ID
- POST `/api/rentals` - Create new rental (Admin/Employee)
- POST `/api/rentals/{id}/return` - Return rental (Admin/Employee)
- GET `/api/rentals/overdue` - Get overdue rentals

### Reservations
- GET `/api/reservations` - Get all reservations
- GET `/api/reservations/{id}` - Get reservation by ID
- POST `/api/reservations` - Create new reservation
- PUT `/api/reservations/{id}` - Update reservation
- DELETE `/api/reservations/{id}` - Delete reservation

### Events
- GET `/api/events` - Get all events
- GET `/api/events/{id}` - Get event by ID
- POST `/api/events` - Create new event (Admin/Employee)
- PUT `/api/events/{id}` - Update event (Admin/Employee)
- DELETE `/api/events/{id}` - Delete event (Admin)

### Admin Operations
- POST `/api/admin/users/{userId}/roles/{roleName}` - Add role to user
- DELETE `/api/admin/users/{userId}/roles/{roleName}` - Remove role from user
- POST `/api/admin/process-overdue-rentals` - Process overdue rentals
- GET `/api/admin/stats/overview` - Get system statistics

## Architecture

### Security Measures

1. **SQL Injection Prevention**:
    - All database operations use JPA with prepared statements
    - Input validation and sanitization
    - Parameterized queries

2. **Deadlock Prevention**:
    - Transaction isolation levels
    - Proper transaction boundaries
    - Connection pool configuration
    - Timeout settings

3. **Authentication Security**:
    - JWT tokens with expiration
    - Password hashing with BCrypt
    - Role-based access control
    - Session management

### Transaction Management

The application uses Spring's declarative transaction management with proper isolation levels to prevent deadlocks:

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void someServiceMethod() {
    // Business logic
}
```

### Database Design

The database follows normalized design principles with appropriate constraints:

- Primary keys for all tables
- Foreign key relationships
- Check constraints for data integrity
- Triggers for complex validation
- Inheritance modeling for events and sponsors

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please open an issue in the GitHub repository.

## Acknowledgments

- Spring Boot community
- Thymeleaf team
- Bootstrap contributors
- All open-source contributors

---

**Note**: This is a demonstration project designed for educational purposes. Please ensure proper security measures and testing before using in a production environment.