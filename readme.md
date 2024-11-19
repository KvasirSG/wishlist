
# Wishlist App

A feature-rich web application built using Spring Boot, Thymeleaf, and Hibernate. The application allows users to create, manage, and share wishlists with friends and family.


## Authors

- [@KvasirSG](https://www.github.com/kvasirsg)
- [@Dufour](https://github.com/Duofour)


## License

This project is licensed under the [MIT](https://choosealicense.com/licenses/mit/)


## Features

### Core Features
- **User Registration and Login**
    - Secure user authentication with BCrypt password hashing.
    - User-friendly registration with form validation and error handling.

- **Wishlists**
    - Create and manage personalized wishlists.
    - Add, edit, or remove wishes.
    - View wishes within a wishlist.
    - Delete empty wishlists with tooltips guiding users when deletion is not possible.

- **Wish Management**
    - Add wishes to an available list.
    - Use a "ready wish" list for temporary wish management before finalizing.
    - Edit and update existing wishes.

- **Sharing**
    - Share wishlists with other registered users by email or username.
    - View wishlists shared by others.

- **Error Handling**
    - Custom error pages for 403, 404, and 500 errors.


## Technologies Used

### Backend
- **Spring Boot**: Core framework for application development.
- **Hibernate**: ORM for database interactions.
- **Spring Security**: For authentication and access control.

### Frontend
- **Thymeleaf**: For rendering dynamic HTML templates.
- **CSS**: Custom dark-mode theme with user-friendly tooltips and button styles.

### Database
- **H2** (In-memory for development).
- **MySQL** (Production).


## Installation

### Prerequisites
1. **Java 17+**: Ensure you have the correct JDK installed.
2. **Maven**: For dependency management.
3. **MySQL**: Set up a database for production usage.

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/wishlist-app.git
   cd wishlist-app
   ```
2. Set up application properties:
- Copy the ``application.properties`` template from ``src/main/resources``.
- Update your database URL, username, and password for MySQL.
3. Build and run:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
4. Access the application at:
    ```bash
    http://localhost:8080
    ```
## Usage
### Local Development
Run the app locally using ``mvn spring-boot:run``. The in-memory H2 database will reset after every restart.

### Production Deployment
- Use a production-grade database like MySQL.
- Configure a reverse proxy (e.g., NGINX) for better performance and security.


