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
 * are
 * not converted into use-item animations.
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMiningGuardMixin {
  @Inject(method = "startDestroyBlock", at = @At("HEAD"))
  private void punchycompat$markMiningRequest(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
    SwingBridge.onMiningRequested();
  }
}