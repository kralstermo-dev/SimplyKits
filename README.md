# SimplyKits
SimplyKits is a Minecraft kit plugin designed for ease of use. It's a very simple and straightforward plugin. A list of commands is listed below, with their respective descriptions and the permission nodes you can use in plugins like LuckPerms.

## Commands
| **Command** | **Usage** | **Permission Node** |
|---|---|---|
| `/kitcreate <name>` | Saves YOUR current inventory (armor, offhand, every main slot) as a kit with that name. Can only be run by players. | `kits.create` |
| `/kitgive <name> <selector>` | Gives the kit to matching players. `<selector>` can be a plain player name, or a vanilla selector like `@a`, `@p`, `@r`, or `@a[distance=..10]`. | `kits.give` |
| `/kitlist` | Lists every saved kit name. | `kits.list` |
| `/kitdelete <name>` | Deletes the named kit. | `kits.delete` |
| `/kitrandom <selector>` | Gives a randomly chosen kit to selected players. The kit is rolled ONCE per command, so everyone targeted gets the same kit. | `kits.give` |
| `/kitrng <selector>` | Rolls an independent random kit for EACH targeted player. | `kits.rng` |
| `/kits` | Opens a clickable menu of every kit (plus a "Random Kit" button). Clicking a kit gives you that kit. | `kits.gui` |

### Data
Kits are stored in `plugins/Kits/kits.yml`

### Setup
**IF YOU ARE NOT JUST DOWNLOADING THE .JAR FILE PLEASE READ THE "LICENSE.md" FILE**
1. Open this repo in an IDE of your choice (IntelliJ IDEA is highly recommended) as a Gradle project.
2. Make sure a Java 25 JDK is installed—the toolchain block in `build.gradle.kts` will make Gradle download/use it automatically if you don't have one.
3. Run `./gradlew build` (or `gradlew.bat build` on Windows). 
4. The compiled jar will appear in `build/libs/kits-plugin-1.0-SNAPSHOT.jar`.
5. Drop that jar into your server's `plugins/` folder and restart the server.

### Bugs
if you find any bugs please use the github issues

---

## Credits
This plugin was made by Morrmo / Morrhawk33
