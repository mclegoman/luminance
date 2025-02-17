import os
import json
import tomllib

configData = {'vertex_shader': "", 'fragment_shader': ""}

def createConfig(id):
    global configData
    if os.path.isfile(id + ".toml"):
        with open(id + ".toml", "rb") as f:
            configData = tomllib.load(f)
    if 'vertex_shader' not in configData:
        configData['vertex_shader'] = ""
    if 'fragment_shader' not in configData:
        configData['fragment_shader'] = ""
    with open(id + ".toml", 'w') as f:
        for key, value in configData.items():
            f.write(f'{key} = "{value}"\n')

def updateProgram(shader_type, shader_name, configData):
    parts = shader_name.split(":")
    partsAmount = len(parts);
    if partsAmount == 1:
        return parse("minecraft", shader_name, shader_type, configData)
    elif partsAmount == 2:
        namespace, path = parts
        return parse(namespace, path, shader_type, configData)
    else:
        print(f"Error: Invalid program '{shader_name}', expected <namespace>:<path>.")
        return None

def parse(namespace, path, shader_type, configData):
    if shader_type == "vertex_shader":
        if configData['vertex_shader'] != "":
            return f"{configData['vertex_shader']}.vsh"
        else:
            return f"{namespace}:{path}.vsh"
    else:
        if configData['fragment_shader'] != "":
            return f"{configData['fragment_shader']}.fsh"
        else:
            return f"{namespace}:{path}.fsh"

def updateShader(file, output_dir, configData):
    with open(file, 'r') as f:
        input = json.load(f)
    output = {}
    output["targets"] = input.get("targets", {})
    output["passes"] = []
    for pass_item in input.get("passes", []):
        pass_copy = pass_item.copy()
        if "program" in pass_copy:
            program = pass_copy.pop("program")
            pass_copy["vertex_shader"] = updateProgram("vertex_shader", program, configData)
            pass_copy["fragment_shader"] = updateProgram("fragment_shader", program, configData)
        output["passes"].append(pass_copy)
    output_file = os.path.join(output_dir, os.path.basename(file))
    with open(output_file, 'w') as f:
        json.dump(output, f, indent=4)

createConfig("config")
dir = os.getcwd()
files = [f for f in os.listdir(dir) if f.endswith('.json')]
output_dir = os.path.join(dir, "output")
os.makedirs(output_dir, exist_ok=True)
for file in files:
    updateShader(os.path.join(dir, file), output_dir, configData)
