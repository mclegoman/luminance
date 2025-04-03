# Perspective

This page is for [Perspective](https://modrinth.com/mod/mclegoman-perspective) specific resourcepack features.

```
TutorialShader
├── assets
│   └── tutorial_shader
│       └── textured_entity
│               └── *example.json* [1]
├── pack.mcmeta
└── pack.png
```

## Spectator Shaders
Perspective's `Textured Entity` system allows for specific entities to have shaders render when spectating the entity.  

> [!NOTE]  
> This feature requires `Named Textured Entity` to be enabled in the Perspective config.  
> This is enabled by default.  
>  
> Perspective uses it's `ShaderPack` system to layer several shaders together in a stack.  
> You won't need to worry about this for this tutorial since luminance shaders are automatically added as stacks of one.  
>  
> For more info, check out [Perspective Wiki/Shader-Packs](https://github.com/mclegoman/perspective/wiki/Shader-Packs).  

Like the `luminance/` folder, the `textured_entity/` folder goes in the namespaced folder, this contains all of Perspective's textured entities.  

Textured Entity is mainly used to swap out the entities texture when named.  
However, for this example we're going to leave the textures as they are, and set the default unnamed zombie's spectator shader to `minecraft:phosphor`.  

```json
assets/tutorial_shader/textured_entity/example.json [1]

{
	"entity": "minecraft:zombie",
	"overrides": [
		{ "prefix": "", "suffix": "", "texture": "" },
		{ "prefix": "", "suffix": "_overlay", "texture": "" },
		{ "prefix": "", "suffix": "_cape", "texture": "" }
	],
	"item_group": false,
	"shader": {
		"pack": "minecraft:phosphor"
	}
}
```
> [!NOTE]  
> - Leaving the `texture` blank in the `overrides` keeps the default texture, overrides can differ for each entity.  
> - Since we're not changing the texture, we don't need to add an egg to the item group.  
> - All luminance shaders in the `luminance:main` shader registry are added to the `perspective:main` shader pack registry.  
>  
> For more info, check out [Perspective Wiki/Textured-Entity](https://github.com/mclegoman/perspective/wiki/Textured-Entity), and [Perspective Wiki/Shader-Packs](https://github.com/mclegoman/perspective/wiki/Shader-Packs).  

`minecraft:phosphor` will now be rendered when spectating a `minecraft:zombie` without a named textured entity.

TODO: explain priorities (note that vanilla has an effective priority *below* 0, which is a funny property to have since anything below 0 is theoretically disabled)