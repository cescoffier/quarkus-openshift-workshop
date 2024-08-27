# Creating a REST/HTTP Microservice

At the heart of the Super Hero application comes Heroes.
We need to expose a REST API allowing CRUD operations on Super Heroes.
This microservice is, let’s say, a *classical* microservice.
It uses HTTP to expose a REST API and internally store data into a database.
This service will be used by the *fight* microservice.

![rest-physical-architecture](diagrams/rest-physical-architecture.svg)


In the following sections, you learn:
-   how to create a new Quarkus application using Red Hat DeveloperHub.
-   how to implement REST API using JAX-RS.
-   how to compose your application using CDI beans.
-   how to access your database using Hibernate with Panache.
-   how to use transactions.
-   how to enable OpenAPI and Swagger-UI.

But first, let’s describe our service. The Super Heroes microservice stores super-heroes, with their names, powers, and so on.
The REST API allows adding, removing, listing, and picking a random hero from the stored set.
Nothing outstanding but a good first step to discover Quarkus.

## Bootstrapping the Hero REST Endpoint

First thing first, we need a project.
That's what your are going to see in this section.

Using RH developer hub UI create a quarkus application with default extensions.


## Directory Structure

Once you bootstrap the project, you get the following directory structure with a few Java classes and other artifacts :

![hero-directory-structure](diagrams/hero-directory-structure.svg)

It generates the following in the hero-service folder:

* the Maven structure with a `pom.xml`
* an `io.quarkus.workshop.superheroes.hero.HeroResource` resource exposed on `/api/heroes`
* an associated unit test `HeroResourceTest`
* the landing page `index.html` that is accessible after starting the application
* example `Dockerfile` files for both native and jvm modes in `src/main/docker`
* the `application.properties` configuration file

Once generated, look at the `pom.xml`.
You will find the import of the Quarkus BOM, allowing you to omit the version on the different Quarkus dependencies.
In addition, you can see the `quarkus-maven-plugin` responsible of the packaging of the application and also providing the development mode.

[//]: # (To do: insert pom relevant fragments here )

If we focus on the dependencies section, you can see the extension allowing the development of REST applications:

[//]: # (To do: insert the extensions fragment )

## The JAX-RS Resource

During the project creation, the `HeroResource.java` file has been created with the following content:

```java
package io.quarkus.workshop.superheroes.hero;

@Path("/api/heroes")
public class HeroResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}
```

It's a very simple REST endpoint, returning "hello" to requests on `/api/heroes`.

## Running the Application

Now we are ready to run our application.Use: `./mvnw quarkus:dev`:

```shell
$ ./mvnw quarkus:dev
[INFO] Scanning for projects...
[INFO]
[INFO] -------------< io.quarkus.workshop.super-heroes:rest-hero >-------------
[INFO] Building rest-hero 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- quarkus-maven-plugin:1.9.2.Final:dev (default-cli) @ rest-hero ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 2 resources
[INFO] Nothing to compile - all classes are up to date
Listening for transport dt_socket at address: 5005
__  ____  __  _____   ___  __ ____  ______
--/ __ \/ / / / _ | / _ \/ //_/ / / / __/
-/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/
2020-11-16 10:01:51,331 INFO  [io.quarkus] (Quarkus Main Thread) rest-hero 1.0-SNAPSHOT on JVM (powered by Quarkus 1.9.2.Final) started in 3.797s. Listening on: http://0.0.0.0:8080
2020-11-16 10:01:51,343 INFO  [io.quarkus] (Quarkus Main Thread) Profile dev activated. Live Coding activated.
2020-11-16 10:01:51,343 INFO  [io.quarkus] (Quarkus Main Thread) Installed features: [cdi, resteasy]
```

Then check that the endpoint returns `hello` as expected:

```shell
$ curl $URL/api/heroes
hello
```

Alternatively, you can open $URL/api/heroes in your browser.

## Development Mode

`quarkus:dev` runs Quarkus in development mode.
This enables hot deployment with background compilation, which means that when you modify your Java files and/or your resource files and invoke a REST endpoint (i.e. cUrl command or refresh your browser), these changes will automatically take effect.
This works too for resource files like the configuration property and HTML files.
Refreshing the browser triggers a scan of the workspace, and if any changes are detected, the Java files are recompiled and the application is redeployed;
your request is then serviced by the redeployed application.
If there are any issues with compilation or deployment an error page will let you know.

The development mode also allows debugging and listens for a debugger on port 5005.
If you want to wait for the debugger to attach before running you can pass `-Dsuspend=true` on the command line.
If you don't want the debugger at all you can use `-Ddebug=false`.

Alright, time to change some code.
Open your favorite IDE and import the project.
To check that the hot reload is working, update the method `HeroResource.hello()` by returning the String "hello hero".
Now, execute the cUrl command again, the output has changed without you to having to stop and restart Quarkus:


## Testing the Application

All right, so far so good, but wouldn't it be better with a few tests, just in case.

In the generated `pom.xml` file, you can see 2 test dependencies:

[//]: # (To do: insert the testing dep)

Quarkus supports Junit 4 and Junit 5 tests.
In the generated project, we use Junit 5.
Because of this, the version of the Surefire Maven Plugin must be set, as the default version does not support Junit 5:

[//]: # (To do: insert the testing dep)

We also set the `java.util.logging` system property to make sure tests will use the correct log manager.

The generated project contains a simple test in `HeroResourceTest.java`.

```java
package io.quarkus.workshop.superheroes.hero;

@QuarkusTest
public class HeroResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/heroes")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }
}
```

By using the `QuarkusTest` runner, the `HeroResourceTest` class instructs JUnit to start the application before the tests.
Then, the `testHelloEndpoint` method checks the HTTP response status code and content.
Notice that these tests use RestAssured, but feel free to use your favorite library.


Execute it with `./mvnw test` or from your IDE.
It fails! It's expected, you changed the output of `HeroResource.hello()` earlier.
Adjust the test body condition accordingly.

## Packaging and Running the Application

The application is packaged using `./mvnw package`.
It produces 2 jar files in `/target`:

* `rest-hero-1.0-SNAPSHOT.jar` : containing just the classes and resources of the projects, it's the regular artifact produced by the Maven build;
* `rest-hero-1.0-SNAPSHOT-runner.jar` : being an executable jar.
  Be aware that it's not an über-jar as the dependencies are copied into the `target/lib` directory.

You can run the application using: `java -jar target/rest-hero-1.0-SNAPSHOT-runner.jar`.

**NOTE**
Before running the application, don't forget to stop the hot reload mode (hit CTRL+C), or you will have a port conflict.

