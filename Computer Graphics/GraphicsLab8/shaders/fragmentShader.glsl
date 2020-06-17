#version 120

uniform sampler2D sampler;
uniform sampler2D basementSampler;
uniform int isBasement;
uniform int isTextured;

struct Light{
    vec3 ambient,diffuse,specular;
    vec3 position;
    vec3 attenuation;
};

struct Material{
    vec3 ambient,diffuse,specular;
    int shininess;
};

uniform Light light;
uniform Material material;

varying vec2 textureCoords;
varying vec3 fragNormal;
varying vec3 fragPos;

void main(){
    if (isTextured==1){
        if (isBasement==0){
            gl_FragColor=texture2D(sampler,textureCoords);
        }else{
            gl_FragColor=texture2D(basementSampler,textureCoords);
        }
    }else{
        vec3 normal=normalize(fragNormal);
        vec3 lightDir=normalize(light.position-fragPos);
        vec3 viewDir=normalize(-fragPos);
        vec3 reflectDir=normalize(-reflect(lightDir,normal));

        vec3 amb=light.ambient*material.ambient;

        float diffuseAngle=max(dot(normal,lightDir),0);
        vec3 diff=light.diffuse*material.diffuse*diffuseAngle;
        diff=clamp(diff,0,1);

        float specAngle=max(dot(reflectDir,viewDir),0);
        vec3 spec=light.specular*material.specular*pow(specAngle,material.shininess/4.);
        spec=clamp(spec,0,1);

        float d=length(light.position-fragPos);
        float attenuation;

        attenuation=1/(light.attenuation.x+light.attenuation.y*d+light.attenuation.z*d*d);


        vec3 res=amb+attenuation*(diff+spec);

        gl_FragColor=vec4(res,1);
    }
}