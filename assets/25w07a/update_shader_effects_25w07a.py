import os
import json
import tomllib

configData = {}

def createConfig(id, dir):
    global configData
    if os.path.isfile(id + ".toml"):
        with open(id + ".toml", "rb") as f:
            configData = tomllib.load(f)
    else:
        print(f'couldnt find config file "{id}.toml", creating...')
    
    changed = False
    if 'root_directory' not in configData:
        configData['root_directory'] = dir
        changed = True
    if 'post_effect_directories' not in configData:
        configData['post_effect_directories'] = ["minecraft\\post_effect"]
        changed = True
    if 'shader_directories' not in configData:
        configData['shader_directories'] = ["minecraft\\shaders"]
        changed = True

    if changed:
        with open(id + ".toml", 'w') as f:
            for key, value in configData.items():
                if (type(value) == type([])):
                    s = "\",\n    \"".join(value).replace("\\","/")
                    f.write(f'{key} = [\n    "{s}"\n]\n\n')
                else:
                    s = value.replace("\\","/")
                    f.write(f'{key} = "{s}"\n')
        print("toml overwritten due to missing data")

    root = configData['root_directory']
    configData['post_effect_directories'] = [d if ":" in d else os.path.join(root, d) for d in configData['post_effect_directories']]
    configData['shader_directories'] = [d if ":" in d else os.path.join(root, d) for d in configData['shader_directories']]


def errorProgram(shader_type, shader_name):
    parts = shader_name.split(":")
    partsAmount = len(parts)
    if partsAmount == 1:
        return "minecraft:"+shader_name+(".fsh" if shader_type == "fragment_shader" else ".vsh")
    elif partsAmount == 2:
        namespace, path = parts
        return namespace+":"+path+(".fsh" if shader_type == "fragment_shader" else ".vsh")
    else:
        print(f"Error: Invalid program '{shader_name}', expected <namespace>:<path>.")
        return None

def searchForProgram(program, configData):
    for directory in configData["shader_directories"]:
        id = program.split(":")
        if (id[0] == os.path.basename(os.path.dirname(directory))):
            programFile = os.path.join(directory, id[1]+".json")
            if os.path.isfile(programFile):
                with open(programFile) as f:
                    return json.load(f)
    return None

def updateUniform(uniform):
    uniform.pop("count")
    return uniform

def includeUniform(uniform):
    name = uniform["name"]
    if name == "ProjMat":
        return False
    if (name == "InSize" or name == "OutSize") and uniform["values"] == [ 1.0, 1.0 ]:
        return False
    return True

def updateShader(file, output_dir, configData):
    print("updating "+file)
    with open(file, 'r') as f:
        input = json.load(f)
    output = {}
    output["targets"] = input.get("targets", {})
    output["passes"] = []
    errors = 0
    for pass_item in input.get("passes", []):
        pass_copy = pass_item.copy()
        if "program" in pass_copy:
            programName = pass_copy.pop("program")
            program = searchForProgram(programName, configData)

            # if it cant load the proper programs, use a somewhat reasonable error value
            if program == None:
                print("    !!! couldnt find program "+programName+" - add the path its in to the config")
                errors += 1
                pass_copy["vertex_shader"] = errorProgram("vertex_shader", programName)
                pass_copy["fragment_shader"] = errorProgram("fragment_shader", programName)
                continue

            # forces shader to top of dict
            pass_copy = {"vertex_shader": program["vertex"], "fragment_shader": program["fragment"]} | pass_copy

            # copy over uniforms
            pass_copy["uniforms"] = [updateUniform(uniform) for uniform in program["uniforms"] if includeUniform(uniform)]
            if "uniforms" in pass_item:
                for existing in pass_item["uniforms"]:
                    for uniform in pass_copy["uniforms"]:
                        if (existing["name"] == uniform["name"]):
                            # the post effect uniforms used to be able to be shorter than the definition uniforms
                            for x in range(len(existing["values"])):
                                uniform["values"][x] = existing["values"][x]

        output["passes"].append(pass_copy)
    output_file = os.path.join(output_dir, os.path.basename(file))
    with open(output_file, 'w') as f:
        json.dump(output, f, indent=4)
    return errors

dir = os.getcwd()
createConfig("config", dir)

errors = 0
for post_directory in configData["post_effect_directories"]:
    if (not os.path.isdir(post_directory)):
        print(f'folder "{post_directory}" does not exist')
        continue

    files = [f for f in os.listdir(post_directory) if f.endswith('.json')]
    if (len(files) == 0):
        print(f'no json files found in folder "{post_directory}"')
        continue

    output_dir = os.path.join(post_directory, "output")
    os.makedirs(output_dir, exist_ok=True)
    for file in files:
        try:
            errors += updateShader(os.path.join(post_directory, file), output_dir, configData)
        except Exception as e:
            print("    !!! error occured:\n"+e)
            errors += 1

print(f"finished updating with {errors} errors")
input("press ENTER to close...")