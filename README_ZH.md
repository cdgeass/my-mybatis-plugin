# my-mybatis plugin

![License](https://img.shields.io/github/license/cdgeass/my-mybatis-plugin)
![Build](https://img.shields.io/github/workflow/status/cdgeass/my-mybatis-plugin/Intellij%20Idea%20plugin%20CI%20with%20Gradle)
![IDEA](https://img.shields.io/badge/dynamic/json?label=IDEA&query=%24%5B0%5D.compatibleVersions.IDEA&url=https%3A%2F%2Fplugins.jetbrains.com%2Fapi%2Fplugins%2F14297%2Fupdates%3Fchannel%3D%26size%3D8)
![Version](https://img.shields.io/jetbrains/plugin/v/14297)
![Downloads](https://img.shields.io/jetbrains/plugin/d/14297)

<!-- Plugin description -->
这是一个帮助你在 IDEA 中使用 MyBatis 的插件。提供了多种功能来帮助你编写 xml 以及进行调试:
1. 格式化 MyBatis 日志
2. 代码跳转
3. 代码补全
<!-- Plugin description end -->

## 功能

- 格式化 MyBatis 日志
    - 在控制台中  
      ![](gif/console_format.gif)  
    - 在 tool window 中  
      ![](gif/toolwindow-format.gif)

- 代码跳转
    - 在代码和 xml tag 间跳转 
    ![](gif/code_jump_java.gif)
    - 在 xml 中跳转 
    ![](gif/code_jump_xml_tag.gif)
    
- 代码补全 
![](gif/completion.gif)

## Installation
- Using IDE built-in plugin system on Windows:
    - <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "my-mybatis"</kbd> > <kbd>Install Plugin</kbd>
- Using IDE built-in plugin system on MacOs:
  - <kbd>Preferences</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "my-mybatis"</kbd> > <kbd>Install Plugin</kbd>
- Manually:
  - Download the [latest release](https://github.com/cdgeass/my-mybatis-plugin/releases/latest) and install it manually using <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>

## License

[GPL © cdgeass.](LICENSE)
