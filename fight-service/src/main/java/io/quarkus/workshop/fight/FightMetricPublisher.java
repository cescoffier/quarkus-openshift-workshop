package io.quarkus.workshop.fight;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FightMetricPublisher {

    private final Counter heroVictories;
    private final Counter villainVictories;

    FightMetricPublisher(MeterRegistry registry) {
        heroVictories = registry.counter("fights", "won-by", "hero");
        villainVictories = registry.counter("fights", "won-by", "villain");
    }

    public void publishFight(Fight fight, FightResult result) {
        if (result.winner().equalsIgnoreCase(fight.hero().name())) {
            heroVictories.increment();
        } else {
            villainVictories.increment();
        }
    }
}
