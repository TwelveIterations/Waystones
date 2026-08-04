package net.blay09.mods.waystones.worldgen.namegen;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.blay09.mods.waystones.Waystones.id;

public class NameGeneratorManager extends SavedData {

    private static final String DATA_NAME = "name_generator";
    private static final Codec<NameGeneratorManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("UsedNames").forGetter(NameGeneratorManager::getUsedNames)
    ).apply(instance, NameGeneratorManager::new));

    @SuppressWarnings("DataFlowIssue")
    public static final SavedDataType<NameGeneratorManager> TYPE = new SavedDataType<>(
            id(DATA_NAME),
            () -> new NameGeneratorManager(List.of()),
            CODEC,
            null
    );

    private final Set<String> usedNames = Sets.newConcurrentHashSet();

    public NameGeneratorManager(List<String> usedNames) {
        this.usedNames.addAll(usedNames);
    }

    public static NameGeneratorManager get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public List<String> getUsedNames() {
        return new ArrayList<>(usedNames);
    }

    private NameGenerator getNameGenerator(NameGenerationMode nameGenerationMode) {
        final var randomGenerator = new TemplateNameGenerator(WaystonesConfig.getActive().worldGen.nameGenerationTemplate)
                .with("MrPork", new MrPorkNameGenerator())
                .with("Biome", new BiomeNameGenerator());
        return switch (nameGenerationMode) {
            case MIXED -> new MixedNameGenerator(randomGenerator, new CustomNameGenerator(false, usedNames));
            case RANDOM_ONLY -> randomGenerator;
            case PRESET_ONLY -> new CustomNameGenerator(true, usedNames);
            default -> new SequencedNameGenerator(new CustomNameGenerator(false, usedNames), randomGenerator);
        };
    }

    public synchronized Component getName(LevelAccessor level, Waystone waystone, RandomSource rand, NameGenerationMode nameGenerationMode) {
        final var nameGenerator = getNameGenerator(nameGenerationMode);
        final var originalName = nameGenerator.generateName(level, waystone, rand).orElse(Component.empty());
        var name = resolveDuplicate(originalName);

        final var event = new GenerateWaystoneNameEvent(waystone, name);
        GenerateWaystoneNameEvent.EVENT.invoker().accept(event);
        name = event.getName();

        usedNames.add(name.getString());
        setDirty();
        return name;
    }

    private Component resolveDuplicate(Component name) {
        var tryName = name;
        int i = 1;
        while (usedNames.contains(tryName.getString())) {
            tryName = name.copy().append(" " + RomanNumber.toRoman(i));
            i++;
        }
        return tryName;
    }

}
