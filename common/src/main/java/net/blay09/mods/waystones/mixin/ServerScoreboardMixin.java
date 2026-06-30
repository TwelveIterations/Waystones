package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.core.TeamWaystoneManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "addPlayerToTeam", at = @At("RETURN"))
    private void addPlayerToTeam(String player, PlayerTeam team, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (callbackInfo.getReturnValue()) {
            TeamWaystoneManager.playerTeamChanged(server, player);
        }
    }

    @Inject(method = "removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/world/scores/PlayerTeam;)V", at = @At("RETURN"))
    private void removePlayerFromTeam(String player, PlayerTeam team, CallbackInfo callbackInfo) {
        TeamWaystoneManager.playerTeamChanged(server, player);
    }
}
