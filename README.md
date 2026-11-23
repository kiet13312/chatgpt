# Chat LGBT Game

**One Android app combining the game ideas built across earlier projects.**

## Modes
- **Garden Defense** — 5×9 garden, three plant types, sun economy, moving enemies, waves and level progression.
- **Câu Cá Vạn Cân** — three selectable characters, casting, reeling, fish weights, shared rewards and rod upgrades.
- **Scratchbetter Studio** — block palette, editable script stack, stage preview, variables and local script saving.
- **Tank Adventures** — top-down tank survival and target scoring.
- **Furni Heist** — an arcade gem-collection escape challenge.
- **Sinh Tồn Khối** — gather wood and stone, craft and place or recover blocks.
- **Đua Xe** and **Mini Game** — quick reaction and lane-dodging modes.

## Shared systems
Player level and XP, coins, gems, energy, daily gift, missions, shop, inventory and local save data are shared across modes.

## Build
GitHub Actions builds a debug APK on every push to main, or by using Actions → Build Android APK → Run workflow.

The artifact is named chat-lgbt-game-debug-apk. The APK is also generated at:
app/build/outputs/apk/debug/app-debug.apk

## Technical
Native Android Java, Canvas rendering, landscape orientation, min SDK 23, compile SDK 35, and no third-party game engine or runtime dependency.

This combined project is independent; existing standalone repositories are not modified.
