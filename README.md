# quarkus-openshift-workshop
A workshop combining DevSpaces, Quarkus, Backstage, OpenShift, Trusted Application Pipeline, and OpenShift.ai


## Running the application in dev mode (locally)

_Prerequisites:_

* Java 21
* Podman or Docker
* Update the `fight-service/src/main/resources/application.properties` file to point to the Granite LLM service URL. For example:
```properties
quarkus.langchain4j.openai.base-url=https://granite-7b-instruct-llm.apps.ai.sandbox2007.opentlc.com/v1
```

Open 4 terminals and run the following commands:

* Terminal 1
```bash
cd fight-ui
./mvnw quarkus:dev
```

* Terminal 2
```bash
cd fight-service
./mvnw quarkus:dev
```

* Terminal 3
```bash
cd hero-service
./mvnw quarkus:dev
```

* Terminal 4
```bash
cd villain-service
./mvnw quarkus:dev
```

Then, open your browser and navigate to `http://localhost:8080` to see the application running (fight-UI).