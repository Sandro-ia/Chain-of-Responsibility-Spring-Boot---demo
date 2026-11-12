# Registration Validation with Chain of Responsibility (Spring Boot)

A REST API built with **Java 21 and Spring Boot 3**, created for the final challenge of the DIO bootcamp on **Design Patterns: from the GoF classics to the Spring Framework**. It applies the **Chain of Responsibility** pattern (a GoF behavioral pattern) to validate a user registration, uses Spring's **dependency injection** to assemble the chain automatically, **Spring Data JPA + H2** to save approved registrations and **OpenFeign** to check the zip code (CEP) on the [ViaCEP](https://viacep.com.br) web service.

## The idea

Think of the checks at an airport: passport, luggage, X-ray. Each desk verifies **only its own part**. If one desk rejects you, the process stops there; if it approves you, you move on to the next desk.

Here, a registration request travels through a chain of handlers, and each handler validates a single rule:

| Order | Handler | Rule |
|---|---|---|
| 1 | `NameHandler` | Name with at least 3 characters |
| 2 | `EmailHandler` | Valid e-mail format |
| 3 | `AgeHandler` | At least 18 years old |
| 4 | `PasswordHandler` | At least 8 characters, one digit and one uppercase letter |
| 5 | `DuplicateEmailHandler` | E-mail not registered yet (queries the database with JPA) |
| 6 | `ZipCodeHandler` | Zip code exists (calls ViaCEP with OpenFeign) |

The chain stops at the **first** rule that fails and reports which one it was. The two costly checks (database and internet) come **last**, so a request that fails a cheap rule never triggers them.

Only when every desk approves, the registration is saved in the H2 database.

## How the pattern maps to the code

| Pattern role | Class | Responsibility |
|---|---|---|
| **Handler** (abstract) | `ValidationHandler` | Defines the routine: check my rule, stop if it fails, otherwise pass to the next |
| **Concrete handlers** | `NameHandler`, `EmailHandler`, `AgeHandler`, `PasswordHandler`, `DuplicateEmailHandler`, `ZipCodeHandler` | Each one implements a single rule |
| **Chain assembly** | `ValidationChain` | Receives all handlers from Spring, links each one to the next and starts the chain |
| **Client** | `RegistrationService` / `RegistrationController` | Run the chain, save the result and answer the HTTP request |

```
 request ──> Name ──> E-mail ──> Age ──> Password ──> Duplicate e-mail ──> Zip code ──> saved (201)
               │         │         │         │               │                │
               ▼         ▼         ▼         ▼               ▼                ▼
            rejected  rejected  rejected  rejected        rejected         rejected      (chain stops, 422)
```

## Where Spring comes in

- `@Component` registers each handler as a **bean** (an object managed by Spring).
- `@Order(n)` defines the position of each handler in the chain.
- `ValidationChain` asks for a `List<ValidationHandler>` in its constructor, and Spring injects **all** handlers already sorted by `@Order` (**dependency injection**).
- Handlers can receive their own dependencies the same way: `DuplicateEmailHandler` gets the repository and `ZipCodeHandler` gets the Feign client.
- Adding a new rule requires **no change** to existing code: create a class with `@Component` and an `@Order`, and Spring adds it to the chain by itself (Open/Closed Principle).
- **Spring Data JPA** creates the repository implementation from an interface, and even builds the `existsByEmail` query from the method name.
- **OpenFeign** creates the HTTP client from an interface (`ViaCepClient`) and a couple of annotations.

## Endpoints

### `POST /registrations`

Validates and, if approved, saves a registration.

Request body:

```json
{
  "name": "Sandro",
  "email": "sandro@email.com",
  "age": 30,
  "password": "Senha1234",
  "zipCode": "01001-000"
}
```

Approved (`201 Created`):

```json
{
  "valid": true,
  "failedRule": null,
  "message": "Registration approved and saved with id 1."
}
```

Rejected (`422 Unprocessable Entity`), for example with `"age": 17`:

```json
{
  "valid": false,
  "failedRule": "Age",
  "message": "You must be at least 18 years old."
}
```

### `GET /registrations`

Lists the saved registrations. The H2 database lives **in memory**, so the list is empty again every time the application restarts.

### `GET /registrations/rules`

Lists the rules in the order they run:

