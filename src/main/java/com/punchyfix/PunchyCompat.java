package com.punchyfix;

import com.punchyfix.config.CompatConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Entry point. Client-only addon: bridges external {@code LivingEntity.swing()}
 * calls
 * (KubeJS 6 {@code player.swing()} or any Java mod) into Punchy's use-item
 * animation.
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