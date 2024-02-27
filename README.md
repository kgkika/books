# Books REST API

## Description
This REST API project is designed as part of a Java Engineer Challenge. It serves as a technical assessment.

## Installation
To run this project locally, follow these steps:
1. Clone the repository: `git clone https://github.com/kgkika/books.git`
2. Navigate to the project directory: `cd books`
3. Install dependencies: `mvn install`
4. Start the server: `mvn spring-boot:run`

## Usage

To interact with the REST API, you can use a tool like Postman API. To use the REST API, follow these guidelines:
1. Download [Postman](https://www.postman.com/).
2. Import the provided Postman collection file under `src/main/resources/collection/morotech_books.postman_collection.json` to quickly set up requests.

## Endpoints
The following endpoints are available in this API:
### 1. GET /books?term={term}
- **Description:** Retrieve a list of books based on the term.
- **Request:** Request parameter `term`specifying the title or author of the books to retrieve.
- **Response:** JSON array containing books with details such as authors, language etc.

### 2. GET /books/paged/?term={term}
- **Description:** Retrieve a list of books based on the term with paging.
- **Request:** Request parameter `term` specifying the title or author of the books to retrieve.
- **Response:** JSON object containing books with details such as authors, language encapsulated in Page format.

### 3. POST /reviews/addReview
- **Description:** Add a review about a book.
- **Request:** JSON object containing details of the review to be created, including review text, rating and book id.
- **Response:** 201 CREATED

### 4. GET /books/details/{bookId}
- **Description:** Retrieve details about the book with its reviews and average rating.
- **Request:** Path parameter `bookId` specifying the book identifier.
- **Response:** JSON object containing details of the book, including authors, language, title, average rating, reviews, etc.

### 5. GET /books/monthlyAverage/{bookId}
- **Description:** Retrieve the average rating by month about the book.
- **Request:** Path parameter `bookId` specifying the book identifier.
- **Response:** JSON object containing the average rating per month of the book by each year.


## Technologies Used
This project is built using the following technologies:
- Java 17/Spring Boot 3.2.3, Spring Data JPA, Spring Web
- Database: SQLite

## Running the Project
By default, the project will run on port `:8080`. You can access it through your web browser or API client at `http://localhost:8080`.

## Database Configuration
### Using the Provided Sample Database
By default, the project comes with a sample SQLite database located at `src/main/resources/sqlite/books.db`. This sample database contains pre-populated data.

### Using a New Database
If you prefer to use a new database, Spring Boot can automatically create one during startup. The location and configuration of this database are managed through the application.properties file.

### Configuring the Database Location
You can specify the location of the database by modifying the spring.datasource.url property in the application.properties file. Simply update the value of this property with the desired path where you want the database file to be created

## Authors
- Katerina Gkika - Software Engineer

## Support
For assistance or inquiries, please contact `katgkika91@gmail.com`.