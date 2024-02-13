#type vertex
#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColour;
layout(location=2) in vec2 aTexCoords;
layout(location=3) in float aTexID;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColour;
out vec2 fTexCoords;
out float fTexID;

void main(){
    fColour = aColour;
    fTexCoords = aTexCoords;
    fTexID = aTexID;

    gl_Position = uProjection * uView * vec4(aPos,1.0);
}

#type fragment
#version 330 core

in vec4 fColour;
in vec2 fTexCoords;
in float fTexID;

uniform sampler2D uTextures[8];

out vec4 colour;

void main(){
    vec4 texColour;
    if(fTexID > 0){
        switch (int(fTexID)) {
            case 0:
                texColour = fColour;
                break;
            case 1:
                texColour = fColour * texture(uTextures[1], fTexCoords);
                break;
            case 2:
                texColour = fColour * texture(uTextures[2], fTexCoords);
                break;
            case 3:
                texColour = fColour * texture(uTextures[3], fTexCoords);
                break;
            case 4:
                texColour = fColour * texture(uTextures[4], fTexCoords);
                break;
            case 5:
                texColour = fColour * texture(uTextures[5], fTexCoords);
                break;
            case 6:
                texColour = fColour * texture(uTextures[6], fTexCoords);
                break;
            case 7:
                texColour = fColour * texture(uTextures[7], fTexCoords);
                break;
        }
        colour = texColour;
    }else{
        colour = fColour;
    }

}