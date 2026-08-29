# Punchy Fix

一个 Forge 1.20.1 客户端附属模组：当 **Java 模组**或 **KubeJS 6 脚本**调用玩家的手臂挥舞方法（`LivingEntity.swing()`，包括 KubeJS 的 `player.swing()`）时，让闭源模组 **Punchy** 播放它的"使用普通物品动画`interact` ，而不是什么都不做或走攻击动画。

## 原理

- KubeJS 6 的 `player.swing()` 最终调用 `LivingEntity.swing(hand, true)`。
- Punchy 自己也 Mixin 了这个方法：普通挥舞会被它记成"攻击"。
- 本模组以更高 Mixin 优先级（1500）抢先拦截 `swing()`，在确认这次挥舞不是
  攻击 / 挖掘 / 已跟踪的物品使用时，向 Punchy 的 `UseActionTracker` 写入一个`USE_ITEM` 触发器。Punchy 的 `UseItemStateMachine` 收到后会播放普通物品使用动画，而它自己的 swing 混入看到已有使用触发器，就不会再把它记成攻击。
- 守卫条件：客户端攻击请求后 1 秒内的回波、挖掘请求后 1 秒内的回波不会被桥接，避免把真实的攻击/挖掘误判成使用动作。物品使用不做抑制：每次成功到达客户端的`swing()` 都会播放使用动画，快速连点会由 Punchy 原生机制打断并重播。

## KubeJS 用法

```js
ItemEvents.rightClick(event=>{
  let player = event.player;
  player.swing();  // 能成功播放手臂挥舞动画
})
```

任意 Java 模组调用 `player.swing(hand)` 也会被自动桥接，无需额外依赖本模组 API。

## 编译要求

| 要求 | 说明 |
|------|------|
| JDK | **21+**（编译目标 Java 17，兼容 Minecraft 1.20.1） |
| Gradle | 通过 `gradlew` 自动下载，无需手动安装 |
| 依赖 jar | 需手动放入 `libs/` 目录 |

### 准备依赖

本项目依赖两个闭源/第三方模组的 jar 文件用于编译时链接，**这些 jar 不会包含在仓库中**。编译前请手动放置：

```
libs/
├── punchy-2.7.jar        # Punchy 2.7 (ARR 许可)
└── kubejs-2001.6.5.jar   # KubeJS Forge 2001.6.5
```

### 构建

```bash
./gradlew build
```

构建产物位于 `build/libs/punchyfix-<version>.jar`。
