By default, a Quarkus application exposes its API description through an OpenAPI specification.
Quarkus also lets you test it via a user-friendly UI named Swagger UI.

### Installing the Open API Dependency

Quarkus proposes a smallrye-openapi extension compliant with the Eclipse MicroProfile OpenAPI specification in order to generate your API OpenAPI specification.

To install the OpenAPI dependency, add the following to the `pom.xml` of the hero-service

```java linenums="1"
{{ insert('hero-service/pom.xml', 'docOpenApiDependency') }}
```
Now, you can open a browser and navigate to the hero endpoint to the default $URL/q/openapi endpoint:

```shell
---
openapi: 3.0.3
info:
  title: hero-service API
  version: 1.0.0-SNAPSHOT
paths:
  /api/heroes:
    get:
      tags:
      - Hero Resource
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: "#/components/schemas/Hero"
    put:
      tags:
      - Hero Resource
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/Hero"
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Hero"
    post:
      tags:
      - Hero Resource
      requestBody:
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/Hero"
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                format: uri
                type: string
  /api/heroes/hello:
    get:
      tags:
      - Hero Resource
      responses:
        "200":
          description: OK
          content:
            text/plain:
              schema:
                type: string
  /api/heroes/random:
    get:
      tags:
      - Hero Resource
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Hero"
  /api/heroes/{id}:
    get:
      tags:
      - Hero Resource
      parameters:
      - name: id
        in: path
        required: true
        schema:
          format: int64
          type: integer
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Hero"
    delete:
      tags:
      - Hero Resource
      parameters:
      - name: id
        in: path
        required: true
        schema:
          format: int64
          type: integer
      responses:
        "204":
          description: No Content
components:
  schemas:
    Hero:
      required:
      - name
      - level
      type: object
      properties:
        id:
          format: int64
          type: integer
        name:
          maxLength: 50
          minLength: 3
          type: string
        otherName:
          type: string
        level:
          format: int32
          minimum: 1
          type: integer
        picture:
          type: string
        powers:
          type: string

```

This contract lacks of documentation. The Eclipse MicroProfile OpenAPI allows you to customize the methods of your REST endpoint as well as the application.

### Customizing Methods

The MicroProfile OpenAPI has a set of annotations to customize each REST endpoint method so the OpenAPI contract is richer and clearer for consumers:

* `@Operation`: Describes a single API operation on a path.
* `@APIResponse`: Corresponds to the OpenAPI Response model object which describes a single response from an API Operation
* `@Parameter`: The name of the parameter.
* `@RequestBody`: A brief description of the request body.

Open the `HeroResource.java` and add the following code to the `getRandomHero` method:

```java
@Operation(summary = "Returns a random hero")
@APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, required = true)))
```

If you curl again the /q/openapi endpoint you will see the following contract for getting random hero operation:

```shell
 /api/heroes/random:
    get:
      tags:
      - Hero Resource
      summary: Returns a random hero
      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Hero"
```

## Swagger UI 

When building APIs, developers want to test them quickly. 
Swagger UI is a great tool permitting to visualize and interact with your APIs.
The UI is automatically generated from your OpenAPI specification. 
The Quarkus smallrye-openapi extension comes with a swagger-ui extension embedding a properly configured Swagger UI page. 
By default, Swagger UI is accessible at /q/swagger-ui.
So, once your application is started, you can go to $URL/swagger-ui and play with your API.

![swagger-ui](images/swagger-ui.png)

