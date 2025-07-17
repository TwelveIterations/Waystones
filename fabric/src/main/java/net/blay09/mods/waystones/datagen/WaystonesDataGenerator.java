package net.blay09.mods.waystones.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WaystonesDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(WaystonesBlockTagProvider::new);
        pack.addProvider(WaystonesItemTagProvider::new);
        pack.addProvider(WaystonesBiomeTagProvider::new);
    }
}
