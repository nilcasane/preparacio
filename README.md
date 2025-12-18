# Veterinary Clinic Project 2025

Your group name: Tetravex & AiPs

Member of the group names: Gonzalo Querolo García, Pau Menino Saborit & Nil Casañé Torras

## Getting Started

### Cloning the Repository

To clone this repository to your local machine, use the following command:

```bash
git clone git@github.com:LabAppInternet/group-103-friday-afternoon-tetravex-aips.git
cd group-103-friday-afternoon-tetravex-aips
```

### Prerequisites

- **Java 21**: Ensure you have Java 21 installed. You can verify this by running `java -version`.

### Running the Application

This project uses the Maven Wrapper, so you don't need to have Maven installed globally.

**On Windows:**
```cmd
mvnw spring-boot:run
```

**On Linux/macOS:**
```bash
./mvnw spring-boot:run
```

The application will start on port `8080`.

### Accessing the Application

- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - **JDBC URL**: `jdbc:h2:mem:testdb`
    - **User Name**: `sa`
    - **Password**: `password`

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) (or `/swagger-ui/index.html` depending on configuration)

## Manual Validation

Manual validation of the API endpoints can be performed using the `calls.http` file located in `src/main/resources/calls.http`.

1. Open `src/main/resources/calls.http` in IntelliJ IDEA.
2. The file contains a sequence of HTTP requests that simulate various user scenarios (Receptionist, Veterinarian, Clinic Manager).
3. Click the "Run" icon (green arrow) next to each request to execute it.
4. The script automatically handles authentication tokens and variable passing (e.g., `visit_id`, `treatment_id`) between requests.

### Validation Flow
The `calls.http` file is organized into sections:
1. **Authentication**: Logs in different users (Receptionist, Vet, Manager) and stores their JWT tokens.
2. **Part 1: Schedule Visits**: Covers scheduling, rescheduling, cancelling, and walk-in visits.
3. **Part 2: Keep Track of Visits and Treatments**: Covers starting consultations, diagnoses, prescriptions, and treatments.
4. **Part 3: Invoicing & Sales**: Covers invoice generation and payment.

**Important**: Run the requests in order, as subsequent requests often depend on IDs (e.g., `visit_id`) captured from previous responses.
