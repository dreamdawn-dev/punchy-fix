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
 * 客户端 KubeJS 绑定（{@code PunchyJS}）。
 */
public final class PunchyJS {

  /**
   * 在主手上触发 Punchy 的正常物品使用动画。
   * 等效于外部的 {@code player.swing()} 调用。
   */
  public boolean swing() {
    return swing("main");
  }

  /**
   * 触发 Punchy 的正常物品使用动画。
   *
   * @param hand "main"/"mainhand" 表示主手，或 "off"/"offhand"/"left" 表示副手
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
   * 当前在 Pose 处理器上混合的 Punchy 动画片段名称，无动画时返回 null。
   */
  public String getCurrentClip() {
    if (!isLoaded()) {
      return null;
    }
    AnimationClip clip = PunchyAnimationManager.POSE_HANDLER.getCurrentClip();
    return clip == null ? null : clip.getName();
  }

  /** "RIGHT"、"LEFT"，当双臂都处于活动状态时返回 null。 */
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