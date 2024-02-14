#type vertex
#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColour;
layout(location=2) in vec2 aTexCoords;
layout(location=3) in float aTexID;

uniform mat4 uEffectsProjection;
uniform mat4 uEffectsView;

out vec4 fColour;
out vec2 fTexCoords;
out float fTexID;

void main(){
    fColour = aColour;
    fTexCoords = aTexCoords;
    fTexID = aTexID;

    gl_Position = uEffectsProjection * uEffectsView * vec4(aPos,1.0);
}

#type fragment
#version 330 core

in vec4 fColour;
in vec2 fTexCoords;
in float fTexID;

uniform sampler2D uEffectsTextures[8];

out vec4 colour;

const float offset = 1.0 / 300.0;
void main(){
    /*
    //Kernel effects
    vec2 offsets[9] = vec2[](
    vec2(-offset, offset), // top-left
    vec2(0.0f, offset), // top-center
    vec2(offset, offset), // top-right
    vec2(-offset, 0.0f), // center-left
    vec2(0.0f, 0.0f), // center-center
    vec2(offset, 0.0f), // center-right
    vec2(-offset, -offset), // bottom-left
    vec2(0.0f, -offset), // bottom-center
    vec2(offset, -offset)// bottom-right
    );

    float kernel[9] = float[](
    1.0 / 16, 2.0 / 16, 1.0 / 16,
    2.0 / 16, 4.0 / 16, 2.0 / 16,
    1.0 / 16, 2.0 / 16, 1.0 / 16
    );

    vec3 sampleTex[9];

    for (int i = 0; i < 9; i++)
    { sampleTex[i] = vec3(fColour * texture(uEffectsTextures[1], fTexCoords + offsets[i])); }

    vec3 col = vec3(0.0);
    for (int i = 0; i < 9; i++){
        col += sampleTex[i] * kernel[i];
    }
    colour = vec4(col,1.0);*/

    /*
    //INVERT
    vec4 col = vec4(0.0);
    col = 1 - fColour * texture(uEffectsTextures[1], fTexCoords);
    col.w = 1;
    colour = col;*/


    //GRAYSCALE
    vec4 col = vec4(0.0);
    col = fColour * texture(uEffectsTextures[1], fTexCoords);
    float avg = (col.r + col.g + col.b)/3.0;
    colour = vec4(avg, avg, avg, 1.0);
}