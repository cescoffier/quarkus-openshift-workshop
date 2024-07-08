package io.quarkus.workshop.fight;

import java.util.List;

public record Hero(String name, int level, List<String> powers) {
}
