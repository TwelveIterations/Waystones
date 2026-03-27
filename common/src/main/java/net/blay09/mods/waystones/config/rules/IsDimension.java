package net.blay09.mods.waystones.config.rules;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public record IsDimension(Identifier dimension) implements ShogiEffect<Boolean> {
    public static final Identifier IDENTIFIER = id("is_dimension");
    public static final MapCodec<IsDimension> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("dimension").forGetter(IsDimension::dimension)
    ).apply(instance, IsDimension::new));

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    @Override
    public Either<? extends Boolean, ?> apply(ShogiContext context) {
        final var currentDimension = WaystoneRuleContext.getEffectiveWaystone(context)
                .map(waystone -> waystone.getDimension().identifier())
                .orElseGet(() -> context.requireLevel().dimension().identifier());
        return Either.left(currentDimension.equals(dimension));
    }
}
