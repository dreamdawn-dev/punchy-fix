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
 * Marks server swing echoes so the bridge never converts them into use-item
 * animations. When the server swings the local player's arm (attack echo,
 * mining echo, or the {@code shouldSwing()} result of an entity interaction /
 * item use), it sends {@code ClientboundAnimatePacket} with action 0 (main
 * hand) or 3 (off hand); the client applies it here by calling
 * {@code LivingEntity.swing(hand)} synchronously. Punchy has already tracked
 * the originating action locally, so re-bridging this echo would restart or
 * override the custom animation and make it stutter.
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
