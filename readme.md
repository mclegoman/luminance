![](./assets/logo/Luminance.png)
## About
Luminance is a library mod that helps with shader rendering, and contains helper classes, and event hooks to help other mods with game rendering.  

# Features  
- **Adds Dynamic Shader Uniforms**
  - These let post-processing shaders have more information about what's happening in-game.
  - Luminance comes with a bunch of these by default, but you can add your own too!
- **Adds shaders that have been used in previous minecraft versions.**
  - _Requires [Perspective](https://modrinth.com/mod/mclegoman-perspective) or [Souper Secret Settings](https://modrinth.com/mod/souper-secret-settings)._
- **Compatibility**  
  - Luminance takes care of rendering shaders, so you don't have to!  
  - You can check if Iris Shaders are enabled using Luminance.  
    - This means you don't have to have Iris added to your development environment!  
  - You can replace your ModMenu icon using Luminance!  
    - Make your mod icon change depending on the time of year, or via a config option, anything's possible!  

## Dependencies
- [Fabric API](https://modrinth.com/mod/fabric-api) or [Quilted Fabric API (QFAPI)](https://modrinth.com/mod/qsl)
    - `fabric-resource-loader-v0`
    - `fabric-key-binding-api-v1`
    - `fabric-lifecycle-events-v1`
- Java 21 or later.
  - Built with the Microsoft build of OpenJDK 21.0.2.

## How to add Luminance to your mod:  
1. Add the following lines to the repository section of your build.gradle:  
```
exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth"
				url = "https://api.modrinth.com/maven"
			}
		}
		filter {
			includeGroup "maven.modrinth"
		}
	}
```
2. Add the following line to the dependencies section of your build.gradle:  
```
include(modImplementation "maven.modrinth:luminance:${project.luminance}")
```
3. Add the following line to your gradle.properties:  
   1. Replace 1.0.0-alpha.3.+1.20.5 with the latest version of Luminance!  
      1. This can be found on the version page on [Modrinth](https://modrinth.com/mod/luminance).  
```
luminance = 1.0.0-alpha.3+1.20.5
```
4. Reload gradle and you're ready to use Luminance!

## Plans  
- [x] Shader registration through resource packs.  
- [ ] Shader rendering.  
- [x] Dynamic Shader Uniforms  
- [ ] Fabric, Quilt, and NeoForge support.
  - [x] Fabric  
  - [ ] Quilt (Native)  
  - [ ] NeoForge  

## Also check out  
[Our Modrinth Page](https://modrinth.com/mod/luminance)  
[Perspective](https://modrinth.com/mod/mclegoman-perspective) by MCLegoMan  
[Souper Secret Settings](https://modrinth.com/mod/souper-secret-settings) by Nettakrim  

#
Licensed under LGPL-3.0-or-later.

**This mod is not affiliated with/endorsed by Mojang Studios or Microsoft.**  
Some game assets are property of Mojang Studios and fall under Minecraft EULA.
