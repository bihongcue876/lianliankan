# 欢乐连连看

一个基于 Java Swing 开发的连连看游戏。

## 游戏特色

- 三种游戏模式：普通模式、休闲模式、关卡模式
- 多主题支持：水果、CXK、MH
- 背景音乐和音效
- 排行榜系统
- 存档功能

## 运行要求

- Java 8 或更高版本
- 屏幕分辨率 800×600 或更高

## 编译运行

```bash
javac -d bin -sourcepath src -cp lib/jlayer-1.0.1.jar src/com/lianliankan/MainApp.java
java -cp bin;lib/jlayer-1.0.1.jar com.lianliankan.MainApp
```

或者在 IDE 中直接运行 `src/com/lianliankan/MainApp.java`。

## 项目结构

```
src/
├── com/lianliankan/
│   ├── audio/          # 音频管理
│   ├── back/           # 游戏逻辑
│   ├── model/          # 数据模型
│   ├── ui/             # 界面组件
│   ├── util/           # 工具类
│   └── MainApp.java    # 程序入口
└── resource/           # 资源文件
    ├── audio/          # 音频
    ├── config/         # 配置
    ├── picture/        # 图片
    └── source/         # 其他资源
```

## 依赖

- JLayer 1.0.1 (MP3播放库)

## 许可证

本项目仅供学习交流使用。
