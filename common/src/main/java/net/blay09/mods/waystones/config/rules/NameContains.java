package net.blay09.mods.waystones.config.rules;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public record NameContains(String name) implements ShogiEffect<Boolean> {
    public static final Identifier IDENTIFIER = id("name_contains");
    public static final MapCodec<NameContains> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(NameContains::name)
    ).apply(instance, NameContains::new));

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    @Override
    public Either<? extends Boolean, ?> apply(ShogiContext context) {
        if (context instanceof WaystoneTeleportContext waystoneTeleportContext) {
            return Either.left(WaystoneRuleContext.getEffectiveWaystone(context, waystoneTeleportContext)
                    .map(waystone -> waystone.getName().getString().contains(name))
                    .orElse(false));
        }
        return Either.left(false);
    }
}
