package net.blay09.mods.waystones.config.rules;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public record InvolvesDimension(Identifier dimension) implements ShogiEffect<Boolean> {
    public static final Identifier IDENTIFIER = id("involves_dimension");
    public static final MapCodec<InvolvesDimension> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("dimension").forGetter(InvolvesDimension::dimension)
    ).apply(instance, InvolvesDimension::new));

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    @Override
    public Either<? extends Boolean, ?> apply(ShogiContext context) {
        if (context instanceof WaystoneTeleportContext waystoneTeleportContext) {
            final var targetDimension = waystoneTeleportContext.getTargetWaystone().getDimension().identifier();
            final var sourceDimension = waystoneTeleportContext.getFromWaystone()
                    .map(waystone -> waystone.getDimension().identifier())
                    .orElseGet(() -> waystoneTeleportContext.getEntity().level().dimension().identifier());
            return Either.left(targetDimension.equals(dimension) || sourceDimension.equals(dimension));
        }
        return Either.left(false);
    }
}
