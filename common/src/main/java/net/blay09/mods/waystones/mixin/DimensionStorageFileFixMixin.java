package net.blay09.mods.waystones.mixin;

import net.minecraft.util.filefix.access.FileRelation;
import net.minecraft.util.filefix.fixes.DimensionStorageFileFix;
import net.minecraft.util.filefix.operations.FileFixOperations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DimensionStorageFileFix.class)
public class DimensionStorageFileFixMixin {
    @Inject(method = "makeFixer", at = @At("RETURN"))
    private void makeFixer(CallbackInfo ci) {
        final var fixer = (DimensionStorageFileFix) (Object) this;
        fixer.addFileFixOperation(
                FileFixOperations.applyInFolders(
                        FileRelation.DATA,
                        List.of(
                                FileFixOperations.move("waystones.dat", "waystones/waystones.dat"),
                                FileFixOperations.move("waystones_name_generator.dat", "waystones/name_generator.dat")
                        )
                )
        );
    }
}
