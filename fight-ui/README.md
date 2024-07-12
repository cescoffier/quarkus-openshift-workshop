# Fight UI

## Supported configuration

| Key                  | Purpose               |
|----------------------|-----------------------|
| `endpoints.heroes`   | Heroes API endpoint   |
| `endpoints.villains` | Villains API endpoint |
| `endpoints.fight`    | Fights API endpoint   |

You can override these as explained in <https://quarkus.io/guides/config-reference>:

- in `application.properties`, or
- in JVM system properties: `-Dendpoints.heroes=http://192.168.100.10:6666/api/heroes/random`, or
- in environment variables: `ENDPOINTS_HEROES=http://192.168.100.10:6666/api/heroes/random`, etc.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/hero-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.
