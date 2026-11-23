# Chat LGBT Game

A lightweight 2D Android game world. The player walks around one connected top-down map; every building opens a playable activity, and exiting it returns to the same place in the world.

## Districts on the shared map
- Garden Defense — plant strategy and wave defense.
- Câu Cá Vạn Cân — fishing, three characters, catch weights and rod upgrades.
- Scratchbetter Studio — block-based creative studio with a stage and local script saving.
- Pixel Island — block gathering, crafting and building.
- Speed Circuit and Tank Track — arcade driving and obstacle challenges.
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

## Shared game systems
Coins, gems, energy, XP, player level, daily reward, missions, shop, inventory and saved map position are shared across the world. Progress is stored locally with Android SharedPreferences.

The added activities are original lightweight mini-game interpretations of popular genres, not copies of commercial titles or their assets. The world map and structures are drawn with Android Canvas; the themed district illustrations are included as local vector resources.

## Build
GitHub Actions builds a debug APK on every push to `main`, or by using **Actions → Build Android APK → Run workflow**.

Artifact name: `chat-lgbt-game-debug-apk`.

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Technical
- Native Android Java and Canvas.
- Landscape orientation.
- minSdk 23, compileSdk 35, Java 17.
- No external game engine or runtime game dependency.
- Existing standalone repositories remain untouched.
