package net.blay09.mods.waystones.config.rules;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.shogi.scope.ShogiScope;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public record Target<T>(ShogiEffect<T> effect) implements ShogiEffect<T> {
    public static final Identifier IDENTIFIER = id("target");

    public static MapCodec<Target<?>> mapCodec(ShogiScope scope) {
        return RecordCodecBuilder.mapCodec(builder -> builder.group(
                scope.getEffectCodec().fieldOf("effect").forGetter(Target::effect)
        ).apply(builder, Target::new));
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    @Override
    public Either<? extends T, ?> apply(ShogiContext context) {
        final var nestedContext = context.fork();
        if (context instanceof WaystoneTeleportContext waystoneTeleportContext) {
            WaystoneRuleContext.setEffectiveWaystone(nestedContext, waystoneTeleportContext.getTargetWaystone());
        }
        return effect.apply(nestedContext);
    }
}
