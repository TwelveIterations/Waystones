package net.blay09.mods.waystones.api;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MutablePersonalizedWaystoneDelegate extends PersonalizedWaystoneDelegate implements MutablePersonalizedWaystone {

    protected final MutablePersonalizedWaystone delegate;

    public MutablePersonalizedWaystoneDelegate(MutablePersonalizedWaystone delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public MutablePersonalizedWaystone getDelegate() {
        return delegate;
    }

    @Override
    public void setAlias(@Nullable Component alias) {
        delegate.setAlias(alias);
    }

    @Override
    public void setConfiguredGroups(Collection<ResourceLocation> configuredGroups) {
        delegate.setConfiguredGroups(configuredGroups);
    }

    @Override
    public void setHidden(boolean hidden) {
        delegate.setHidden(hidden);
    }
}
