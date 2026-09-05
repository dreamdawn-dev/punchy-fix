package com.punchyfix;

import com.punchyfix.config.CompatConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * 模组入口。仅客户端运行的附属模组：将外部的 {@code LivingEntity.swing()} 调用
 *（如 KubeJS 6 的 {@code player.swing()} 或任意 Java 模组）桥接到 Punchy 的
 * 使用物品动画中。
 */
@Mod(PunchyCompat.MOD_ID)
public final class PunchyCompat {
  public static final String MOD_ID = "punchyfix";

  public PunchyCompat() {
    if (FMLEnvironment.dist == Dist.CLIENT) {
      ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CompatConfig.SPEC);
    }
  }
}