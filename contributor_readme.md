## Mod Developers:  
- "Entity Linked" Shaders under the "perspective" ~~and/or "souper_secret_settings"~~ namespaces should have a mod priority of:  
  ~~1. "souper_secret_settings"~~  
  2. "perspective"  
  3. any other mods.  
  - This means, your mod should check if ~~souper_secret_settings or~~ perspective is installed before rendering.  
    - Perspective will check if soup is installed, and if so will let soup render the shader.  

## Contributors:  
- Please put your username on the attributions in each class you edit/create.  