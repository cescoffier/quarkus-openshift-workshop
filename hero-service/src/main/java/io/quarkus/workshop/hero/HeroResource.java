//<docHeroResource>
package io.quarkus.workshop.hero;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;

import org.eclipse.microprofile.openapi.annotations.Operation;


import java.net.URI;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/api/heroes")
@RunOnVirtualThread
public class HeroResource {

    @GET
    @Path("/hello")
    public String hello() {
        return "Hello from Quarkus REST";
    }

    //<docHeroCrudContent>
    @Operation(summary = "Returns a random hero")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Hero.class, required = true)))
    @GET
    @Path("/random")
    public RestResponse<Hero> getRandomHero() {
        var hero = Hero.findRandom();
        if (hero != null) {
            Log.debugf("Found random hero: %s", hero);
            return RestResponse.ok(hero);
        } else {
            Log.debug("No random hero found");
            return RestResponse.notFound();
        }
    }

    @GET
    public List<Hero> getAllHeroes() {
        return Hero.listAll();
    }

    @GET
    @Path("/{id}")
    public RestResponse<Hero> getHero(Long id) {
        var hero = Hero.<Hero>findById(id);
        if (hero != null) {
            return RestResponse.ok(hero);
        } else {
            Log.debugf("No Hero found with id %d", id);
            return RestResponse.notFound();
        }
    }

    @POST
    @Transactional
    public RestResponse<URI> createHero(@Valid Hero hero, @Context UriInfo uriInfo) {
        hero.persist();
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(hero.id));
        Log.debugf("New Hero created with URI %s", builder.build().toString());
        return RestResponse.created(builder.build());
    }

    @PUT
    @Transactional
    public Hero updateHero(@Valid Hero hero) {
        Hero retrieved = Hero.findById(hero.id);
        retrieved.name = hero.name;
        retrieved.otherName = hero.otherName;
        retrieved.level = hero.level;
        retrieved.picture = hero.picture;
        retrieved.powers = hero.powers;
        Log.debugf("Hero updated with new valued %s", retrieved);
        return retrieved;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public RestResponse<Void> deleteHero(Long id) {
        if (Hero.deleteById(id)) {
            Log.debugf("Hero deleted with %d", id);
        }
        return RestResponse.noContent();
    }
    //</docHeroCrudContent>
}
//</docHeroResource>

