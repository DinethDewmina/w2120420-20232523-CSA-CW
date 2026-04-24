# w2120420-20232523-CSA-CW
Smart Campus Infrastructure & IoT Management API

This project delivers a functional JAX-RS web service designed to manage campus rooms and their associated IoT sensors. Built on the Jersey framework with an integrated Grizzly container, the system handles data persistence in-memory through synchronized collections. The API features professional JSON error mapping, telemetry validation, and automated request auditing.

See the demo here link - https://youtu.be/4X6iuj797hw

##API Overview

Base Endpoint: http://localhost:8080/api/v1

Root Discovery: GET /api/v1

Room Management: GET /api/v1/rooms, POST /api/v1/rooms

Room Details: GET /api/v1/rooms/{roomId}, DELETE /api/v1/rooms/{roomId}

Sensor Management: GET /api/v1/sensors, POST /api/v1/sensors

Sensor Details: GET /api/v1/sensors/{sensorId}

Telemetry History: GET /api/v1/sensors/{sensorId}/readings, POST /api/v1/sensors/{sensorId}/readings

Specific Readings: GET /api/v1/sensors/{sensorId}/readings/{readingId}

Diagnostic Tool: GET /api/v1/diagnostics/failure-test

##Design Notes

The architecture follows a hierarchical domain model where rooms act as containers for sensors, which in turn generate historical readings.

Standardized Responses: All interactions—including success and error states—return standardized JSON payloads using a consistent envelope.

Referential Integrity: Deleting a room is blocked (409 Conflict) if it still contains sensors to prevent orphaned data.

Validation: Attempting to register a sensor to a non-existent room ID triggers a 404 Not Found, ensuring all sensors are linked to a valid parent.

Maintenance Logic: Telemetry is rejected (403 Forbidden) if the target sensor is currently set to a MAINTENANCE status.

Security: A global exception provider intercepts raw failures to keep internal stack traces hidden from the client.

Grizzly Configuration: The server is configured to prevent the container from stripping JSON bodies from error responses, ensuring the client always receives the errorMessage.

Auditing: A JAX-RS filter captures and logs the HTTP method, URI, and response status for every transaction.

Build And Run

Ensure your environment has Maven and JDK 11 (or higher) configured. Open your terminal in the project's base directory.

Bash
#To compile and run tests:
mvn clean test

Bash
#To launch the server:
mvn exec:java

#Access the live API at:
http://localhost:8080/api/v1

##Sample curlCommands

#System Discovery:

Bash
curl -i http://localhost:8080/api/v1/

#Register a Room:

Bash
curl -i -X POST http://localhost:8080/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"ROOM-101","name":"Advanced Computing Lab","capacity":35}'

#Fetch All Rooms:

Bash
curl -i http://localhost:8080/api/v1/rooms

#Register a Sensor:

Bash
curl -i -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"S-TEMP-01","type":"TEMPERATURE","status":"ACTIVE","roomId":"ROOM-101"}'

#Filter Sensors by Type:

Bash
curl -i "http://localhost:8080/api/v1/sensors?type=TEMPERATURE"

#Submit a Reading (ID and Timestamp auto-generated):

Bash
curl -i -X POST http://localhost:8080/api/v1/sensors/S-TEMP-01/readings \
-H "Content-Type: application/json" \
-d '{"value":22.5}'

#View Reading History:

Bash
curl -i http://localhost:8080/api/v1/sensors/S-TEMP-01/readings

#Demonstrate Conflict Rule (Room with Sensors):

Bash
curl -i -X DELETE http://localhost:8080/api/v1/rooms/ROOM-101

#Demonstrate Missing Linked Resource:

Bash
curl -i -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"S-FAIL","type":"CO2","roomId":"GHOST-ROOM"}'

#Demonstrate Maintenance Mode Restriction:

Bash
#First, create a sensor in MAINTENANCE mode:
curl -i -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"S-CO2-01","type":"CO2","status":"MAINTENANCE","roomId":"ROOM-101"}'

#Attempt to add a reading (Should fail with 403):
curl -i -X POST http://localhost:8080/api/v1/sensors/S-CO2-01/readings \
-H "Content-Type: application/json" \
-d '{"value":450.0}'

#Demonstrate Global Error Handler:

Bash
curl -i http://localhost:8080/api/v1/diagnostics/failure-test

##Coursework Report Answers

1.1 Resource Lifecycle

By default, JAX-RS resources operate under a per-request lifecycle, meaning instances are created for every individual call. To maintain a consistent state across the campus, this project uses a singleton repository. This design ensures that the shared in-memory data remains persistent and that thread-safe operations prevent data corruption during simultaneous user requests.

1.2 Hypermedia / HATEOAS

Hypermedia enhances API discoverability by providing the client with context-aware links within the response. This "engine of application state" allows clients to navigate the system without having every URL pre-defined. It decouples the client from the server’s URI structure, making the API more flexible and easier to update over time.

2.1 IDs Only vs Full Room Objects

Providing only IDs results in a lightweight, high-performance payload that conserves bandwidth. However, including full room objects is often better for user interfaces as it provides all necessary metadata (like capacity and names) in a single transaction. This "eager" approach reduces the total number of round-trips the client must make to the server.

2.2 DELETE Idempotency

The DELETE method is idempotent because the final state of the server remains the same regardless of how many times the request is repeated. After the initial successful removal, the resource no longer exists. While subsequent calls return a 404 Not Found in this implementation, the outcome—the absence of that resource—is unchanged, satisfying the idempotency requirement.

3.1 @Consumes(MediaType.APPLICATION_JSON)

This annotation acts as a gatekeeper, ensuring the endpoint only processes requests with a JSON content type. If a client attempts to send data in a different format (like XML), JAX-RS automatically rejects the request with a 415 Unsupported Media Type status, protecting the resource logic from incompatible data formats.

3.2 Why @QueryParam Is Better For Filtering

Using query parameters (e.g., ?type=CO2) is the standard for filtering because it treats the filter as a modifier of the existing collection rather than a new resource path. This approach is more scalable, allows for the easy combination of multiple filters (like ?type=CO2&status=ACTIVE), and keeps the URI structure clean and logical.

4.1 Sub-Resource Locator Benefits

Sub-resource locators allow for a more modular and organized codebase. By delegating nested operations (like sensor readings) to a dedicated child resource class, we avoid creating bloated controllers. This separation makes the code easier to test, read, and maintain as the complexity of the API grows.

4.2 Historical Data Consistency

Data integrity is maintained by ensuring that every telemetry submission to /readings immediately reflects in the parent sensor's state. By updating the currentValue field in tandem with the history log, the API provides a consistent "real-time" view of the sensor without requiring the client to manually aggregate historical data.

5.2 Choosing 404 for Linked Resources

This project utilizes 404 Not Found when a child resource (Sensor) references a non-existent parent (Room). This aligns with RESTful principles where the parent container is a resource that must be located before a child can be attached. It provides a clear signal that the request reached the right endpoint, but the targeted relationship could not be established.

5.4 Stack Trace Exposure Risks

Leaking stack traces is a significant security vulnerability. It provides potential attackers with a roadmap of the server’s internal architecture, including framework versions, class names, and file paths. This metadata can be used to identify known exploits, making reconnaissance much easier for malicious actors.

5.5 Why Filters Are Better For Logging

JAX-RS filters provide a centralized, non-intrusive way to handle "cross-cutting concerns" like logging. Instead of manually writing log statements in every resource method, a filter ensures that every request and response is recorded automatically. This guarantees consistency across the entire API and keeps the business logic clean.
