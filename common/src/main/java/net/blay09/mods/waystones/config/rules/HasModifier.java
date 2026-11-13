package net.blay09.mods.waystones.config.rules;

import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.shogi.effect.ConstantEffect;
import net.blay09.mods.shogi.effect.ShogiEffect;
import net.blay09.mods.shogi.effect.EffectArgumentCodecs;
import net.blay09.mods.shogi.context.ShogiContext;
import net.blay09.mods.shogi.scope.ShogiScope;
import net.blay09.mods.shogi.coercion.Coercion;
import net.blay09.mods.shogi.common.util.InventoryLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.HolderSetCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record HasModifier(HolderSet<Item> item, ShogiEffect<?> count) implements ShogiEffect<Boolean> {

    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath("waystones", "has_modifier");

    public static MapCodec<HasModifier> mapCodec(ShogiScope scope) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(), false).fieldOf("item").forGetter(HasModifier::item),
                EffectArgumentCodecs.effectOrConstant(scope).fieldOf("count").orElse(new ConstantEffect(new JsonPrimitive(1))).forGetter(HasModifier::count)
        ).apply(instance, HasModifier::new));
    }

    @Override
    public Either<Boolean, ?> apply(ShogiContext context) {
        final int requestedCount = count.apply(context).mapLeft(Coercion.NON_NEGATIVE_INT).orThrow();
        return Either.left(WaystoneRuleContext.getSourceModifierContainer(context)
                .map(container -> InventoryLookup.countMatchingInContainer(container, stack -> stack.is(item)) >= requestedCount)
                .orElse(false));
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

}
