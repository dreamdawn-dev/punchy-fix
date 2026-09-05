package com.punchyfix.mixin.client;

import com.punchyfix.bridge.SwingBridge;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在 Punchy 自身的 {@code LivingEntitySwingMixin} 之前运行（优先级 1500 > 1000）。
 * 如果我们先记录了 USE_ITEM 触发器，Punchy 的 swing Mixin 会检测到
 * {@code UseActionTracker.peekTrigger() != null} 并跳过记录攻击，
 * 从而使挥臂变为正常的物品使用动画，而非攻击动画。
 */
@Mixin(value = LivingEntity.class, priority = 1500)
public abstract class LivingEntitySwingBridgeMixin {
  @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"))
  private void punchycompat$bridgeExternalSwing(InteractionHand hand, boolean fromServer, CallbackInfo ci) {
    SwingBridge.tryBridgeSwing((LivingEntity) (Object) this, hand);
  }
}