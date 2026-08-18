package net.blay09.mods.waystones.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.Set;

public class PersonalizedWaystoneDelegate extends WaystoneDelegate implements PersonalizedWaystone {

    protected final PersonalizedWaystone delegate;

    public PersonalizedWaystoneDelegate(PersonalizedWaystone delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public PersonalizedWaystone getDelegate() {
        return delegate;
    }

    @Override
    public Waystone getBackingWaystone() {
        return delegate.getBackingWaystone();
    }

    @Override
    public Optional<Component> getAlias() {
        return delegate.getAlias();
    }

    @Override
    public Set<ResourceLocation> getConfiguredGroups() {
        return delegate.getConfiguredGroups();
    }

    @Override
    public boolean isHidden() {
        return delegate.isHidden();
    }
}
