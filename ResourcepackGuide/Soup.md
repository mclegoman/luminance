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
assets/tutorial_shader/souper_secret_settings/layers/example.json [1]

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

You can add new groups by either adding them to the "groups" list, or by adding files in `assets/<namespace>/souper_secret_settings/groups/<registry>/<name>.json`, like this:

```
TutorialShader
├── assets
│   └── tutorial_shader
│       └── souper_secret_settings
│           └── groups
│               └── luminance_main
│                   └── *depthless.json* [2]
├── pack.mcmeta
└── pack.png
```

```json
assets/tutorial_shader/souper_secret_settings/groups/luminance_main/depthless.json [2]

{"entries":["+all","-random_depth"]}
```

Like with layers, its easiest to make these in game, however there isnt a way to copy them to your clipboard, so you have to find them in `.minecraft/config/souper_secret_settings/groups/...` then move them to your resourcepack

The `luminance_main/` folder is the name of the [registry](PackSetup.md#optional-fields) with its `:` replaced with `_`, so to make a group for modifiers, put it in `souper_secret_settings_modifiers/`

## Modifiers

Soup can have shaders that run at the start and end of each layer, or between shaders within the layer.

This can be used for a few fun effects, for an example i'll use `soup:mix`, which mixes the original image from before the layer is rendered, with the one afterwards.

To make a shader get treated as a modifier, it can be added to the `souper_secret_settings:modifiers` shader [registry](PackSetup.md#optional-fields), like this:

```json
assets/soup/luminance/mix.json

{
  "post_effect": "soup:mix",
  "enabled": true,
  "disable_ui_rendertype": true,
  "registries": [
    "souper_secret_settings:modifiers" <- this is important!
  ],
  "custom": {}
}
```

Then, instead of the usual `"passes": [...]` list in the [post_effect](AddingShaders.md#passes) json, there is a `"custom_passes": {...}` set, which itself contains lists of passes:

```json
assets/soup/post_effect/mix.json

{
    "targets": {
        "0": {},
        "base": {"persistent": true}
    },
    "custom_passes": {
        "souper_secret_settings:before_layer_render": [
            {
                "program": "minecraft:post/blit",
                "inputs": [
                    {
                        "sampler_name": "In",
                        "target": "minecraft:main"
                    }
                ],
                "output": "base"
            }
        ],
        "souper_secret_settings:after_layer_render": [
            {
                "program": "soup:post/mix",
                "inputs": [
                    {
                        "sampler_name": "In",
                        "target": "base"
                    },
                    {
                        "sampler_name": "Base",
                        "target": "minecraft:main"
                    }
                ],
                "output": "0"
            },
            {
                "program": "minecraft:post/blit",
                "inputs": [
                    {
                        "sampler_name": "In",
                        "target": "0"
                    }
                ],
                "output": "minecraft:main"
            }
        ]
    }
}
```

Here there is one pass before the layer is rendered that puts `"minecraft:main"` on a persistent target called `"base"`

Then, after the layer is rendered, `"base"` is mixed with `"minecraft:main"` and puts on `"0"` (this is after layer render, so `"minecraft:main"` now contains the altered image), then there's another pass in the same list to put it back onto `"minecraft:main"`

The `"base"` target needs to be [persistent](AddingShaders.md#luminance-specific-target-stuff), or it won't get carried across the different lists

The pass lists you can use are:
- `"souper_secret_settings:before_layer_render"`
- `"souper_secret_settings:after_layer_render"`
- `"souper_secret_settings:before_shader_render"`
- `"souper_secret_settings:after_shader_render"`

There are also some [dynamic uniforms](AddingShaders.md#dynamic-uniforms) that can help with making fun effects:
- `soup_shader_index` - index of the shader being rendered (or about to be rendered in the case of `before_shader_render`)
- `soup_layer_size` - size of the layer currently being rendered