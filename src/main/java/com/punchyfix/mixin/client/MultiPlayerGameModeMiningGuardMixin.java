package com.punchyfix.mixin.client;

import com.punchyfix.bridge.SwingBridge;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Marks destroy-block requests so mining swings (local call and server echo)
 * are not converted into use-item animations.
 * <p>
 * Each tick while holding left-click on the same block, the vanilla client
 * calls {@code continueDestroyBlock} which internally calls
 * {@code player.swing(MAIN_HAND)}. Without refreshing the mining timestamp
 * here, the echo window expires after {@code miningEchoWindowMs} (default 1s)
 * and every subsequent tick's swing gets bridged into a use-item animation,
 * causing the arm to flail rapidly.
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMiningGuardMixin {
  @Inject(method = "startDestroyBlock", at = @At("HEAD"))
  private void punchycompat$markMiningRequest(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
    SwingBridge.onMiningRequested();
  }

  @Inject(method = "continueDestroyBlock", at = @At("HEAD"))
  private void punchycompat$markContinueMiningRequest(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
    SwingBridge.onMiningRequested();
  }
}