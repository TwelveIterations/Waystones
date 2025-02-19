package net.blay09.mods.waystones.api;

import java.util.function.Supplier;

public enum EventResult {
    DEFAULT,
    ALLOW,
    DENY;

    public boolean withDefault(Supplier<Boolean> predicate) {
        return switch (this) {
            case DEFAULT -> predicate.get();
            case ALLOW -> true;
            case DENY -> false;
        };
    }
}
