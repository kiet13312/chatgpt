# Chat LGBT Game

A lightweight landscape Android 2D world. Walk around one connected map and enter its districts. Coins, XP, level, resources and saved progress are shared locally.

## Open city life
The original **Sunport City** district adds open-world-style everyday activities:
- Walk around a small city with animated pedestrians and a day/night cycle.
- Buy a personal car and drive around using touch controls.
- Accept a delivery job and reach the glowing destination marker for a reward.
- Buy a home plot, return to it, and build or remodel the house.
- Visit the materials shop for building supplies.

This is an original, non-violent city-life interpretation inspired by open-world exploration, not a copy of GTA VI code, locations, characters, missions, maps or assets.

## Minecraft-inspired block building
- Persistent 14 × 8 tile house editor.
- Place or break wood, stone, glass, brick, leaves and door blocks.
- Breaking blocks returns the corresponding material.
- Craft glass from wood and stone.
- The house layout and material inventory are saved on the device.

The block editor is an original 2D building system inspired by block-building games, not a copy of Minecraft assets or code.

## Other districts
- Garden Defense — plant strategy and wave defense.
- Câu Cá Vạn Cân — fishing, three characters, catch weights and rod upgrades.
- Scratchbetter Studio — block-based creative studio with a stage and local script saving.
- Pixel Island — block gathering and building.
- Speed Circuit and Tank Track — driving and obstacle challenges.
- Gem City — a small treasure maze.
- Puzzle Park — pair matching.
- Rhythm Stage — timing and rhythm.
- Runner Trail — three-lane obstacle runner.
- Pet Village — feed, play with and clean a virtual pet.
- Farm & Tycoon / Town Tycoon — grow crops, harvest and expand a shop.
- Sports Field — goal-target challenge.
- Strategy Castle — three-lane upgrade-and-push strategy.
- Color Tag Arena — collect stars and avoid the roaming chaser.
- Arcade Plaza — quick tap-score mini game.

## Shared systems
Coins, gems, energy, XP, player level, daily reward, missions, shop, inventory, vehicle ownership, house ownership, materials and saved map position are shared or stored locally using Android SharedPreferences.

## Build
GitHub Actions builds a debug APK on every push to `main`, or through **Actions → Build Android APK → Run workflow**.

Artifact name: `chat-lgbt-game-debug-apk`.
APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Technical
- Native Android Java and Canvas.
- Landscape orientation.
- minSdk 23, compileSdk 35, Java 17.
- No external game engine or runtime game dependency.
- Existing standalone repositories remain untouched.
