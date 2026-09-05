package com.punchyfix.mixin.client;

import com.punchyfix.bridge.SwingBridge;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 标记客户端攻击请求，防止桥接逻辑将真正的攻击挥臂
 *（包括多人游戏的服务端回显）转换为使用物品动画。
 */
@Mixin(Minecraft.class)
public abstract class MinecraftAttackGuardMixin {
  @Inject(method = "startAttack", at = @At("HEAD"))
  private void punchycompat$markAttackRequest(CallbackInfoReturnable<Boolean> cir) {
    SwingBridge.onAttackRequested();
  }
}