## Packaging and Running the Application

==If you don't stopped the hero-service dev mode, please stop it.==

The application is packaged running `./mvnw package` or `quarkus build` in a Terminal.
It produces 2 jar files in `/target`:

* `rest-hero-1.0-SNAPSHOT.jar`: containing just the classes and resources of the projects, it's the regular artifact produced by the Maven build;
* `quarkus-app/quarkus-run.jar`: being an executable jar.
  Be aware that it's not an über-jar as the dependencies are copied into the `target/quarkus-app/lib` directory.

The application is now runnable from a terminal by running the following command:

```shell 
java -jar target/quarkus-app/quarkus-run.jar
```

Now: 

<div class="grid cards" markdown>
-   :warning:{ .lg .middle }:warning:{ .lg .middle } __Stop the hero-service__ :warning:{ .lg .middle }:warning:{ .lg .middle }

    ---

    Please stop the hero-service.
</div>
