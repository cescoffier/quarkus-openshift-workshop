# Creating the Villain Microservice

New microservice, new project! 
In this section we will see the counterpart of the Hero microservice: the Villain microservice.
The Villain REST Endpoint is really similar to the Hero Endpoint but has been developed using the Spring compatibility layer provided by Quarkus.
While users are encouraged to use Quarkus extensions, this compatibility layer is provided to make developing new applications with Quarkus a natural getting started experience.


## Bootstrapping the Villain Rest Endpoint

==The code is now fully available and you will not have to write any of this microservice.==
Like we did for hero-service, we will create the villain-service from a Red Hat Developer Hub template, which will automatically pull the complete code from the GitHub repository.

==So, please proceed with following steps==

* ==Navigate to the _Create Option_: in the left-hand menu of the RHDH, click on the "Create" option==.

![create-service-rhdh.png](images%2Fcreate-service-rhdh.png)

* ==Select the template: you'll see a list of available templates. Choose the `OpenCodeQuest - Quarkus microservice with Spring MVC and Spring Data` template from the list==.

![villain-template-rhdh.png](images%2Fvillain-template-rhdh.png)

* ==Select your cluster name, check the reference in the current url. The rest of the fields will be pre-filled by default==.


* ==Click Next button until a summary is shown==

![villain-summary-rhdh.png](images%2Fvillain-summary-rhdh.png)

* ==Review the configuration. Then, click on `Create`==.

* ==If everything went well, you should see the following successful page. Click on the `Open Component in Catalog`==:

![villain-success-rhdh.png](images%2Fvillain-success-rhdh.png)

* ==Once in the villain-service component home page, you can launch the Dev Spaces opening by clicking the link OpenShift Dev Spaces (VS Code)==

![villain-home-page-rhdh.png](images%2Fvillain-home-page-rhdh.png)


## The Villain Service

At the heart of the Super Hero application come also  _villains_.

We need to expose a REST API allowing CRUD operations on _super heroes_.
This microservice is also a *classical* microservice.
It uses HTTP to expose a REST API, and it internally stores data into a database.

This service will be used by the *fight* microservice.

==The code is fully provided. You will not have to write any of this microservice.==

## Directory Structure

Once you bootstrap the project, you get the following directory structure with a few Java classes and other artifacts:

![villain-directory-structure](target/villain-directory-structure.svg)

You get the following in the `villain-service` folder:

* the Maven structure with a `pom.xml`
* an `io.quarkus.workshop.villain.VillainController` controller exposed on `/api/heroes`
* an associated unit test `VillainControllerTest`
* example `Dockerfile` files for both native and jvm modes in `src/main/docker`
* the `application.properties` configuration file

==Look at the `pom.xml`.==

The `pom.xml` is basically the same than for heroes apart that it contains a few more dependencies: `quarkus-spring-web` and `quarkus-spring-data`.

The Quarkus Spring compatibility extensions map Spring APIs to APIs in existing extensions that have already been optimized for fast startup, reduced memory utilization and native compilation, like RestEasy and CDI.

==Be aware that Quarkus Spring compatibility extensions do not utilize the Spring application context.==
For this reason, attempting to utilize additional Spring libraries will likely not work.

## The Controller

We get a rest controller, the `VillainController.java` with the following content:

```java linenums="1"
{{ insert('villain-service/src/main/java/io/quarkus/workshop/villain/VillainController.java') }}
```

This controller exposes CRUD operations to "villains" and leverages Spring Web and Spring Data JPA annotations for handling HTTP requests and transactions:

1. **@RestController**: Marks the class as a RESTful controller where every method returns a JSON response instead of a view. 

2. **@RequestMapping**: Specifies the base URL path (`/api/villains`) for all the endpoints within this controller. It replaces the standard JAX-RS `@Path`

3. **@GetMapping, @PostMapping, @PutMapping, @DeleteMapping**: These annotations define HTTP methods (GET, POST, PUT, DELETE) to map specific endpoints to CRUD operations.

Not Spring specific annotations but used like in the hero-service:

4. **@Transactional**: Ensures that the methods annotated with it are executed within a transaction context, which automatically commits or rolls back the transaction.

5. **@Valid**: Validates the input entity (`Villain`) based on the constraints defined on its fields.

6. **@Context**: Injects `UriInfo`, which provides contextual information about the current URI to help in creating new URIs for created resources.

7. **@RunOnVirtualThread**: Runs the controller on a virtual thread for better concurrency management. This is specific to the SmallRye library.

## Accessing Database

As everything is in place the `import.sql` file contains already all SQL statements to populate the villain database.

### The Villain repository

As we are following a Spring Data programming model with JPA,we need to extend the JPA specific Repository interface, JpaRepository. 
This will enable Quarkus to find this interface and automatically create an implementation for it.
By extending the interface, we get the most relevant CRUD methods automatically. 

You can check the `VillainRepository.java` code. It also contains a more specific method to retrieve a villain randomly from database:

```java linenums="1"
{{ insert('villain-service/src/main/java/io/quarkus/workshop/villain/VillainRepository.java', 'springSpecificMethod') }}
```

### The Villain entity

Finally we have an entity class representing the villains.

```java linenums="1"
{{ insert('villain-service/src/main/java/io/quarkus/workshop/villain/Villain.java') }}
```

### Tests

As in the case of the heroes, the tests could not be missing here. 
A test class is provided and contains the basic tests to ensure that the villain microservice works correctly.

You can check that everything works fine by starting the application in development mode.

run one of the following commands:

`./mvnw quarkus:dev`

or

`quarkus dev`

Note that the tests have been successful run. 

==Also, you can curl the villains endopoint and you should get lots of villains==

```shell
curl http://localhost:8080/api/villains
```

## Deploy the Villain microservice

To deploy the Villain service, ==remember to perform a commit&push of the code== (there is already a change on `application.properties` waiting to be pushed). 

==You can get some help [here](from-git-to-openshif.md)==

## Houston, we've got a problem

At this stage, you should observe an issue during the CI pipeline. The step **acs-image-check** (responsible of verifying that we are not violating any policies in our cluster) is now failing.

![acs-check](images/acs-check.png)

==Click the task to get the logs and understand what policy has been violated==

![acs-image-check-error](images/acs-image-check-error.png)

As you can see, it looks like a **SuperVillain**  has introduced a bad dependency in our code ! 

==Open the `pom.xml` file in DevSpaces IDE==

![devspaces-CVE](images/devspaces-CVE.png)

Thanks to the [Red Hat Dependency Analytics](https://marketplace.visualstudio.com/items?itemName=redhat.fabric8-analytics) IDE extension, you can even see that the dependency is automatically underlined suggesting there's an issue with it.

==Remove the dependency as it's not needed in our code==

## Deploy the Villain microservice after fixing the issue
Once you have corrected the dependency issue and comited your code, ==you can tag and release== your code in GitLab to promote in preprod and prod.


## Stop your DevSpaces Workspace
As you now have fully deployed your microservice, you do not need anymore the DevSpaces workspace so let's just stop it to preserve resources.

==Click the grey button **"><"** on the bottom left of DevSpaces IDE==

![devspaces-stop1](images/devspaces-stop1.png)

==Then Click "Dev Spaces : Stop Workspace==

![devspaces-stop2](images/devspaces-stop2.png)