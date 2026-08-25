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
 * Core bridge. Called from {@code LivingEntity.swing()} (our mixin runs before
 * Punchy's own
 * swing mixin). When the swing was NOT initiated by an attack, mining or
 * item-use flow that
 * Punchy already tracks, we record a {@code USE_ITEM} trigger so Punchy's
 * UseItemStateMachine
 * plays the normal item-use animation (the "interact" clip for generic items).
 */
public final class SwingBridge {
  private static final long NANOS_PER_MS = 1_000_000L;

  private static volatile long lastAttackRequestNanos;
  private static volatile long lastMiningRequestNanos;

  private SwingBridge() {
  }

  /**
   * Called at HEAD of Minecraft.startAttack(): marks that a client attack is in
   * flight.
   */
  public static void onAttackRequested() {
    lastAttackRequestNanos = System.nanoTime();
  }

  /**
   * Called at HEAD of MultiPlayerGameMode.startDestroyBlock(): marks a mining
   * swing.
   */
  public static void onMiningRequested() {
    lastMiningRequestNanos = System.nanoTime();
  }

  /**
   * Tries to convert an external swing into Punchy's use-item animation.
   *
   * @return true if a USE_ITEM trigger was recorded
   */
  public static boolean tryBridgeSwing(LivingEntity entity, InteractionHand hand) {
    if (entity == null) {
      return false;
    }
    if (!CompatConfig.ENABLED.get()) {
      return false;
    }
    Minecraft mc = Minecraft.getInstance();
    if (mc == null || mc.player == null || entity != mc.player) {
      // Only the local player's first-person arms are rendered by Punchy.
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

    // Punchy is already handling a use action for this swing.
    if (UseActionTracker.peekTrigger() != null) {
      return false;
    }
    // Single-player attack swing (attack trigger still pending).
    if (AttackActionTracker.isTriggered()) {
      return false;
    }

    // Multiplayer echo of a client-initiated attack.
    if (within(now, lastAttackRequestNanos, (long) CompatConfig.ATTACK_ECHO_WINDOW_MS.get() * NANOS_PER_MS)) {
      return false;
    }
    // Mining swing (local call inside startDestroyBlock, plus its server echo).
    if (within(now, lastMiningRequestNanos, (long) CompatConfig.MINING_ECHO_WINDOW_MS.get() * NANOS_PER_MS)) {
      return false;
    }

    // Every external swing that is not an attack/mining echo becomes a use-item
    // trigger.
    // Punchy natively consumes one trigger per tick and restarts the clip (with
    // skip-blend-in
    // for fast re-triggers), so rapid repeated swings interrupt and replay the
    // animation.
    UseActionTracker.recordUse(resolvedHand, UseActionTracker.TriggerType.USE_ITEM);
    return true;
  }

  private static boolean within(long now, long stamp, long windowNanos) {
    return stamp > 0L && now - stamp <= windowNanos;
  }
}