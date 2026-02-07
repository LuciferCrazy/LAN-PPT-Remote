# 📱 LAN PPT Remote (局域网 PPT 遥控器)

一个超轻量级的 Android PPT 翻页工具，专为老旧电脑和无外网环境设计。
核心功能：通过局域网 (UDP) 连接，利用手机音量键控制电脑 PPT 翻页。

## ✨ 特点
- **极简设计**：无需复杂的服务器配置，Python 脚本一键启动。
- **省电模式**：支持“伪锁屏”演示模式，黑屏下依然可用音量键翻页。
- **低延迟**：使用 UDP 协议，毫秒级响应。
- **防误触**：支持 IP 锁定和解锁功能。

## 🛠️ 环境要求
- **电脑端**：Windows 10/11 (需安装 Python 或直接运行 exe)
- **手机端**：Android 8.0+
- **网络**：手机和电脑需在同一 WiFi 下（推荐手机开热点）

## 🚀 快速开始

### 1. 电脑端 (服务端)
1. 确保电脑已安装 Python 3.x。
2. 安装依赖：`pip install pyautogui`
3. 运行脚本：`python server.py`
4. (或者直接下载 Releases 中的 `server.exe` 双击运行)

### 2. 手机端
1. 下载并安装 APK。
2. 输入电脑的 IP 地址 (在电脑终端输入 `ipconfig` 查看)。
3. 点击“锁定 IP”。
4. 按音量键 `+` (上一页) 或 `-` (下一页)。

## ⚠️ 注意事项
- 首次运行时，Windows 防火墙可能会弹窗，请务必勾选 **“允许访问”**。
- 端口默认为 `9999`，如需修改请同步更改 Python 脚本和 Android 源码。

## 🙏 Acknowledgments (致谢)
本项目在开发过程中使用了 AI 辅助编程工具。
This project was developed with the assistance of AI tools.
Special thanks to Google Gemini for acting as my pair programmer.

* **Core Logic & UI**: Code structure and implementation assistance provided by **Google Gemini**.
* **Idea & Testing**: Conceived, tested, and deployed by **Lucifer**.

## 📄 开源协议
本项目采用 [MIT License](LICENSE) 开源。
Copyright (c) 2026 Lucifer
