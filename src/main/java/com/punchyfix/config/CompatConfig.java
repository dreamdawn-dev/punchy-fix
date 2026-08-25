package com.punchyfix.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class CompatConfig {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
      .comment("Master toggle for the swing bridge.")
      .define("enabled", true);

  public static final ForgeConfigSpec.IntValue ATTACK_ECHO_WINDOW_MS = BUILDER
      .comment(
          "Milliseconds after a client attack request during which swing() calls are treated as the server attack echo and are NOT bridged.")
      .defineInRange("attackEchoWindowMs", 1000, 100, 5000);

  public static final ForgeConfigSpec.IntValue MINING_ECHO_WINDOW_MS = BUILDER
      .comment(
          "Milliseconds after a destroy-block request during which swing() calls are treated as mining swings and are NOT bridged.")
      .defineInRange("miningEchoWindowMs", 1000, 100, 5000);

  public static final ForgeConfigSpec SPEC = BUILDER.build();

  private CompatConfig() {
  }
}