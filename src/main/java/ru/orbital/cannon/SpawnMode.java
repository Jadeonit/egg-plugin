package ru.orbital.cannon;

import java.util.Locale;

public enum SpawnMode {

    NORMAL,
    ORBITAL,
    STAB;

    public static SpawnMode parse(String raw) {
        if (raw == null) {
            return NORMAL;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "orbital", "orbit", "orb", "орбитал", "орбиталка", "орбит" -> ORBITAL;
            case "stab", "стаб", "стилет", "удар" -> STAB;
            default -> NORMAL;
        };
    }
}
