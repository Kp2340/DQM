# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.4/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.4/maven-plugin/build-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.4/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.4/reference/web/servlet.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

# Spring Boot + React Employee Management System

A full-stack application for managing employee data with CRUD operations and Excel export using Apache POI.

## Technologies
- Backend: Spring Boot, Spring Data JPA
- Frontend: React, Axios, Bootstrap
- Libraries: Apache POI
- Database: MySQL (or H2 for demo)

## Setup
1. Clone the repo: `git clone https://github.com/Kp2340/DQM.git`
2. Backend:
    - Navigate to `backend/`
    - Run `mvn spring-boot:run`
3. Frontend:
    - Navigate to `frontend/`
    - Run `npm install && npm start`
4. Open `http://localhost:3000` in your browser.

## Live Demo
[View Live Demo](https://your-app-url)

## Screenshots
![Home Page](screenshots/home.png)