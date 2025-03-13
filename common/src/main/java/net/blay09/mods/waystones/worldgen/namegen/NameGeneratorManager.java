package net.blay09.mods.waystones.worldgen.namegen;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.event.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NameGeneratorManager extends SavedData {

    private static final String DATA_NAME = Waystones.MOD_ID + "_name_generator";
    private static final Codec<NameGeneratorManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("usedNames").forGetter(NameGeneratorManager::getUsedNames)
    ).apply(instance, NameGeneratorManager::new));

    public static final SavedDataType<NameGeneratorManager> TYPE = new SavedDataType<>(
            DATA_NAME,
            (context) -> new NameGeneratorManager(List.of()),
            ctx -> CODEC,
            null // TODO this can't be null but mod loaders will save us soon I'm sure
    );

    private final Set<String> usedNames = Sets.newHashSet();

    public NameGeneratorManager(List<String> usedNames) {
        this.usedNames.addAll(usedNames);
    }

    public List<String> getUsedNames() {
        return new ArrayList<>(usedNames);
    }

    private NameGenerator getNameGenerator(NameGenerationMode nameGenerationMode) {
        final var randomGenerator = new TemplateNameGenerator(WaystonesConfig.getActive().worldGen.nameGenerationTemplate)
                .with("MrPork", new MrPorkNameGenerator())
                .with("Biome", new BiomeNameGenerator());
        switch (nameGenerationMode) {
            case MIXED:
                return new MixedNameGenerator(randomGenerator, new CustomNameGenerator(false, usedNames));
            case RANDOM_ONLY:
                return randomGenerator;
            case PRESET_ONLY:
                return new CustomNameGenerator(true, usedNames);
            case PRESET_FIRST:
            default:
                return new SequencedNameGenerator(new CustomNameGenerator(false, usedNames), randomGenerator);
        }
    }

    public synchronized Component getName(LevelAccessor level, Waystone waystone, RandomSource rand, NameGenerationMode nameGenerationMode) {
        final var nameGenerator = getNameGenerator(nameGenerationMode);
        final var originalName = nameGenerator.generateName(level, waystone, rand).orElse(Component.empty());
        var name = resolveDuplicate(originalName);

        final var event = new GenerateWaystoneNameEvent(waystone, name);
        Balm.getEvents().fireEvent(event);
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

    public static NameGeneratorManager get(MinecraftServer server) {
        final var overworld = server.getLevel(Level.OVERWORLD);
        return Objects.requireNonNull(overworld).getDataStorage().computeIfAbsent(TYPE);
    }

}
