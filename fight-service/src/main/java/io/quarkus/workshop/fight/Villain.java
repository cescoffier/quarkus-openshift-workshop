package io.quarkus.workshop.fight;

import java.util.List;

public record Villain(String name, int level, List<String> powers) {
}
