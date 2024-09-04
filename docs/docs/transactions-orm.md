# Transactions and ORM

The Hero API's role is to allow CRUD operations on Super Heroes.
In this module we will create a Hero entity and persist/update/delete/retrieve it from a Postgres database in a transactional way.

## Directory Structure

In this module we will add extra classes to the Hero API project.
You will end-up with the following directory structure:

![hero-transaction-orm-directory-structure](target/hero-transaction-orm-directory-structure.svg)

## Database dependencies

This microservice:

* interacts with a PostGreSQL database - so it needs a driver
* uses Hibernate with Panache - so need the dependency on it
* validates payloads and entities - so need a validator
* consumes and produces JSON - so we need a mapper

Hibernate ORM is the de-facto JPA implementation and offers you the full breadth of an Object Relational Mapper.
It makes complex mappings possible, but it does not make simple and common mappings trivial.
[Hibernate ORM with Panache](https://github.com/quarkusio/quarkus/tree/master/extensions/panache) focuses on making your entities trivial and fun to write in Quarkus.

Because JPA and Bean Validation work well together, we will use Bean Validation to constrain our business model.

All the needed dependencies to access the database are already in the pom.xml file. Check that you have the following:

```java linenums="1"
{{ insert('hero-service/pom.xml', 'docDbDependency') }}
```

If you need to add them, just run the following command:

```shell

$ ./mvnw quarkus:add-extension -Dextensions="jdbc-postgresql,hibernate-orm-panache,hibernate-validator,rest-jackson"
```

From now on, you can choose to either edit your pom directly or use the `quarkus:add-extension` command.

## Hero Entity

To define a Panache entity, simply extend `PanacheEntity`, annotate it with `@Entity` and add your columns as public fields (no need to have getters and setters).
Create a new java class under `src/main/java/io/quarkus/workshop/hero` and copy the following content:

```java linenums="1"
{{ insert('hero-service/src/main/java/io/quarkus/workshop/hero/Hero.java', 'docEntityHero', ['docFindRandomHero']) }}
```

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

```java linenums="1"
{{ insert('hero-service/src/main/java/io/quarkus/workshop/hero/Hero.java', 'docFindRandomHero') }}
```


**NOTE**

You would need to add the following import statement if not done automatically by your IDE `import java.util.Random;`

## Configuring Hibernate

As Quarkus supports the automatic provisioning of unconfigured services in development and test mode, we don't need at the moment to configure anything regarding the database access. 
Quarkus will automatically start a Postgresql service and wire up your application to use this service. 

Quarkus development mode is really useful for applications that mix front end or services and database access.
We use `quarkus.hibernate-orm.database.generation=drop-and-create` in conjunction with `import.sql` so every change to your app and in particular to your entities, the database schema will be properly recreated and your data (stored in `import.sql`) will be used to repopulate it from scratch.
This is best to perfectly control your environment and works magic with Quarkus live reload mode:
your entity changes or any change to your `import.sql` is immediately picked up and the schema updated without restarting the application!

For that, make sure to have the following configuration in your `application.properties` (located in `src/main/resources`):

```properties linenums="1" 
{{ insert('hero-service/src/main/resources/application.properties', 'dropAndCreateProp') }}
```

### Adding Data

To load some data when Hibernate ORM starts, add the following SQL statements in the `import.sql` in the root of the `resources` directory.
This is useful to have a data set ready for the tests or demos.

```sql linenums="1" 
{{ insert('hero-service/src/main/resources/import.sql', 'docDataSql') }}
```

Ok, but that's just a few entries.
Download the SQL file [import.sql](https://raw.githubusercontent.com/cescoffier/quarkus-openshift-workshop/03d5a943c0948bc53c598b6ee78a71e50ef77cee/hero-service/src/main/resources/import.sql) and copy it under `src/main/resources`.
Now, you have around 500 heroes that will be loaded in the database.


## HeroResource Endpoint

The `HeroResource` Endpoint was bootstrapped with only one method `hello()`.
We need to add extra methods that will allow CRUD operations on heroes.

### Making HeroResource Transactional

To manipulate the `Hero` entity we need make `HeroResource` transactional.
The idea is to wrap methods modifying the database (e.g. `entity.persist()`) within a transaction.
Marking a CDI bean method `@Transactional` will do that for you and make that method a transaction boundary.

`@Transactional` can be used to control transaction boundaries on any CDI bean at the method level or at the class level to ensure every method is transactional.

Here are the new methods to add to the `HeroResource` class:

```java linenums="1"
{{ insert('hero-service/src/main/java/io/quarkus/workshop/hero/HeroResource.java', 'docHeroResource', [], ['docHeroCrudContent']) }}
```

Notice that both methods that persist and update a hero, pass a `Hero` object as a parameter.
Thanks to the Bean Validation's `@Valid` annotation, the `Hero` object will be checked to see if it's valid or not.
It it's not, the transaction will be rollback-ed.

If you didn't yet, start the application in dev mode:

```shell
$./mvnw quarkus:dev

```
or

```shell
$ quarkus dev

```

Then, open your browser to $URL/api/heroes.
You should see lots of heroes...

## Configuring the Datasource for Production

Production databases need to be configured as normal. 
So if you want to include a production database config in your `application.properties` and continue to use Dev Services,
we recommend that you use the `%prod` profile to define your database settings.

Just add the following datasource configuration in the `src/main/resources/application.properties` file:

```properties linenums="1" 
{{ insert('hero-service/src/main/resources/application.properties', 'docDataSourceConfig') }}
```

## CRUD Tests in HeroResourceTest

To test the `HeroResource` endpoint, we will be using a `@QuarkusTest` that will fire a Postgres database and then test CRUD operations.

In the generated build file, you can see 2 test dependencies:

```xml linenums="1"
{{ insert('hero-service/pom.xml', 'docTestingDeps') }}
```

Also, the generated project contains a simple test. Edit the HeroResourceTest.java to match the following content:

```java linenums="1"
{{ insert('hero-service/src/test/java/io/quarkus/workshop/hero/HeroResourceTest.java') }}
```

The following test methods have been added to the `HeroResourceTest` class:

* `shouldNotGetUnknownHero`: giving a random Hero identifier, the `HeroResource` endpoint should return a 204 (No content)
* `shouldGetRandomHero`: checks that the `HeroResource` endpoint returns a random hero
* `shouldNotAddInvalidItem`: passing an invalid `Hero` should fail when creating it (thanks to the `@Valid` annotation)
* `shouldGetInitialItems`: checks that the `HeroResource` endpoint returns the list of heroes
* `shouldAddAnItem`: checks that the `HeroResource` endpoint creates a valid `Hero`
* `shouldUpdateAnItem`: checks that the `HeroResource` endpoint updates a newly created `Hero`
* `shouldRemoveAnItem`: checks that the `HeroResource` endpoint deletes a hero from the database

Let’s have a look to the 2 annotations used on the HeroResourceTest class. 
`@QuarkusTest` indicates that this test class is checking the behavior of a Quarkus application. 
The test framework starts the application before the test class and stops it once all the tests have been executed. 
In our case, we just execute HTTP requests to check the result.

With this code written, execute the test using `./mvnw test`.
The test should pass.


