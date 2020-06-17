#version 120

attribute vec3 vertex;
attribute vec2 texCoords;
attribute vec3 normal;

uniform mat4 m;
uniform mat4 mvp;

varying vec2 textureCoords;
varying vec3 fragNormal;
varying vec3 fragPos;

void main(){
    gl_Position=mvp*vec4(vertex,1);
    textureCoords=texCoords;
    fragNormal=(m*vec4(normal,0)).xyz;
    fragPos=(m*vec4(vertex,0)).xyz;
}