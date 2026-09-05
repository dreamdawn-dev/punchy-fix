package com.punchyfix.mixin.client;

import com.punchyfix.bridge.SwingBridge;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 标记挖掘方块请求，防止挖掘挥臂（本地调用和服务端回显）被转换为使用物品动画。
 * <p>
 * 在按住左键挖掘同一方块时，原版客户端每 tick 都会调用
 * {@code continueDestroyBlock}，其内部会调用
 * {@code player.swing(MAIN_HAND)}。如果不在此处刷新挖掘时间戳，
 * 回显窗口会在 {@code miningEchoWindowMs}（默认 1 秒）后过期，
 * 导致后续每 tick 的挥臂都被桥接为使用物品动画，造成手臂快速抽动。
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMiningGuardMixin {
  @Inject(method = "startDestroyBlock", at = @At("HEAD"))
  private void punchycompat$markMiningRequest(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
    SwingBridge.onMiningRequested();
  }

  @Inject(method = "continueDestroyBlock", at = @At("HEAD"))
  private void punchycompat$markContinueMiningRequest(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
    SwingBridge.onMiningRequested();
  }
}