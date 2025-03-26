# Souper Secret Settings

This page is for [Souper Secret Settings](https://modrinth.com/mod/souper-secret-settings) specific resourcepack features.

```
TutorialShader
├── assets
│   └── tutorial_shader
│       └── souper_secret_settings
│           └── layers
│               └── *example.json* [1]
├── pack.mcmeta
└── pack.png
```

Like the `luminance/` folder, the `souper_secret_settings/` folder goes in the namespaced folder, and it's where all of Soup's resourcepack features go.

The `layers/` folder is where layer presets can be added.

This could be used for fun effects with [Client Execution](https://modrinth.com/mod/client-execution), or just for having some presets you like the look of if you're adding some custom shaders.

The files just have the layer's data, like this:

```json
assets/tutorial_shader/souper_secret_settings/example.json [1]

{"shaders":[{"id":"soup:xor"},{"id":"minecraft:phosphor"},{"id":"soup:xor"}],"modifiers":[{"id":"soup:noise"}]}
```

To get the data to put in the layer json, you could go to `.minecraft/config/souper_secret_settings/layers` and copy over one of your saved layers, or you can use `/soup:layer copy saved <name>` or `/soup:layer copy current <index>` to copy the data of a layer you already have to your clipboard.

The layer can then be loaded in all the same ways saved layers normally can be, it will just be namespaced, so in this case `/soup:layer save load tutorial_shader:example`

## Spectator Shaders

If the resulting name is the same as an entities id, then it will be applied when spectating that entity

For instance `assets/minecraft/souper_secret_settings/layers/pig.json` for `minecraft:pig`

This has a [priority of 100](Perspective.md#spectator-shaders), and will even work for `minecraft:player` (note that if you just want a default shader for yourself, you can save to the `default` layer)

## Soup Groups

In the `luminance/` [jsons](PackSetup.md#luminancejson), the custom data can be used to add shaders to groups

```json
"custom": {
    "souper_secret_settings": {
        "groups": [ <...> ]
    }
}
```

The groups used by soup are (sorted roughly by how objectively they are defined):

- `"edible"`: shaders that can be randomly chosen when eating soup, these shouldnt be overly laggy (as in `soup:sorting`) or disruptive (as in `soup:stereogram`)
- `"depth"`: any shader that uses depth
- `"persistent"`: shaders that keep data across frames
- `"animated"`: shaders that use luminance_time
- `"filter"`: shaders where calculations are entirely per-pixel, with no interaction between them
- `"warp"`: shaders that modify what uv is used to get a pixel, with minimal changes to the color itself
- `"dither"`: shaders that have a dithering effect
- `"outline"`: shaders that have an outlining effect
- `"blur"`: shaders that in some way reduce detail in the image
- `"bloom"`: shaders that have a bloom-like effect, making some parts of the image brighter and spread
- `"retro"`: shaders that have a "retro" vibe