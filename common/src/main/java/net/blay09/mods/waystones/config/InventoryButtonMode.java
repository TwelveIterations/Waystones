package net.blay09.mods.waystones.config;

public class InventoryButtonMode {

    private final String value;

    public InventoryButtonMode(String value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return !value.isEmpty() && !"none".equalsIgnoreCase(value);
    }

    public boolean isReturnToNearest() {
        return "nearest".equalsIgnoreCase(value);
    }

    public boolean isReturnToAny() {
        return "any".equalsIgnoreCase(value);
    }

    public boolean hasNamedTarget() {
        return isEnabled() && !isReturnToNearest() && !isReturnToAny();
    }

    public String getNamedTarget() {
        return value;
    }
}
