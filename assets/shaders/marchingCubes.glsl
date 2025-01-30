#type vertex
#version 330 core
layout(location = 0) in vec3 aPosition;
uniform mat4 view;
uniform mat4 projection;
out vec3 color;
void main()
{

    gl_Position = projection * view * vec4(aPosition, 1.0);

    color = vec3(0, 0, 1/(gl_Position.z*0.5f + 0.5f));
}
#type fragment
#version 330 core
in vec3 color;
out vec4 FragColor;
void main()
{
    FragColor = vec4(color, 1);
}


