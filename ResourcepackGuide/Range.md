# Range Config

This is specifically to explain `range` configs for uniform overrides, see [Adding Shaders](AddingShaders.md#dynamic-uniforms) for an overview on how uniform config works

```json
(example of how to use range for reference)

"config": [
  {"name": "0_range", "values": [ 0.0, 1.0 ]},
]
```

`range` remaps the value of a uniform, for instance, if you wanted to change a uniform value from `10` to `5` when the player starts sprinting, you could use `luminance_isSprinting` with a range of `[ 10, 5 ]`

whatever addition/multiplication you do to the `[ 0, 1 ]` will apply to the value, so if you want a uniform to be double what it usually is, it would be `[ 0, 2 ]`, or if you wanted it to be 2 less than it usually is, `[ -2, -1 ]` and half that would be `[ -1, -0.5 ]`

## Remapping Logic

for most uniforms, the first value in the `range` list is the value that will be used when the uniform is `0`, and the second when its `1`, using the average of the two at `0.5`, and so on

if a uniform has a range that isnt `0-1`, for instance `luminance_currentHealth` which is `0-20`, then it will *first* remap the value to `0-1`, then remap it to the `range`, so to double the value of `luminance_currentHealth` youd use `[ 0, 40 ]` instead

this does not happen if a uniform has an unbounded range (for instance `luminance_pos` which is completely unbounded, or `luminance_velocity` which has a range `0-...`), in those cases it just remaps the values `0-1` and extrapolates - so if you're at x=100 and the range is `[ 0, 0.1 ]`, the value used will be `10`

the [equation](https://en.wikipedia.org/wiki/Linear_interpolation#Programming_language_support) used for a range `[ a, b ]` is `a + ((b - a) x value)`, where the value is either directly from the uniform, or was first remapped to `0-1` if it could be

## Changes to Ranges

`luminance_currentHealth` actually *doesnt* have a range `0-20`, it has a range `0-maxHealth`, which can change. this means if youre using a range `[ 0, 1 ]`, then the health value that `1` represents *could* be something other than 20

if you want to use the current min or max value, you can use `null` in the config, so a `range` of `[ null, null ]` would be the same as having no `range` (this is what the default value of `range` is!)

you can mix these, like `[ 10, null ]`, i'm not entirely sure what situation that would be useful in, but its nice to have the option

## Overrides Only

as per the note in [Adding Shaders](AddingShaders.md#dynamic-uniforms), the `range` config is defined by the code in charge of the `"override"`, not the uniforms themselves (like `period` for `"luminance_time"` is), this means you can only use `range` if its part of an override, like this:

```json
will work:
{
  "name": "luminance_time",
  "values": [],
  "override": [ "luminance_time" ]
  "config": [
    {
      "name": "0_range",
      "values": [ 1, -1 ]
    }
  ]
}

wont work:
{
  "name": "luminance_time",
  "values": [ 1.0 ],
  "config": [
    {
      "name": "0_range",
      "values": [ 1, -1 ]
    }
  ]
}
```