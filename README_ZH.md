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
      ![](https://raw.githubusercontent.com/cdgeass/pictures/main/20210826144131.gif)
    - 在 tool window 中  
      ![](https://raw.githubusercontent.com/cdgeass/pictures/main/20210826145049.gif)

- 代码跳转
    - 在代码和 xml tag 间跳转
      ![](https://raw.githubusercontent.com/cdgeass/pictures/main/20210826145727.gif)
    - 在 xml 中跳转
      ![](https://raw.githubusercontent.com/cdgeass/pictures/main/20210826150434.gif)
    
- 代码补全
  ![](https://raw.githubusercontent.com/cdgeass/pictures/main/20210826152811.gif)

- MyBatis Generator
  ![](https://raw.githubusercontent.com/cdgeass/pictures/main/20210826153821.gif)

## 安装
- 在 Windows 上使用 IDEA 内的插件系统:
    - <kbd>File</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "my-mybatis"</kbd> > <kbd>Install Plugin</kbd>
- 在 MacOs 上使用 IDEA 内的插件系统:
  - <kbd>Preferences</kbd> > <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>Search for "my-mybatis"</kbd> > <kbd>Install Plugin</kbd>
- 手动:
  - 下载 [最新版本](https://github.com/cdgeass/my-mybatis-plugin/releases/latest) 并手动安装 <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>

## License

[GPL © cdgeass.](LICENSE)
