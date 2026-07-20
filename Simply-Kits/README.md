# Kits Plugin

## Setup
1. Open this folder in IntelliJ IDEA (or your IDE of choice) as a Gradle project.
2. Make sure a Java 25 JDK is installed — the toolchain block in `build.gradle.kts`
   will make Gradle download/use it automatically if you don't have one.
3. Run `./gradlew build` (or `gradlew.bat build` on Windows). The compiled
   jar will appear in `build/libs/kits-plugin-1.0-SNAPSHOT.jar`.
4. Drop that jar into your Paper 26.2 server's `plugins/` folder and restart.

## Commands
- `/kitcreate <name>` — saves YOUR current inventory (armor, offhand, every
  main slot) as a kit with that name. Player-only.
- `/kitgive <name> <selector>` — gives the kit to matching players.
  `<selector>` can be a plain player name, or a vanilla selector like
  `@a`, `@p`, `@r`, `@a[distance=..10]`, etc.
- `/kitlist` — lists every saved kit name.
- `/kitdelete <name>` — deletes a saved kit.
- `/kitrandom <selector>` — gives a randomly chosen kit to matching players. The kit is rolled ONCE per command, so everyone targeted gets the same kit.
- `/kitrng <selector>` — secret/admin variant. Rolls an independent random kit for EACH targeted player. Only ops can see/use it by default (`kits.rng` permission).
- `/kits` — opens a clickable menu of every kit (plus a "Random Kit" button). Clicking one gives it only to you.

## Data
Kits are stored in `plugins/Kits/kits.yml`, one entry per slot, so you can
open that file and see exactly what's saved.
