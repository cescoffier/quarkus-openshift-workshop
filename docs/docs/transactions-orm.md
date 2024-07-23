# Transactions and ORM

The Hero API's role is to allow CRUD operations on Super Heroes.
In this module we will create a Hero entity and persist/update/delete/retrieve it from a Postgres database in a transactional way.

## Directory Structure

In this module we will add extra classes to the Hero API project.
You will end-up with the following directory structure:

![hero-transaction-orm-directory-structure](../../target/hero-transaction-orm-directory-structure.svg)

## Installing the PostgreSQL Dependency, Hibernate with Panache and Hibernate Validator

[//]: # (Review this part depending on the backstage Quarkus plugin: )

This microservice:

* interacts with a PostGreSQL database - so it needs a driver
* uses Hibernate with Panache - so need the dependency on it
* validates payloads and entities - so need a validator
* consumes and produces JSON - so we need a mapper

Hibernate ORM is the de-facto JPA implementation and offers you the full breadth of an Object Relational Mapper.
It makes complex mappings possible, but it does not make simple and common mappings trivial.
Hibernate ORM with Panache focuses on making your entities trivial and fun to write in Quarkus.footnote:[Panache https://github.com/quarkusio/quarkus/tree/master/extensions/panache]

Because JPA and Bean Validation work well together, we will use Bean Validation to constrain our business model.

To add the required dependencies, just run the following command:

```shell

$ ./mvnw quarkus:add-extension -Dextensions="jdbc-postgresql,hibernate-orm-panache,hibernate-validator,resteasy-jsonb"
```

From now on, you can choose to either edit your pom directly or use the `quarkus:add-extension` command.

## Hero Entity

To define a Panache entity, simply extend `PanacheEntity`, annotate it with `@Entity` and add your columns as public fields (no need to have getters and setters).
The `Hero` entity should look like this:


Notice that you can put all your JPA column annotations and Bean Validation constraint annotations on the public fields.

### Adding Operations

Thanks to Panache, once you have written the `Hero` entity, here are the most common operations you will be able to do:

```java
// creating a hero
Hero hero = new Hero();
hero.name = "Superman";
hero.level = 9;

// persist it
hero.persist();

// getting a list of all Hero entities
List<Hero> heroes = Hero.listAll();

// finding a specific hero by ID
hero = Hero.findById(id);

// counting all heroes
long countAll = Hero.count();
```

But we are missing a business method:
we need to return a random hero.
For that it's just a matter to add the following method to our `Hero.java` entity:


**NOTE**
You would need to add the following import statement if not done automatically by your IDE `import java.util.Random;`

## Configuring Hibernate

Quarkus development mode is really useful for applications that mix front end or services and database access.
We use `quarkus.hibernate-orm.database.generation=drop-and-create` in conjunction with `import.sql` so every change to your app and in particular to your entities, the database schema will be properly recreated and your data (stored in `import.sql`) will be used to repopulate it from scratch.
This is best to perfectly control your environment and works magic with Quarkus live reload mode:
your entity changes or any change to your `import.sql` is immediately picked up and the schema updated without restarting the application!

For that, make sure to have the following configuration in your `application.properties` (located in `src/main/resources`):


## HeroService Transactional Service

To manipulate the `Hero` entity we will develop a transactional `HeroService` class.
The idea is to wrap methods modifying the database (e.g. `entity.persist()`) within a transaction.
Marking a CDI bean method `@Transactional` will do that for you and make that method a transaction boundary.

`@Transactional` can be used to control transaction boundaries on any CDI bean at the method level or at the class level to ensure every method is transactional.
You can control whether and how the transaction is started with parameters on `@Transactional`:

* `@Transactional(REQUIRED)` (default): starts a transaction if none was started, stays with the existing one otherwise.
* `@Transactional(REQUIRES_NEW)`: starts a transaction if none was started ; if an existing one was started, suspends it and starts a new one for the boundary of that method.
* `@Transactional(MANDATORY)`: fails if no transaction was started ; works within the existing transaction otherwise.
* `@Transactional(SUPPORTS)`: if a transaction was started, joins it ; otherwise works with no transaction.
* `@Transactional(NOT_SUPPORTED)`: if a transaction was started, suspends it and works with no transaction for the boundary of the method ; otherwise works with no transaction.
* `@Transactional(NEVER)`: if a transaction was started, raises an exception ; otherwise works with no transaction.

Creates a new `HeroResource.java` file in the same package with the following content:

```java linenums="1"
{{ insert('src/main/java/io/quarkus/workshop/hero/Hero.java') }}
```

Notice that both methods that persist and update a hero, pass a `Hero` object as a parameter.
Thanks to the Bean Validation's `@Valid` annotation, the `Hero` object will be checked to see if it's valid or not.
It it's not, the transaction will be rollback-ed.

## Configuring the Datasource

Our project now requires a connection to a PostgreSQL database.
The main way of obtaining connections to a database is to use a datasource.
In Quarkus, the out of the box datasource and connection pooling implementation is Agroal.footnote:[Agroal https://agroal.github.io]

This is done in the `src/main/resources/application.properties` file.

icon:hand-o-right[role="red", size=2x] [red big]#Call to action#

Just add the following datasource configuration:


## HeroResource Endpoint

The `HeroResource` Endpoint was bootstrapped with only one method `hello()`.
We need to add extra methods that will allow CRUD operations on heroes.


Here are the new methods to add to the `HeroResource` class:


## Dependency Injection

Dependency injection in Quarkus is based on ArC which is a CDI-based dependency injection solution tailored for Quarkus' architecture.footnote:[ArC https://github.com/quarkusio/quarkus/tree/master/independent-projects/arc]
You can learn more about it in the Contexts and Dependency Injection guide.footnote:[Quarkus - Contexts and Dependency Injection https://quarkus.io/guides/cdi-reference.html]

ArC comes as a dependency of `quarkus-resteasy` so you already have it handy.
That's why you were able to use `@Inject` in the `HeroResource` to inject a reference to `HeroService`.

## Adding Data

To load some SQL statements when Hibernate ORM starts, add the following `import.sql` in the root of the `resources` directory.
It contains SQL statements terminated by a semicolon.
This is useful to have a data set ready for the tests or demos.

Ok, but that's just a few entries.
Download the SQL file {github-raw}/super-heroes/rest-hero/src/main/resources/import.sql[import.sql] and copy it under `src/main/resources`.
Now, you have around 500 heroes that will be loaded in the database.

If you didn't yet, start the application in dev mode:

```shell
$./mvnw quarkus:dev

```
**NOTE**
Consider copying the url mentioned by CRW

Then, open your browser to $URL/api/heroes.
You should see lots of heroes...

## CRUD Tests in HeroResourceTest

To test the `HeroResource` endpoint, we will be using a `QuarkusTestResource` that will fire a Postgres database and then test CRUD operations.
The `QuarkusTestResource` is a test extension that can configure the environment before running the application but in our context, because of CRW, we will be using the database configured and running on OpenShift.

We need to install in our `pom.xml` an extra dependency for data-binding functionality:



Then, in `io.quarkus.workshop.superheroes.hero.HeroResourceTest`, you will add the following test methods to the `HeroResourceTest` class:

* `shouldNotGetUnknownHero`: giving a random Hero identifier, the `HeroResource` endpoint should return a 204 (No content)
* `shouldGetRandomHero`: checks that the `HeroResource` endpoint returns a random hero
* `shouldNotAddInvalidItem`: passing an invalid `Hero` should fail when creating it (thanks to the `@Valid` annotation)
* `shouldGetInitialItems`: checks that the `HeroResource` endpoint returns the list of heroes
* `shouldAddAnItem`: checks that the `HeroResource` endpoint creates a valid `Hero`
* `shouldUpdateAnItem`: checks that the `HeroResource` endpoint updates a newly created `Hero`
* `shouldRemoveAnItem`: checks that the `HeroResource` endpoint deletes a hero from the database

The code is as follow:

Let's have a look to the 2 annotations used on the `HeroResourceTest` class.
`@QuarkusTest` indicates that this test class is checking the behavior of a Quarkus application.
The test framework starts the application before the test class and stops it once all the tests have been executed.
The tests and the application runs in the same JVM, meaning that the test can be injected with application _beans_.
This feature is very useful to test specific parts of the application.
However in our case, we just execute HTTP requests to check the result.

With this code written, execute the test using `./mvnw test`.
The test should pass.

