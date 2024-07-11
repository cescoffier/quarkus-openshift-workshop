package io.quarkus.workshop.fightui;

import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/config/endpoints")
public class ConfigResource {

    @ConfigProperty(name = "endpoints.heroes", defaultValue = "http://localhost:8080/api/heroes/random")
    String heroesEndpoint;

    @ConfigProperty(name = "endpoints.villains", defaultValue = "http://localhost:8081/api/villains/random")
    String villainsEndpoint;

    @ConfigProperty(name = "endpoints.fight", defaultValue = "http://localhost:8082/api/fights")
    String fightEndpoint;

    @GET
    public JsonObject configuration() {
        return new JsonObject()
                .put("heroes", heroesEndpoint)
                .put("villains", villainsEndpoint)
                .put("fight", fightEndpoint);
    }
}

