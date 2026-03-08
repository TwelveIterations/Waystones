package net.blay09.mods.waystones.config.rules;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.minecraft.resources.Identifier;

import static net.blay09.mods.waystones.Waystones.id;

public record NameEquals(String name) implements ShogiEffect<Boolean> {
    public static final Identifier IDENTIFIER = id("name_equals");
    public static final MapCodec<NameEquals> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(NameEquals::name)
    ).apply(instance, NameEquals::new));

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    @Override
    public Either<? extends Boolean, ?> apply(ShogiContext context) {
        return Either.left(WaystoneRuleContext.getEffectiveWaystone(context)
                .map(waystone -> name.equals(waystone.getName().getString()))
                .orElse(false));
    }
}
