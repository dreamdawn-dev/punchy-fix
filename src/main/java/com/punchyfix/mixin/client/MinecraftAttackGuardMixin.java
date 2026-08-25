package com.punchyfix.mixin.client;

import com.punchyfix.bridge.SwingBridge;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Marks client attack requests so the swing bridge does not convert real attack
 * swings
 * (including the multiplayer server echo) into use-item animations.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftAttackGuardMixin {
  @Inject(method = "startAttack", at = @At("HEAD"))
  private void punchycompat$markAttackRequest(CallbackInfoReturnable<Boolean> cir) {
    SwingBridge.onAttackRequested();
  }
}