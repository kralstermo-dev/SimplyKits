# SimplyKits
Simply kits is a minecraft kit plugin designed for ease of use.
Its a very simply and straight forward plugin. A list of commands is listed below, with their respecting description and permission node you can use in mods like luckperms

## Commands
|**Command**|**usage**|**Permission Node**|
|-|-|-|
|`/kitcreate <name>`|saves YOUR current inventory (armor, offhand, every<br />  main slot) as a kit with that name. Can only be run by players|`kits.create`|
|`/kitgive <name> <selector>`|gives the kit to matching players.<br />  `<selector>` can be a plain player name, or a vanilla selector like<br />  `@a`, `@p`, `@r`, `@a` `[distance=..10]`, etc.|`kits.give`|
|`/kitlist`|lists every saved kit name|`kits.list`|
|`/kitdelete <name>`|Deletes the named kit|`kits.delete`|
|`/kitrandom <selector>`|Gives a randomly chosen kit to selected players. The kit is rolled ONCE per command, so everyone targeted gets the same kit|`kits.give`|
|`/kitrng <selector>`|Rolls an independent random kit for EACH targeted player|`kits.rng`|
|`/kits`|Opens a clickable menu of every kit (plus a "Random Kit" button). Clicking a kit gives you a chosen kit|`kits.gui`|


### Data
Kits are stored in `plugins/Kits/kits.yml`

### Setup
**IF YOU ARE NOT JUST DOWNLOADING THE .JAR FILE PLEASE READ THE "LICENSE.md" FILE**
1. Open this Repo in a IDE of your choice (i would recommend IntelliJ IDEA for this) as a Gradle project
2. Make sure you have java 25 JDK is installed -  the toolchain block in `build.gradle.kt` will make Gradle download/use it automatically if you don't have one.
4. Run `./gradlew build` (or `gradlew.bat build` on Windows). The compiledjar will appear in `build/libs/kits-plugin-1.0-SNAPSHOT.jar`.
5. Drop that jar into your server's plugins/ folder and restart.

---

## Credits

This plugin was made by Morrmo/Morrhawk33
