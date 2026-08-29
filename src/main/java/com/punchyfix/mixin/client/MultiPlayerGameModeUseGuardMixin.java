package com.punchyfix.mixin.client;

import com.punchyfix.bridge.SwingBridge;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Marks client-initiated use/interact flows so the swing bridge can tell a
 * server swing echo of that flow (suppress: Punchy already animated it) apart
 * from an external server swing such as KubeJS {@code player.swing()} in
 * server scripts (bridge).
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeUseGuardMixin {
  @Inject(method = "useItem", at = @At("RETURN"))
  private void punchyfix$markUseItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    if (punchyfix$isTracked(cir.getReturnValue())) {
      SwingBridge.onUseRequested();
    }
  }

  @Inject(method = "useItemOn", at = @At("RETURN"))
  private void punchyfix$markUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
    if (punchyfix$isTracked(cir.getReturnValue()) || punchyfix$isBoneMealUse(player, hand)) {
      SwingBridge.onUseRequested();
    }
  }

  @Inject(method = "interact", at = @At("RETURN"))
  private void punchyfix$markInteract(Player player, Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    if (punchyfix$isTracked(cir.getReturnValue())) {
      SwingBridge.onUseRequested();
    }
  }

  @Inject(method = "interactAt", at = @At("RETURN"))
  private void punchyfix$markInteractAt(Player player, Entity entity, EntityHitResult hit, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    if (punchyfix$isTracked(cir.getReturnValue())) {
      SwingBridge.onUseRequested();
    }
  }

  private static boolean punchyfix$isTracked(InteractionResult result) {
    return result != null && result.consumesAction();
  }

  private static boolean punchyfix$isBoneMealUse(Player player, InteractionHand hand) {
    if (player != null && hand != null) {
      ItemStack stack = player.getItemInHand(hand);
      return stack != null && stack.is(Items.BONE_MEAL);
    }
    return false;
  }
}
