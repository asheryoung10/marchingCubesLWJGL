#type vertex
#version 330 core
layout(location = 0) in vec3 aVertex;
layout(location = 1) in vec3 iaPosition;
layout(location = 2) in vec3 iaColor;
layout(location = 3) in vec3 iaScale;
uniform mat4 view;
uniform mat4 projection;
out vec3 fColor;
void main()
{
    fColor = iaColor;
    gl_Position = projection * view * vec4(aVertex * iaScale + iaPosition, 1.0);
}
#type fragment
#version 330 core
in vec3 fColor;
out vec4 FragColor;
void main()
{
    FragColor = vec4(fColor, 1);
}





