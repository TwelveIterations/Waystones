package net.blay09.mods.waystones.config.rules;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public record IsWithinDistance(float distance) implements ShogiEffect<Boolean> {
    public static final Identifier IDENTIFIER = id("is_within_distance");
    public static final MapCodec<IsWithinDistance> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("distance").forGetter(IsWithinDistance::distance)
    ).apply(instance, IsWithinDistance::new));

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    @Override
    public Either<? extends Boolean, ?> apply(ShogiContext context) {
        final var actualDistance = WaystoneRuleContext.getTargetWaystone(context)
                .map(waystone -> (float) Math.sqrt(context.entity().distanceToSqr(waystone.getPos().getCenter())));
        return Either.left(actualDistance.map(it -> it <= distance).orElse(false));
    }
}
