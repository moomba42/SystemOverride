#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec3 color;

out vec3 pass_color;
out vec3 pass_normal;

void main(){
    pass_color = color;
    pass_normal = normal;
    gl_Position = vec4(position, 1.0);
}