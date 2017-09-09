#version 330

in vec3 position;
in vec3 normal;
in vec3 color;

out vec3 pass_normal;
out vec3 pass_color;
out vec3 pass_location;

uniform mat4 m_matrix;
uniform mat4 mvp_matrix;
uniform vec3 camera_location;

void main(){
    pass_normal = normal;
    pass_color = color;
    pass_location = (m_matrix*vec4(position, 1.0)).xyz;
    gl_Position = mvp_matrix*vec4(position, 0.0);
}