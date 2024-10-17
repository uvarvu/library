# Book Library REST project

## Building and Running:

These commands can be used to build the application and start it:

1. `mvn clean install` to build
2. `mvn exec:java` to start

## Running Test Cases (Node.js is a prereq):
1. `npm install -g @usebruno/cli` to install the Bruno CLI
2. `bru run` from `src/test/LibraryTests` to run the test cases
3. If you want to view the test cases themselves then you can find Bruno at https://www.usebruno.com and then use it to open the collection in `src/test`.

## Documentation

The API documentation can be found in two places:

1. `http://localhost:8019/v3/api-docs` for the Open API documentation
2. `http://localhost:8019/swagger-ui/index.html` for Swagger

## Assumptions
1.  Users cannot upload the same book twice, an error is returned for this scenario.
2.  Errors are returned if books cannot be found by ISBN or author.
3.  Errors are returned if books are borrowed/returned that don't belong to the library
4.  Errors are returned if someone tries to borrow a book and there are no copies left
5.  If someone returns a book that isn't from the library but matches one that's offered then it will be accepted!

## Additional Features
1.  I decided to implement the Library cache using an enum as more traditional methods such as singleton objects can be defeated using Spring's dependency injection tools.
2.  The Bruno collection contains tests and assertions to ensure the output of the rest API is correct.
3.  I implemented a validation exception handler to handle any cases where book attributes are incorrect.
4.  I used Spring AOP to add a logging aspect to the rest controller.
5.  Bucket4j is used for rate limiting.
