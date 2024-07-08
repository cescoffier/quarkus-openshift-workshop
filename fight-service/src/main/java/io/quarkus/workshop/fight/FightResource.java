package io.quarkus.workshop.fight;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/fights")
public class FightResource {

    private final FightSimulatorService simulator;
    private final FightMetricPublisher publisher;

    FightResource(FightSimulatorService simulator, FightMetricPublisher publisher) {
        this.simulator = simulator;
        this.publisher = publisher;
    }


    @POST
    public FightResult fight(Fight fight) {
        var result = simulator.fight(fight.hero(), fight.villain());
        publisher.publishFight(fight, result);
        return result;
    }

}
