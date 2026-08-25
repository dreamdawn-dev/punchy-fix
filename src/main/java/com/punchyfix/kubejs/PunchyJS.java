package com.punchyfix.kubejs;

import com.punchyfix.bridge.SwingBridge;
import com.punchyfix.config.CompatConfig;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.fml.ModList;
import punchy.client.animation.PunchyAnimationManager;
import punchy.client.animation.data.AnimationClip;
import punchy.config.PunchyConfig;

/**
 * Client-side KubeJS binding ({@code PunchyJS}).
 */
public final class PunchyJS {

  /**
   * Triggers Punchy's normal item-use animation on the main hand.
   * Equivalent to an external {@code player.swing()} call.
   */
  public boolean swing() {
    return swing("main");
  }

  /**
   * Triggers Punchy's normal item-use animation.
   *
   * @param hand "main"/"mainhand" or "off"/"offhand"/"left"
   */
  public boolean swing(String hand) {
    Minecraft mc = Minecraft.getInstance();
    if (mc == null || mc.player == null) {
      return false;
    }
    return SwingBridge.tryBridgeSwing(mc.player, parseHand(hand));
  }

  public boolean isLoaded() {
    return ModList.get().isLoaded("punchy");
  }

  public boolean isEnabled() {
    return isLoaded() && PunchyConfig.isModEnabled();
  }

  public boolean isPlaying() {
    if (!isLoaded()) {
      return false;
    }
    return PunchyAnimationManager.POSE_HANDLER.isPlaying();
  }

  /**
   * Name of the Punchy animation clip currently blended on the pose handler, or
   * null.
   */
  public String getCurrentClip() {
    if (!isLoaded()) {
      return null;
    }
    AnimationClip clip = PunchyAnimationManager.POSE_HANDLER.getCurrentClip();
    return clip == null ? null : clip.getName();
  }

  /** "RIGHT", "LEFT" or null when both arms are active. */
  public String getActiveArm() {
    if (!isLoaded()) {
      return null;
    }
    HumanoidArm arm = PunchyAnimationManager.POSE_HANDLER.getActiveArm();
    return arm == null ? null : arm.name();
  }

  public boolean isBridgeEnabled() {
    return CompatConfig.ENABLED.get();
  }

  public int getAttackEchoWindowMs() {
    return CompatConfig.ATTACK_ECHO_WINDOW_MS.get();
  }

  public int getMiningEchoWindowMs() {
    return CompatConfig.MINING_ECHO_WINDOW_MS.get();
  }

  private static InteractionHand parseHand(String hand) {
    if (hand == null) {
      return InteractionHand.MAIN_HAND;
    }
    return switch (hand.toLowerCase(Locale.ROOT)) {
      case "off", "offhand", "off_hand", "left" -> InteractionHand.OFF_HAND;
      default -> InteractionHand.MAIN_HAND;
    };
  }
}