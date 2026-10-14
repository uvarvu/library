# Book library singleton example

A small Java 27 / Spring Boot 4 REST application demonstrating a thread-safe,
in-memory singleton. It uses immutable records, constructor injection, Jakarta
Validation, OpenAPI documentation, AOP request logging, and Bucket4j rate limiting.

## The singleton

`Library` is a Spring `@Component`. The default bean scope is singleton: each
application context creates one library and injects that same instance into its
services. It is not a JVM-wide enum singleton; independent application contexts
and ordinary `new Library()` instances have independent state. Spring dependency
injection manages this lifetime rather than defeating the singleton pattern.

Singleton scope alone does not make mutable state thread-safe. `Library` stores
immutable `Book` records in a `ConcurrentHashMap`. Adds use `putIfAbsent`, removals
use atomic `remove`, and borrowing/returning use `compute` to serialize updates
for each ISBN. Returned books are snapshots; author searches return immutable
lists sorted by ISBN. A search across multiple books is not a transactional
snapshot during concurrent writes.

All data is lost on restart. Multiple application instances each have their own
library and rate limiter. This example intentionally has no database or loan
tracking: returning any known ISBN increments its available copies, even without
an outstanding loan.

## Build and run

Install a JDK 27 or newer and set `JAVA_HOME` if needed. The Maven wrapper downloads
Maven 3.9.11 on its first run; a separate Maven installation is unnecessary.

```sh
./mvnw clean verify
./mvnw spring-boot:run
```

On Windows use `mvnw.cmd`. An installed Maven 3.9+ also works. To run the packaged
application after building:

```sh
java -jar target/library-1.jar
```

The server listens on port **8019**. Swagger UI is available at
<http://localhost:8019/swagger-ui/index.html> and OpenAPI JSON at
<http://localhost:8019/v3/api-docs>.

## API

| Method | Path | Request |
| --- | --- | --- |
| PUT | `/library/add-book` | Book JSON |
| DELETE | `/library/remove-book` | Book JSON (only ISBN identifies the book) |
| GET | `/library/find-book-by-isbn/{isbn}` | — |
| GET | `/library/find-books-by-author/{author}` | — |
| POST | `/library/borrow-book` | `{"isbn":"978-0241578186"}` |
| POST | `/library/return-book` | `{"isbn":"978-0241578186"}` |

Example book JSON:

```json
{
  "isbn": "978-0241578186",
  "title": "The Twits",
  "author": "Roald Dahl",
  "publicationYear": 2022,
  "availableCopies": 10
}
```

ISBN, title, and author must be nonblank. Publication year must be between 1500
and the current year, and an incoming book must have at least one available copy.
ISBNs are identifiers; checksum validation is not performed. Author matching is
exact and case sensitive. Existing endpoint paths, success messages, and error
response formats are retained.

Validation errors return 400; missing books/authors return 404; duplicate books
and exhausted copies return 409. The shared application-wide rate limiter returns
429 after its tokens run out. Defaults are 20 tokens, refilling 10 tokens every
5 seconds (greedy refill). Configure `library.rate-limit.capacity`,
`library.rate-limit.refill-tokens`, and `library.rate-limit.refill-seconds` in
`application.properties` or through Spring Boot environment variables.

## Java modernization (21 → 27)

The compiler release, minimum build JDK, and CI JDK are now **27**. No preview
features or `--enable-preview` flags are required. Java 27 is the latest stable
release as of October 2026; Java 25 is the latest LTS release.

Features applied to this example:

- **Java 22 unnamed variables and patterns:** map updates use `(_, book)` because
  the key is unused. Request logging extracts ISBNs with record patterns such as
  `BookRequest(var isbn, _, _, _, _)`, explicitly ignoring the remaining fields.
  Record patterns and pattern switches were finalized in Java 21; discarding
  components with `_` became permanent in Java 22.
- **Java 25 flexible constructor bodies:** `LibraryException` checks its error
  argument for null before calling `super(...)`, producing a clear diagnostic.
- **Virtual-thread HTTP execution:** `spring.threads.virtual.enabled=true` enables
  virtual threads in the embedded server. Virtual threads were finalized in
  Java 21; Java 24 removed pinning caused by ordinary synchronized blocks and
  methods. A real HTTP test verifies that requests execute on virtual threads.
  This is a concurrency example, not a claim of faster in-memory operations.
- **Test instrumentation:** Surefire loads Mockito as a startup Java agent,
  following Mockito's guidance for modern JDKs rather than relying on dynamic
  self-attachment.

Other finalized additions, including Java 25 scoped values and module imports,
and Java 24 stream gatherers, do not simplify this small application's current
operations. Compact source files and instance main methods are unnecessary for
its Spring Boot entry point. Structured concurrency and primitive patterns are
still preview features in Java 27, so they are not enabled.

Spring Boot 4.1.1's published compatibility range currently ends at Java 26.
This project targets Java 27 and verifies its own tests and packaged application
on that JDK; those checks do not extend the framework's official support range.
The Java 27 baseline is intentional for this learning example and is not an LTS
baseline. Dependencies remain on stable releases.

References: [Java 27 downloads](https://www.oracle.com/java/technologies/downloads/),
[language changes](https://docs.oracle.com/en/java/javase/27/language/java-language-changes-summary.html),
[virtual thread pinning improvements](https://openjdk.org/jeps/491),
[Spring Boot requirements](https://docs.spring.io/spring-boot/system-requirements.html).

## Tests

`./mvnw verify` runs independent JUnit tests, including concurrent add/borrow/return
operations using virtual threads, singleton identity, request validation,
rate limiting, and OpenAPI generation. HTTP tests reset both library and bucket
state between cases.

The optional Bruno collection is in `src/test/LibraryTests`. With the application
running and Node.js installed:

```sh
npm install -g @usebruno/cli
cd src/test/LibraryTests
bru run
```

Run the collection against a freshly started application; its requests form a
sequence and change the in-memory library.
