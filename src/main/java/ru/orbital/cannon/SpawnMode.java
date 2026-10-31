package ru.orbital.cannon;

import java.util.Locale;

public enum SpawnMode {

    NORMAL,
    ORBITAL,
    MEGAORBITAL,
    STAB,
    WITHER;

    public static SpawnMode parse(String raw) {
        if (raw == null) {
            return NORMAL;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "orbital", "orbit", "orb", "орбитал", "орбиталка", "орбит" -> ORBITAL;
            case "megaorbital", "mega", "megaorb", "мега", "мегаорбитал", "мегаорбиталка" -> MEGAORBITAL;
            case "stab", "стаб", "стилет", "удар" -> STAB;
            case "wither", "withercannon", "witherc", "визер", "визерканон", "визер-канон" -> WITHER;
            default -> NORMAL;
        };
    }
}
