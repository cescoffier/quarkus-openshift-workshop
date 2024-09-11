By default, a Quarkus application exposes its API description through an OpenAPI specification.
Quarkus also lets you test it via a user-friendly UI named Swagger UI.

### Installing the Open API Dependency

Quarkus proposes a smallrye-openapi extension compliant with the Eclipse MicroProfile OpenAPI specification in order to generate your API OpenAPI specification.

The OpenAPI dependency is already present in the `pom.xml` of the hero-service

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

## Swagger UI 

When building APIs, developers want to test them quickly. 
Swagger UI is a great tool permitting to visualize and interact with your APIs.
The UI is automatically generated from your OpenAPI specification. 
The Quarkus smallrye-openapi extension comes with a swagger-ui extension embedding a properly configured Swagger UI page. 
By default, Swagger UI is accessible at /q/swagger-ui.
So, once your application is started, you can go to $URL/swagger-ui and play with your API.

![swagger-ui](images/swagger-ui.png)

