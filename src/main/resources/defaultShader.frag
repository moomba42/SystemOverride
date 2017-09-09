#version 330

in vec3 pass_normal;
in vec3 pass_color;
in vec3 pass_location;

out vec4 out_color;

uniform vec3 light_dir = normalize(vec3(-1.0, -1.0, -1.0));
uniform vec3 camera_location;

void main() {
	vec4 ambient_color = vec4(0.1, 0.0, 0.0, 1.0);
    vec4 diffuse_color = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 specular_color = vec4(0.0, 0.0, 0.0, 0.0);
    float specular_damping = 0.5;

    float diffuse_factor = dot(pass_normal, -light_dir);
    if(diffuse_factor > 0){
        diffuse_color = vec4(1.0, 0.0, 0.0, 1.0) * diffuse_factor;

        vec3 to_camera = normalize(camera_location-pass_location);
        vec3 reflected = normalize(reflect(light_dir, pass_normal));
        float specular_factor = dot(to_camera, reflected);
        if(specular_factor > 0){
            specular_color = vec4(vec3(specular_factor*diffuse_factor*specular_damping), 1.0)*vec4(1.0, 1.0, 1.0, 1.0);
        }
    }

    out_color = ambient_color + diffuse_color + specular_color;
}
