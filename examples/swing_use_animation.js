// 示例 KubeJS 6 客户端脚本。
// 将此文件放入 <instance>/kubejs/client_scripts/ 目录。
// 注意：服务端脚本调用 player.swing() 时，当挥臂数据包到达客户端后，
// Mixin 也会自动桥接——无需在服务端使用 PunchyJS。

// 直接触发：在主手上播放 Punchy 的正常物品使用动画。
PunchyJS.swing();

// 指定手部。
PunchyJS.swing("off");

// 查询当前 Punchy 动画状态。
console.log("Punchy 动画片段: " + PunchyJS.getCurrentClip());
console.log("Punchy 正在播放: " + PunchyJS.isPlaying());
console.log("活动手臂: " + PunchyJS.getActiveArm());

// 实际使用场景：在已有的 KubeJS 客户端事件中挥臂。
// ClientEvents.tick(event => {
//     if (someCondition) {
//         PunchyJS.swing();
//     }
// });