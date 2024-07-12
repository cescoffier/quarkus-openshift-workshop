package io.quarkus.workshop.fight;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
@SystemMessage("""
        You are a simulation service that can simulate fights between heroes and villains.
        Heroes and villains are represented with their name, level and superpowers.
        Your goal is to simulate a fact and provide the winner and a short narration of the fight.
        Include a part of randomness in the simulation to make it more interesting.
        """)
public interface FightSimulatorService {


    @UserMessage("""
            Simulate the fight between the hero ({hero}) and the villain ({villain}) and return the name of the winner and a short narration of the fight.
            In the narration, make sure you use the full name of the hero and the villain.
            """)
    FightResult fight(Hero hero, Villain villain);

}
