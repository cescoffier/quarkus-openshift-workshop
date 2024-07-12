package io.quarkus.workshop.fightui;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
public class Gateway {

    @ConfigProperty(name = "endpoints.heroes")
    String heroesEndpoint;

    @ConfigProperty(name = "endpoints.villains")
    String villainsEndpoint;

    @ConfigProperty(name = "endpoints.fight")
    String fightEndpoint;

    public void init(@Observes Router router, Vertx vertx) {
        WebClient client = WebClient.create(vertx);
        router.get("/gateway/hero").handler(rc ->
                client.getAbs(heroesEndpoint).send()
                        .onSuccess(resp ->
                                rc.response()
                                        .setStatusCode(resp.statusCode())
                                        .putHeader("content-type", "application/json")
                                        .end(resp.bodyAsString())
                        )
                        .onFailure(rc::fail));

        router.get("/gateway/villain").handler(rc ->
                client.getAbs(villainsEndpoint).send()
                        .onSuccess(resp ->
                                rc.response()
                                        .setStatusCode(resp.statusCode())
                                        .putHeader("content-type", "application/json")
                                        .end(resp.bodyAsString())
                        )
                        .onFailure(rc::fail));

        router.post("/gateway/fight")
                .handler(BodyHandler.create())
                .handler(rc ->
                        client.postAbs(fightEndpoint)
                                .putHeader("content-type", "application/json")
                                .sendJson(rc.body().asJsonObject())
                                .onSuccess(resp ->
                                        rc.response()
                                                .setStatusCode(resp.statusCode())
                                                .putHeader("content-type", "application/json")
                                                .end(resp.bodyAsString())
                                )
                                .onFailure(rc::fail)
                );
    }

}
