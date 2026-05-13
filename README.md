# 欢乐连连看

一个基于 Java Swing 开发的连连看游戏。

## 游戏特色

- 三种游戏模式：普通模式、休闲模式、关卡模式
- 多主题支持：水果、CXK、MH
- 背景音乐和音效（独立音量控制）
- 排行榜系统
- 存档功能
- 游戏内设置（调整时自动暂停）

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
│   │   ├── AudioManager.java           # 音频管理器
│   │   └── VolumeControlledPlayer.java # MP3播放器（支持音量控制）
│   ├── back/           # 游戏逻辑
│   ├── model/          # 数据模型
│   ├── ui/             # 界面组件
│   ├── util/           # 工具类
│   └── MainApp.java    # 程序入口
└── resource/           # 资源文件
    ├── audio/          # 音频
    ├── config/         # 配置
    ├── picture/        # 图片
    ├── source/         # 其他资源
    └── help/           # 帮助图片
```

## 游戏模式

### 普通模式
- 限时模式，共5关
- 可选择难度（Normal/Hard）
- 连击系统：2秒内连续消除触发连击

### 休闲模式
- 不限时
- 可选择主题和棋盘大小
- 额外主题分数 ×2.5

### 关卡模式
- 每个关卡5个阶段
- 棋子数固定为8种
- 不同关卡有不同主题

## 依赖

- JLayer 1.0.1 (MP3播放库)

## 更新日志

### v0.3.1
- 音量控制分离：背景音乐和音效独立控制
- 游戏内设置：调整音量时自动暂停计时
- MP3音量控制：支持实时音量调整
- 存档模式验证：防止串模式
- 继续游戏优化：只显示有存档的模式
- 普通模式界面：标题改为"阶段 X - 普通模式"
- 帮助对话框：连线规则图示改为一行两个图片布局

## 许可证

本项目仅供学习交流使用。
