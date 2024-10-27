# Luminance Todo  
- **Update to 1.21.3**  
- **Dynamic Uniforms**  
  - Add a way when registering a uniform to specify if said uniform should be clamped, with `uniform.getMin()` and `uniform.getMax()` (they should probably return `Optional<Float>` incase the uniform isn't clamped in which case we'd return `Optional.empty()`).  
  - Add a uniform config.  
    - This would ideally be in the shader program json (and post_effect json) inside the uniform, and would allow the shader to specify data that the dynamic uniform can access.  

> [!NOTE]  
> The following is required to be done before releasing.  
- Fix shader rendering.  
  - Make sure (depth) shader rendering works in both fast/fancy and fabulous graphics.  
  - Add a check for depth shaders, so we can disable them in game (screen) mode.  
  - Make sure player hand is rendered over the shader unless iris is installed and enabled.  