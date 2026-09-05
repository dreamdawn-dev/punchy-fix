package com.punchyfix.bridge;

import com.punchyfix.config.CompatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import punchy.client.state.AttackActionTracker;
import punchy.client.state.UseActionTracker;
import punchy.config.PunchyConfig;

/**
 * 核心桥接逻辑。在 {@code LivingEntity.swing()} 中被调用（我们的 Mixin 优先级
 * 高于 Punchy 自身的 swing Mixin）。当挥臂动作并非由 Punchy 已追踪的攻击、
 * 挖掘或物品使用流程触发时，我们记录一个 {@code USE_ITEM} 触发器，使 Punchy
 * 的 UseItemStateMachine 播放正常的物品使用动画（即通用物品的"交互"动画片段）。
 */
public final class SwingBridge {
  private static final long NANOS_PER_MS = 1_000_000L;

  private static volatile long lastAttackRequestNanos;
  private static volatile long lastMiningRequestNanos;
  private static volatile long lastUseRequestNanos;
  private static volatile boolean serverEchoPending;

  private SwingBridge() {
  }

  /**
   * 在 Minecraft.startAttack() 头部调用：标记客户端攻击请求正在进行中。
   */
  public static void onAttackRequested() {
    lastAttackRequestNanos = System.nanoTime();
  }

  /**
   * 在 MultiPlayerGameMode.startDestroyBlock() 头部调用：标记一个挖掘挥臂。
   */
  public static void onMiningRequested() {
    lastMiningRequestNanos = System.nanoTime();
  }

  /**
   * 在客户端发起的使用/交互流程（useItem、useItemOn、interact、interactAt）
   * 返回时调用，这些流程 Punchy 已在本地追踪。在 {@code useEchoWindowMs}
   * 窗口内到达的服务端挥臂回显是该流程的回显，不应桥接；窗口外的回显则是外部
   * 服务端挥臂（如 KubeJS 服务端脚本中的 {@code player.swing()}），仍会被桥接。
   */
  public static void onUseRequested() {
    lastUseRequestNanos = System.nanoTime();
  }

  /**
   * 在 {@code ClientPacketListener.handleAnimate()} 头部调用，当动画包是
   * 本地玩家的主手/副手挥臂时触发。紧随其后的 swing 调用是服务端回显
   *（服务端决定的攻击、挖掘、实体交互或物品使用挥臂），而非外部挥臂，
   * 因此桥接逻辑不应将其转换为使用物品触发器。
   */
  public static void markServerSwingEcho() {
    serverEchoPending = true;
  }

  /** 清除待处理的服务端回显标记（安全网，防止 swing 从未执行的情况）。 */
  public static void clearServerSwingEcho() {
    serverEchoPending = false;
  }

  private static boolean consumeServerSwingEcho() {
    boolean pending = serverEchoPending;
    serverEchoPending = false;
    return pending;
  }

  /**
   * 尝试将外部挥臂转换为 Punchy 的使用物品动画。
   *
   * @return 如果成功记录了 USE_ITEM 触发器则返回 true
   */
  public static boolean tryBridgeSwing(LivingEntity entity, InteractionHand hand) {
    boolean serverEcho = consumeServerSwingEcho();
    if (entity == null) {
      return false;
    }
    if (!CompatConfig.ENABLED.get()) {
      return false;
    }
    Minecraft mc = Minecraft.getInstance();
    if (mc == null || mc.player == null || entity != mc.player) {
      // Punchy 只渲染本地玩家的第一人称手臂。
      return false;
    }
    if (!ModList.get().isLoaded("punchy")) {
      return false;
    }
    if (!PunchyConfig.isModEnabled()) {
      return false;
    }
    InteractionHand resolvedHand = hand == null ? InteractionHand.MAIN_HAND : hand;

    long now = System.nanoTime();

    // 客户端发起且 Punchy 已在本地播放动画的使用/交互流程的服务端回显：
    // 跳过它，避免自定义动画片段被重新开始。
    // 外部服务端挥臂（没有近期客户端使用流程）仍会被桥接。
    if (serverEcho && within(now, lastUseRequestNanos, (long) CompatConfig.USE_ECHO_WINDOW_MS.get() * NANOS_PER_MS)) {
      return false;
    }

    // Punchy 已经在处理此次挥臂的使用动作。
    if (UseActionTracker.peekTrigger() != null) {
      return false;
    }
    // 单机攻击挥臂（攻击触发器仍在等待中）。
    if (AttackActionTracker.isTriggered()) {
      return false;
    }

    // 多人游戏中客户端发起攻击的服务端回显。
    if (within(now, lastAttackRequestNanos, (long) CompatConfig.ATTACK_ECHO_WINDOW_MS.get() * NANOS_PER_MS)) {
      return false;
    }
    // 挖掘挥臂（startDestroyBlock 内的本地调用及其服务端回显）。
    if (within(now, lastMiningRequestNanos, (long) CompatConfig.MINING_ECHO_WINDOW_MS.get() * NANOS_PER_MS)) {
      return false;
    }

    // 所有非攻击/挖掘回显的外部挥臂都转为使用物品触发器。
    // Punchy 原生每 tick 消耗一个触发器并重新播放动画片段（快速重新触发时
    // 使用 skip-blend-in 跳过混合过渡），因此连续快速挥臂会打断并重播动画。
    UseActionTracker.recordUse(resolvedHand, UseActionTracker.TriggerType.USE_ITEM);
    return true;
  }

  private static boolean within(long now, long stamp, long windowNanos) {
    return stamp > 0L && now - stamp <= windowNanos;
  }
}