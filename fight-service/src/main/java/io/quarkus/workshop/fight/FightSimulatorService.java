package io.quarkus.workshop.fight;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.time.temporal.ChronoUnit;

@RegisterAiService
public interface FightSimulatorService {


    @UserMessage("""
            <|system|>
            You are a simulation service that can simulate fights between heroes and villains.
            Heroes and villains are represented with their name, level and superpowers.
            Your goal is to simulate a fight and provide the winner and a short narration of the fight.
            Include a part of randomness in the simulation to make it more interesting.
            You must only use the information provided in the hero and villain objects to simulate the fight.
            {response_schema}
            
            <|user|>
            Input: Hero[name=julien, level=2, powers=[python, javascript]]
            Villain[name=clement, level=3, powers=[java, javascript]]
            
            <|assistant|>
            Output: {"winner": "Clement","narration": "Clement's intimate knowledge of Java and JavaScript allowed him to quickly adapt to the battle and use his powers to counter Julien's attempts to exploit his Python skills."}
            
            <|user|>
            Input: Hero[name=luke skywalker, level=100, powers=[the force, lightsaber]]
            Villain[name=darth vader, level=90, powers=[the dark force, lightsaber]]
            
            <|assistant|>
            Output: {"winner": "luke skywalker","narration": "Luke Skywalker's mastery of the force and his lightsaber skills allowed him to defeat Darth Vader in a fierce battle. Despite Darth Vader's experience and power, Luke's determination and skill proved to be too much for him."}

            <|user|>
            Input: Hero[name=jar jar been, level=10, powers=[agility]]
            Villain[name=super frog, level=20, powers=[super leap, speed]]
            
            <|assistant|>
            Output: {"winner": "super frog","narration": "Super frog super leaping ability allowed him to defeat jar jar been in a fierce battle. Despite jar jar been's experience and agility, super frog's skills proved to be too much for him."}
            
            <|user|>
            {hero}
            {villain}
            
            <|assistant|>
            Output:
            """)
    @Timeout(value = 1, unit = ChronoUnit.MINUTES)
    FightResult fight(Hero hero, Villain villain);

}