```json
["Name", "E-mail", "Age", "Password", "Duplicate e-mail", "Zip code"]
```

## About the zip code check

The `ZipCodeHandler` calls `https://viacep.com.br/ws/{zipCode}/json/`. It handles three situations:

1. **Wrong format** (not 8 digits): rejected without calling ViaCEP, which answers `400 Bad Request` to malformed zip codes.
2. **Zip code that does not exist**: ViaCEP answers `{"erro": "true"}`, so the zip code is rejected as "not found".
3. **ViaCEP offline or slow**: the request is rejected with a message saying the zip code could not be verified (timeouts are set in `application.properties`).

> This project needs an internet connection to validate zip codes.

## A note about passwords

The password is only **checked** by the chain and then discarded: it is never saved. Storing plain-text passwords is a serious security mistake. A real application would save a hash of it (for example with Spring Security and BCrypt).

## Adding a new rule

Create a new handler and Spring does the rest:

```java
@Component
@Order(7)
public class BlockedDomainHandler extends ValidationHandler {

    @Override
    protected ValidationResult check(RegistrationRequest request) {
        // your rule here: return ValidationResult.ok() or ValidationResult.fail(getRuleName(), "...")
        return ValidationResult.ok();
    }

    @Override
    public String getRuleName() {
        return "Blocked domain";
    }
}
```

## Project structure

```
demo/
├── pom.xml
└── src/main/
    ├── java/com/example/demo/
    │   ├── DemoApplication.java            # Spring Boot entry point (@EnableFeignClients)
    │   ├── model/
    │   │   ├── RegistrationRequest.java    # data sent by the client
    │   │   └── ValidationResult.java       # verdict: approved or rejected
    │   ├── handler/
    │   │   ├── ValidationHandler.java      # abstract handler
    │   │   ├── NameHandler.java
    │   │   ├── EmailHandler.java
    │   │   ├── AgeHandler.java
    │   │   ├── PasswordHandler.java
    │   │   ├── DuplicateEmailHandler.java  # uses JPA
    │   │   └── ZipCodeHandler.java         # uses OpenFeign
    │   ├── service/
    │   │   ├── ValidationChain.java        # assembles the chain
    │   │   └── RegistrationService.java    # runs the chain and saves
    │   ├── entity/
    │   │   └── Registration.java           # JPA entity (table)
    │   ├── repository/
    │   │   └── RegistrationRepository.java # Spring Data JPA interface
    │   ├── client/
    │   │   ├── ViaCepClient.java           # OpenFeign client
    │   │   └── ViaCepResponse.java         # ViaCEP JSON
    │   └── controller/
    │       └── RegistrationController.java # REST endpoints
    └── resources/
        └── application.properties
```

## How to run

**Requirements:** JDK 21 and an internet connection (Maven dependencies and ViaCEP).

### Using IntelliJ IDEA

1. Open the project folder in IntelliJ and wait for Maven to download the dependencies.
2. Open `DemoApplication.java`.
3. Click the green run button next to `main`.
4. The API starts at `http://localhost:8080`.

### Using the command line

```
./mvnw spring-boot:run
```

(on Windows: `mvnw.cmd spring-boot:run`)

## How to test

- **Browser/H2 Console:** open `http://localhost:8080/h2-console` (JDBC URL: jdbc:h2:mem:testdb).
- **Postman:** create a `POST` request to `http://localhost:8080/registrations`, choose **Body → raw → JSON** and paste the JSON example above. Then open `GET /registrations` to see the saved row.
- **curl:**

```
curl -X POST http://localhost:8080/registrations \
  -H "Content-Type: application/json" \
  -d '{"name":"Sandro","email":"sandro@email.com","age":30,"password":"Senha1234","zipCode":"01001-000"}'
```

## Technologies

- Java 21

- Spring Boot 3.2.5 (Spring Web, Spring Data JPA)

- H2 Database (in memory)

- OpenFeign (Spring Cloud 2023.0.1)

- Maven

## Possible next steps

- Add unit tests for each handler and for the whole chain (JUnit 5 and Mockito).
- Hash the password with Spring Security (BCrypt) and save it.
- Save the address returned by ViaCEP together with the registration.
- Replace H2 with a persistent database such as PostgreSQL.

## Author

Sandro ([@Sandro-ia](https://github.com/Sandro-ia))
