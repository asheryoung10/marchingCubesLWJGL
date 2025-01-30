#type vertex
#version 330 core
layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 aNorm;
layout(location = 2) in vec3 aColor;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
out vec3 fNorm;
out vec3 fColor;
out vec3 fragPos;
void main()
{
    fNorm = aNorm;
    fColor = aColor;
    fragPos = vec3(view * model * vec4(aPosition, 1.0));
    gl_Position = projection * view * model * vec4(aPosition, 1.0);
}
#type fragment
#version 330 core
in vec3 fNorm;
in vec3 fColor;
in vec3 fragPos;
out vec4 FragColor;
//uniform vec3 camPos;
void main()
{

    vec3 lightPos = vec3(10,10,10);
    vec3 lightDir = lightPos - fragPos;
    FragColor = vec4(normalize(abs(fNorm)), 1);
}


