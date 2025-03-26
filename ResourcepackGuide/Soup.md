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

This has a [priority of 100](perspective.md#spectator-shaders)