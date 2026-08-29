// Example KubeJS 6 CLIENT script.
// Drop this into <instance>/kubejs/client_scripts/.
// Note: a server-side script calling player.swing() is ALSO bridged automatically by the
// mixin when the swing packet reaches the client - you do not need PunchyJS there.

// Direct trigger: play Punchy's normal item-use animation on the main hand.
PunchyJS.swing();

// With an explicit hand.
PunchyJS.swing("off");

// Query the current Punchy animation state.
console.log("Punchy clip: " + PunchyJS.getCurrentClip());
console.log("Punchy playing: " + PunchyJS.isPlaying());
console.log("Active arm: " + PunchyJS.getActiveArm());

// Real-world pattern: swing inside an existing KubeJS client event.
// ClientEvents.tick(event => {
//     if (someCondition) {
//         PunchyJS.swing();
//     }
// });
