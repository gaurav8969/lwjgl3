#type vertex
#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColour;
layout (location=2) in vec2 aTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColour;
out vec2 fTexCoords;

void main(){
    fColour = aColour;
    gl_Position = uProjection * uView * vec4(aPos,1.0);
    fTexCoords = aTexCoords;
}

#type fragment
#version 330 core

uniform float uTime;
uniform sampler2D TEX_SAMPLER;

in vec4 fColour;
in vec2 fTexCoords;

out vec4 colour;

void main(){
    colour = texture(TEX_SAMPLER,fTexCoords);
}