# Helidon SE Media MultiPart Example

This example demonstrates how to use `MultiPartSupport` with both the `WebServer`
 and `WebClient` APIs.

This project implements a simple file service web application that supports uploading
and downloading files. The unit test uses the `WebClient` API to test the endpoints.

## Build

```shell
mvn package
```

## Run

First, start the server:

```shell
java -jar target/helidon-examples-media-multipart.jar
```

Then open <http://localhost:8080/ui> in your browser.
