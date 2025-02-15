
# Helidon SE CORS Example

This example shows a simple greeting application, similar to the one from the 
Helidon SE QuickStart, enhanced with CORS support.

Look at the `resources/application.yaml` file and notice the `restrictive-cors` 
section. (We'll come back to the `cors` section later.) The `Main#corsSupportForGreeting` method loads this 
configuration and uses it to set up CORS for the application's endpoints. 

Near the end of the `resources/logging.properties` file, a commented line would turn on `FINE
` logging that would reveal how the Helidon CORS support makes it decisions. To see that logging,
uncomment that line and then package and run the application.
  
## Build and run

```shell
mvn package
java -jar target/helidon-examples-cors.jar
```

## Using the app endpoints as with the "classic" greeting app

These normal greeting app endpoints work just as in the original greeting app:

```shell
curl -X GET http://localhost:8080/greet
#{"message":"Hello World!"}

curl -X GET http://localhost:8080/greet/Joe
#{"message":"Hello Joe!"}

curl -X PUT -H "Content-Type: application/json" -d '{"greeting" : "Hola"}' http://localhost:8080/greet/greeting

curl -X GET http://localhost:8080/greet/Jose
#{"message":"Hola Jose!"}
```

## Using CORS

### Sending "simple" CORS requests

The following requests illustrate the CORS protocol with the example app.

By setting `Origin` and `Host` headers that do not indicate the same system we trigger CORS processing in the
 server:

```shell
# Follow the CORS protocol for GET
curl -i -X GET -H "Origin: http://foo.com" -H "Host: here.com" http://localhost:8080/greet

```
```text
HTTP/1.1 200 OK
Access-Control-Allow-Origin: *
Content-Type: application/json
Date: Thu, 30 Apr 2020 17:25:51 -0500
Vary: Origin
connection: keep-alive
content-length: 27

{"greeting":"Hola World!"}
```
Note the new headers `Access-Control-Allow-Origin` and `Vary` in the response.

The same happens for a `GET` requesting a personalized greeting (by passing the name of the
 person to be greeted):
```shell
curl -i -X GET -H "Origin: http://foo.com" -H "Host: here.com" http://localhost:8080/greet/Joe

```
```text
HTTP/1.1 200 OK
Date: Wed, 31 Jan 2024 11:58:06 +0100
Access-Control-Allow-Origin: *
Connection: keep-alive
Content-Length: 24
Content-Type: application/json
Vary: ORIGIN

{"greeting":"Hola Joe!"}
```
These two `GET` requests work because the `Main#corsSupportForGreeting` method adds a default `CrossOriginConfig` to the
`CorsSupport.Builder` it sets up. This is in addition to adding a `CrossOriginConfig` based on the `restrictive-cors` 
configuration in `application.yaml` we looked at earlier.

These are what CORS calls "simple" requests; the CORS protocol for these adds headers to the request and response which
the client and server exchange anyway.

### "Non-simple" CORS requests

The CORS protocol requires the client to send a _pre-flight_ request before sending a request
 that changes state on the server, such as `PUT` or `DELETE`, and to check the returned status
  and headers to make sure the server is willing to accept the actual request. CORS refers to such `PUT` and `DELETE`
  requests as "non-simple" ones.
   
This command sends a pre-flight `OPTIONS` request to see if the server will accept a subsequent `PUT` request from the
specified origin to change the greeting:
```shell
curl -i -X OPTIONS \
    -H "Access-Control-Request-Method: PUT" \
    -H "Origin: http://foo.com" \
    -H "Host: here.com" \
    http://localhost:8080/greet/greeting
```    
```text
HTTP/1.1 200 OK
Date: Wed, 31 Jan 2024 12:00:15 +0100
Access-Control-Allow-Methods: PUT
Access-Control-Allow-Origin: http://foo.com
Access-Control-Max-Age: 3600
Connection: keep-alive
Content-Length: 0
```
The successful status and the returned `Access-Control-Allow-xxx` headers indicate that the
 server accepted the pre-flight request. That means it is OK for us to send `PUT` request to perform the actual change 
 of greeting. (See below for how the server rejects a pre-flight request.)
```shell
curl -i -X PUT \
    -H "Origin: http://foo.com" \
    -H "Host: here.com" \
    -H "Access-Control-Allow-Methods: PUT" \
    -H "Access-Control-Allow-Origin: http://foo.com" \
    -H "Content-Type: application/json" \
    -d "{ \"greeting\" : \"Cheers\" }" \
    http://localhost:8080/greet/greeting
```
```text
HTTP/1.1 204 No Content
Date: Wed, 31 Jan 2024 12:01:45 +0100
Access-Control-Allow-Origin: http://foo.com
Connection: keep-alive
Content-Length: 0
Vary: ORIGIN
```
And we run one more `GET` to observe the change in the greeting:
```shell
curl -i -X GET -H "Origin: http://foo.com" -H "Host: here.com" http://localhost:8080/greet/Joe
```
```text
HTTP/1.1 200 OK
Date: Wed, 31 Jan 2024 12:02:13 +0100
Access-Control-Allow-Origin: *
Connection: keep-alive
Content-Length: 26
Content-Type: application/json
Vary: ORIGIN

{"greeting":"Cheers Joe!"}
```
Note that the tests in the example `MainTest` class follow these same steps.

## Using overrides

The `Main#corsSupportForGreeting` method loads override settings for any other CORS set-up if the config contains a 
"cors" section. (That section is initially commented out in the example `application.yaml` file.) Not all applications 
need this feature, but the example shows how easy it is to add.

With the same server running, repeat the `OPTIONS` request from above, but change the `Origin` header to refer to 
`other.com`:
```shell
curl -i -X OPTIONS \
    -H "Access-Control-Request-Method: PUT" \
    -H "Origin: http://other.com" \
    -H "Host: here.com" \
    http://localhost:8080/greet/greeting
```
```text
HTTP/1.1 403 CORS origin is not in allowed list
Date: Wed, 31 Jan 2024 12:02:51 +0100
Connection: keep-alive
Content-Length: 0
```
This fails because the app set up CORS using the "restrictive-cors" configuration in `application.yaml` which allows 
sharing only with `foo.com` and `there.com`, not with `other.com`. 

Stop the running app, uncomment the commented section at the end of `application.yaml`, and build and run the app again.
```shell
mvn package
java  -jar target/helidon-examples-cors.jar
```
Send the previous `OPTIONS` request again and note the successful result:
```text
HTTP/1.1 200 OK
Date: Wed, 31 Jan 2024 12:05:36 +0100
Access-Control-Allow-Methods: PUT
Access-Control-Allow-Origin: http://other.com
Access-Control-Max-Age: 3600
Connection: keep-alive
Content-Length: 0
```
The application uses the now-uncommented portion of the config file to override the rest of the CORS set-up. You can 
choose whatever key name you want for the override. Just make sure you tell your end users whatever the key is your app 
uses for overrides.

A real application might read the normal configuration (`restrictive-cors`) from one config source and any overrides 
from another. This example combines them in one config source just for simplicity.
