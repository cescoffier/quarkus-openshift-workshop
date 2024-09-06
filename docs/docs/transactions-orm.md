# Transactions and ORM

The Hero API's role is to allow CRUD operations on Super Heroes.
In this module we will create a Hero entity and persist/update/delete/retrieve it from a Postgres database in a transactional way.

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

All the needed dependencies to access the database are already in the `pom.xml` file. 

==Check that you have the following:==

```java linenums="1"
{{ insert('hero-service/pom.xml', 'docDbDependency') }}
```

## Hero Entity

At this point we need an Entity class.
There is already a `Hero.java` file under `src/main/java/io/quarkus/workshop/hero` so you don't need to create it.
However this file is empty.
To define a Panache entity, simply extend `PanacheEntity`, annotate it with `@Entity` and add your columns as public fields (no need to have getters and setters).

==Edit the `Hero.java` file under `src/main/java/io/quarkus/workshop/hero` and copy the following content:==

```java linenums="1"
{{ insert('hero-service/src/main/java/io/quarkus/workshop/hero/Hero.java', 'docEntityHero', ['docFindRandomHero']) }}
```

Notice that you can put all your JPA column annotations and Bean Validation constraint annotations on the public fields.

### Adding Operations

For our workshop we need returning a random hero.
==For that it's just a matter to add the following method to our `Hero.java` entity:==

```java linenums="1"
{{ insert('hero-service/src/main/java/io/quarkus/workshop/hero/Hero.java', 'docFindRandomHero') }}
```

<div class="grid cards" markdown>
-   :information:{ .lg .middle } __Import__ 

    ---

    ==You would need to add the following import statement if not done automatically by your IDE `import java.util.Random;`==
</div>


## Configuring Hibernate

As Quarkus supports the automatic provisioning of unconfigured services in development and test mode, we don't need at the moment to configure anything regarding the database access. 
Quarkus will automatically start a Postgresql service and wire up your application to use this service. 

Quarkus development mode is great for apps that combine front-end, services, and database access. By using quarkus.hibernate-orm.database.generation=drop-and-create with import.sql, any changes to your entities automatically recreate the database schema and repopulate data. This setup works perfectly with Quarkus live reload, instantly applying changes without restarting the app.

==For that, make sure to have the following configuration in your `application.properties` (located in `src/main/resources`):==

```properties linenums="1" 
{{ insert('hero-service/src/main/resources/application.properties', 'dropAndCreateProp') }}
```

### Adding Data

==To load some data when Hibernate ORM starts, run the following command on a Terminal:==

```shell
curl https://raw.githubusercontent.com/cescoffier/quarkus-openshift-workshop/03d5a943c0948bc53c598b6ee78a71e50ef77cee/hero-service/src/main/resources/import.sql -fL -o src/main/resources/import.sql
```

It will download the specified file and copy the content in your `/src/resources/import.sql` file.
Now, you have around 500 heroes that will be loaded in the database.


## HeroResource Endpoint

The `HeroResource` Endpoint was bootstrapped with only one method `hello()`.
We need to add extra methods that will allow CRUD operations on heroes.

### Making HeroResource Transactional

To manipulate the `Hero` entity we need make `HeroResource` transactional.
The idea is to wrap methods modifying the database (e.g. `entity.persist()`) within a transaction.
Marking a CDI bean method `@Transactional` will do it and make that method a transaction boundary.

==Replace the content of the `HeroResource.java` by the following one. It contains the new methods for accessing data:==

```java linenums="1"
{{ insert('hero-service/src/main/java/io/quarkus/workshop/hero/HeroResource.java', 'docHeroResource', [], ['docHeroCrudContent']) }}
```

Notice that both methods that persist and update a hero, pass a `Hero` object as a parameter.
Thanks to the Bean Validation's `@Valid` annotation, the `Hero` object will be checked to see if it's valid or not.
It it's not, the transaction will be rollback-ed.

If you didn't yet, open a Terminal and start the application in dev mode:

```shell
./mvnw quarkus:dev

```
or

```shell
$ quarkus dev

```

==Then, open your browser to $HERO_URL/api/heroes.==
You should see lots of heroes...

## Configuring the Datasource for Production

Production databases need to be configured as normal. 
So if you want to include a production database config in your `application.properties` and continue to use Dev Services,
we recommend that you use the `%prod` profile to define your database settings.

==Just add the following datasource configuration in the `src/main/resources/application.properties` file:==

```properties linenums="1" 
{{ insert('hero-service/src/main/resources/application.properties', 'docDataSourceConfig') }}
```

## CRUD Tests in HeroResourceTest

We added a few methods to the HeroResource, we should test them!

==For testing the new methods added to the HeroResource, replace the content of the `HeroResourceTest.java` by the following:==

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

==Press `r` in the terminal you have Quarkus dev running. Tests will start running and they should pass.==


