package com.punchyfix.mixin.client;

import com.punchyfix.bridge.SwingBridge;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Runs BEFORE Punchy's own {@code LivingEntitySwingMixin} (priority 1500 >
 * 1000).
 * If we record a USE_ITEM trigger first, Punchy's swing mixin sees
 * {@code UseActionTracker.peekTrigger() != null} and skips recording an attack,
 * so the swing becomes a normal item-use animation instead of an attack.
 */
@Mixin(value = LivingEntity.class, priority = 1500)
public abstract class LivingEntitySwingBridgeMixin {
  @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"))
  private void punchycompat$bridgeExternalSwing(InteractionHand hand, boolean fromServer, CallbackInfo ci) {
    SwingBridge.tryBridgeSwing((LivingEntity) (Object) this, hand);
  }
}