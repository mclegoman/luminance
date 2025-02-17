import os
import json

def updateProgram(shader_type, shader_name):
    parts = shader_name.split(":")
    partsAmount = len(parts);
    if partsAmount == 1:
        return parse("minecraft", shader_name, shader_type)
    elif partsAmount == 2:
        namespace, path = parts
        return parse(namespace, path, shader_type)
    else:
        print(f"Error: Invalid program '{shader_name}', expected <namespace>:<path>.")
        return None

def parse(namespace, path, shader_type):
    extension = ".vsh" if shader_type == "vertex_shader" else ".fsh"
    return f"{namespace}:{path}{extension}"

def updateShader(file, output_dir):
    with open(file, 'r') as f:
        input = json.load(f)
    output = {}
    output["targets"] = input.get("targets", {})
    output["passes"] = []
    for pass_item in input.get("passes", []):
        pass_copy = pass_item.copy()
        if "program" in pass_copy:
            program = pass_copy.pop("program")
            pass_copy["vertex_shader"] = updateProgram("vertex_shader", program)
            pass_copy["fragment_shader"] = updateProgram("fragment_shader", program)
        output["passes"].append(pass_copy)
    output_file = os.path.join(output_dir, os.path.basename(file))
    with open(output_file, 'w') as f:
        json.dump(output, f, indent=4)

dir = os.getcwd()
files = [f for f in os.listdir(dir) if f.endswith('.json')]
output_dir = os.path.join(dir, "output")
os.makedirs(output_dir, exist_ok=True)
for file in files:
    updateShader(os.path.join(dir, file), output_dir)
