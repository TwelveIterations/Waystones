package net.blay09.mods.waystones.worldgen.namegen;

import com.google.common.collect.Sets;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.event.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class NameGeneratorManager extends SavedData {

    private static final String DATA_NAME = Waystones.MOD_ID + "_NameGenerator";
    private static final String USED_NAMES = "UsedNames";
    private static final NameGeneratorManager clientStorageCopy = new NameGeneratorManager();

    private final Set<String> usedNames = Sets.newConcurrentHashSet();

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

    public static NameGeneratorManager load(CompoundTag compound, HolderLookup.Provider provider) {
        NameGeneratorManager nameGenerator = new NameGeneratorManager();
        ListTag tagList = compound.getList(USED_NAMES, Tag.TAG_STRING);
        for (Tag tag : tagList) {
            nameGenerator.usedNames.add(tag.getAsString());
        }
        return nameGenerator;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        ListTag tagList = new ListTag();
        for (String entry : usedNames) {
            tagList.add(StringTag.valueOf(entry));
        }
        compound.put(USED_NAMES, tagList);
        return compound;
    }

    public static NameGeneratorManager get(@Nullable MinecraftServer server) {
        if (server != null) {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            return Objects.requireNonNull(overworld).getDataStorage().computeIfAbsent(new Factory<>(NameGeneratorManager::new, NameGeneratorManager::load, null), DATA_NAME);
        }

        return clientStorageCopy;
    }

}
