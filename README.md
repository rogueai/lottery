# lottery

A simple Lottery Rest API.

## Requirements
The project requires:
* JDK 1.8
* Maven 3.2+

## Build
To build and run the project:
```
$ cd lottery/
$ mvn spring-boot:run

```
The server is then accessible at: http://localhost:8080

## Rest API
```
/ticket		    POST		Create a ticket
/ticket		    GET		  Return list of tickets
/ticket/{id}	GET		  Get individual ticket
/ticket/{id}	PUT		  Amend ticket lines
/status/{id}	PUT		  Retrieve status of ticket
```
