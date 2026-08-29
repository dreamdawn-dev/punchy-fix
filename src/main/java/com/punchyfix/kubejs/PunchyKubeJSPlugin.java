package com.punchyfix.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

/**
 * KubeJS 6 plugin. Registered via
 * META-INF/services/dev.latvian.mods.kubejs.KubeJSPlugin.
 */
public final class PunchyKubeJSPlugin extends KubeJSPlugin {
  @Override
  public void registerBindings(BindingsEvent event) {
    if (event.getType() == ScriptType.CLIENT) {
      event.add("PunchyJS", new PunchyJS());
    }
  }
}