# Blog Application - Spring Boot

A full-featured blog application built with Spring Boot, featuring user authentication, blog creation, comments, likes, categories, search, and pagination.

## Features

- ✅ User Registration & Authentication (JWT-based)
- ✅ Create, Read, Update, Delete Blogs
- ✅ Comments on Blogs
- ✅ Like/Unlike Blogs
- ✅ Categories for Blogs
- ✅ Search Functionality
- ✅ Pagination
- ✅ Modern Responsive UI
- ✅ View Count Tracking
- ✅ Popular Blogs Sorting

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- (Optional) MySQL for production (H2 database is used by default for development)

## How to Run

### Option 1: Using Maven (Recommended)

1. **Navigate to the project directory:**
   ```bash
   cd BLOG
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

   Or if you prefer to run the JAR directly:
   ```bash
   mvn package
   java -jar target/blog-app-1.0.0.jar
   ```

### Option 2: Using IDE (IntelliJ IDEA / Eclipse)

1. **Import the project:**
   - Open your IDE
   - Import as Maven project
   - Select the `pom.xml` file

2. **Run the application:**
   - Find `BlogApplication.java` in `src/main/java/com/blog/`
   - Right-click and select "Run BlogApplication"
   - Or use the IDE's run button

## Accessing the Application

Once the application is running:

- **Frontend:** Open your browser and go to: `http://localhost:8080`
- **API Base URL:** `http://localhost:8080/api`
- **H2 Console (for database inspection):** `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:blogdb`
  - Username: `sa`
  - Password: (leave empty)

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user

### Blogs
- `GET /api/blogs` - Get all blogs (with pagination, search, sorting)
- `GET /api/blogs/{id}` - Get blog by ID
- `GET /api/blogs/author/{authorId}` - Get blogs by author
- `POST /api/blogs` - Create a new blog (requires authentication)
- `PUT /api/blogs/{id}` - Update blog (requires authentication)
- `DELETE /api/blogs/{id}` - Delete blog (requires authentication)

### Comments
- `GET /api/comments/blog/{blogId}` - Get comments for a blog
- `POST /api/comments` - Create a comment (requires authentication)
- `PUT /api/comments/{id}` - Update comment (requires authentication)
- `DELETE /api/comments/{id}` - Delete comment (requires authentication)

### Likes
- `POST /api/likes/blog/{blogId}` - Toggle like on a blog (requires authentication)
- `GET /api/likes/blog/{blogId}` - Get like info for a blog

### Categories
- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create a category

## Usage Guide

1. **Register/Login:**
   - Click "Sign Up" to create an account
   - Or click "Login" if you already have an account

2. **Read Blogs:**
   - Browse blogs on the home page
   - Click on any blog card to read the full content
   - Use search to find specific blogs
   - Filter by categories

3. **Create Blog:**
   - Click "Write" in the navigation
   - Fill in title, content, and optionally add an image URL and categories
   - Click "Publish"

4. **Interact:**
   - Like blogs by clicking the like button
   - Comment on blogs by scrolling to the comments section
   - View popular blogs by selecting "Most Popular" in the sort dropdown

## Database Configuration

By default, the application uses H2 in-memory database. To use MySQL:

1. Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/blogdb
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
   ```

2. Create the database:
   ```sql
   CREATE DATABASE blogdb;
   ```

## Troubleshooting

- **Port 8080 already in use:** Change the port in `application.properties`:
  ```properties
  server.port=8081
  ```

- **Build errors:** Make sure you have Java 17+ installed:
  ```bash
  java -version
  ```

- **Maven not found:** Install Maven or use the Maven wrapper:
  ```bash
  ./mvnw spring-boot:run  # Linux/Mac
  mvnw.cmd spring-boot:run  # Windows
  ```

## Project Structure

```
BLOG/
├── src/
│   ├── main/
│   │   ├── java/com/blog/
│   │   │   ├── entity/          # Database entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/          # Business logic
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── security/         # Security configuration
│   │   │   └── dto/              # Data transfer objects
│   │   └── resources/
│   │       ├── static/           # Frontend files
│   │       │   ├── index.html
│   │       │   ├── css/
│   │       │   └── js/
│   │       └── application.properties
│   └── test/                     # Test files
├── pom.xml                       # Maven configuration
└── README.md
```

## Technologies Used

- **Backend:** Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Database:** H2 (development), MySQL (production ready)
- **Authentication:** JWT (JSON Web Tokens)
- **Frontend:** HTML5, CSS3, JavaScript (Vanilla)
- **Build Tool:** Maven

## License

This project is open source and available for educational purposes.
