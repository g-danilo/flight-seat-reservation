# Flight Seat Reservation System

A small Spring Boot backend for searching flights and reserving seats.

The implementation focuses on: timezone-aware booking cutoffs, temporary seat holds, preventing double booking under concurrent requests, and releasing seats when bookings expire or are cancelled.

## Tech Stack

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* H2
* Maven

## Requirements

* Java 21+
* Maven 3.9+ (or use the included Maven Wrapper)

## Running the application

Using the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

The application uses an in-memory H2 database, so data is reset whenever the application restarts.
After application start, database can be accessed at http://localhost:8080/h2-console
with credentials username=sa, password=

## API

### Search flights

```http
GET /flights
```

Optional filters:

```text
GET /flights?date=2026-10-05
GET /flights?departureAirport=DUB
GET /flights?arrivalAirport=LHR
GET /flights?departureAirport=DUB&arrivalAirport=LHR
```

The endpoint can also combine the date and route filters.

### Create a flight

```http
POST /admin/flights
Content-Type: application/json
```

Example:

```json
{
  "flightNumber": "EI123",
  "departureAirport": "DUB",
  "departureTime": "2026-10-05T14:30:00+01:00[Europe/Dublin]",
  "arrivalAirport": "LHR",
  "arrivalTime": "2026-10-05T15:50:00+01:00[Europe/London]",
  "totalSeats": 180
}
```

### Get available seats

```http
GET /flights/{id}/seats
```

Returns the currently available seat numbers for the flight.

### Reserve a seat

```http
POST /flights/{id}/bookings
Content-Type: application/json
```

Example:

```json
{
  "passengerName": "Danilo Gasljevic",
  "seatNumber": 12
}
```

A successful reservation initially has the status:

```text
HELD
```

### Confirm a booking

```http
POST /bookings/{id}/confirm
```

A held booking becomes:

```text
CONFIRMED
```

### Cancel a booking

```http
DELETE /bookings/{id}
```

A held or confirmed booking becomes:

```text
CANCELLED
```

The associated seat is released.

### Delete a flight

```http
DELETE /admin/flights/{id}
```

A flight can only be deleted when it has no bookings.

## Design decisions

### Time zones and booking cutoff

Flight departure times are stored as `ZonedDateTime`.

The booking cutoff is calculated using the flight's departure timezone rather than the server timezone.

This avoids relying on the timezone of the machine running the application.

### Seat holds

A newly created booking is held for **2 minutes**.

A scheduled task runs every 10 seconds and:

1. Marks expired held bookings as `EXPIRED`.
2. Releases the seats belonging to those bookings.

The hold duration and cleanup interval are intentionally short because this allows for a real time testing of the holdng/expire process. For a production level design, I would use longer holds.

### Preventing double booking

Seat availability is protected at the database level.

An active seat has a unique `(flight_id, seat_number)` constraint. When two requests attempt to reserve the same seat concurrently, only one can successfully create the active-seat record.

The resulting database constraint violation is translated into a `409 Conflict` response.

This is preferable to relying only on an application-level "check then insert", which would still be vulnerable to a race condition between concurrent requests.

### Booking confirmation and expiration

Confirmation checks that the booking is still in the `HELD` state and that its hold has not expired.

If the hold has expired, the booking is marked as `EXPIRED`, its seat is released, and the request is rejected.

The confirmation and expiration operations are transactional so that the booking and seat state are updated together.

### Cancellation

Both held and confirmed bookings can be cancelled.

Cancelling a booking releases its active seat.

Expired or already cancelled bookings cannot be cancelled again.

### Flight deletion

Flights that do not have bookings can be deleted.

A flight with bookings cannot be deleted and returns `409 Conflict`. This prevents existing reservations from becoming inconsistent with the flight they belong to.

### Examples

Examples for each of the endpoints can be found in the **requests.http** file in the project root.

## Error handling

The API uses a small number of meaningful application exceptions:

* `ResourceNotFoundException` → `404 Not Found`
* `ConflictException` → `409 Conflict`

Examples of conflicts include:

* booking window has closed
* seat is already booked
* booking has expired
* booking cannot be confirmed in its current state
* flight cannot be deleted because it has bookings

## Persistence

H2 is used as the database because it keeps the application easy to run without requiring external infrastructure.

The database is configured with `create-drop`, so the data is intentionally not persistent between application restarts.

For a production system, I would use a persistent relational database such as MariaDB.

## Scope and trade-offs

This project intentionally does not implement production concerns that were outside the scope of the assessment:

* **Authentication and authorization** — OAuth2/OIDC with role-based access control.
* **Persistent infrastructure** — as mentioned above, a managed relational database such as MariaDB, with backups and migrations.
* **Monitoring and observability** — centralized logging, metrics, distributed tracing, and health checks.

The goal was to keep the implementation small while making the important booking and concurrency rules explicit and reliable.


## Additional endpoint

The following endpoint was added because it is useful for inspecting seat availability:

```http
GET /flights/{id}/seats
```

It provides a simple way to see which seats are currently available.
