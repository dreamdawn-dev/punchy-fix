package com.punchyfix.mixin.client;

import com.punchyfix.bridge.SwingBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 标记服务端挥臂回显，防止桥接逻辑将其转换为使用物品动画。
 * 当服务端挥动本地玩家的手臂时（攻击回显、挖掘回显，或实体交互/物品使用中
 * {@code shouldSwing()} 的结果），它会发送 action 为 0（主手）或 3（副手）的
 * {@code ClientboundAnimatePacket}；客户端在此处通过同步调用
 * {@code LivingEntity.swing(hand)} 来处理它。Punchy 已在本地追踪了原始动作，
 * 重新桥接此回显会导致自定义动画被重新开始或覆盖，造成卡顿。
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerAnimateMixin {
  @Inject(method = "handleAnimate", at = @At("HEAD"))
  private void punchyfix$markServerSwingEcho(ClientboundAnimatePacket packet, CallbackInfo ci) {
    int action = packet.getAction();
    if (action == 0 || action == 3) { // SWING_MAIN_HAND / SWING_OFF_HAND
      Minecraft mc = Minecraft.getInstance();
      if (mc != null && mc.level != null && mc.player != null) {
        Entity target = mc.level.getEntity(packet.getId());
        if (target == mc.player) {
          SwingBridge.markServerSwingEcho();
        }
      }
    }
  }

  @Inject(method = "handleAnimate", at = @At("RETURN"))
  private void punchyfix$clearServerSwingEcho(CallbackInfo ci) {
    SwingBridge.clearServerSwingEcho();
  }
}