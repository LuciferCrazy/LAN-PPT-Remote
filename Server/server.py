import socket
import pyautogui
import os

# --- 配置区域 ---
# 监听所有网络接口，这样手机开热点也能连
HOST = '0.0.0.0'
# 端口号，必须和 Android App 里写的一样
PORT = 9999


def start_server():
    # 打印一些欢迎信息，让你知道程序在运行
    print("=" * 40)
    print(f"   PPT 遥控器服务端已启动")
    print(f"   正在监听端口: {PORT}")
    print("=" * 40)
    print(" [提示] 请确保手机和电脑在同一 WiFi 下")
    print(" [提示] 如果是 Windows 11，请留意防火墙弹窗，务必点'允许'")
    print("-" * 40)

    # 创建 UDP Socket (使用 UDP 协议，速度快，无连接)
    # AF_INET = IPv4, SOCK_DGRAM = UDP
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
        try:
            s.bind((HOST, PORT))
        except OSError as e:
            print(f"[错误] 端口 {PORT} 被占用或无法绑定。")
            print(f"详细错误: {e}")
            input("按回车键退出...")
            return

        while True:
            try:
                # 接收数据：每次最多接收 1024 字节
                # data 是接收到的二进制数据，addr 是发送者的 (IP, 端口)
                data, addr = s.recvfrom(1024)

                # 解码命令
                command = data.decode('utf-8').strip()

                # 打印日志，方便你调试
                print(f"[收到指令] {command}  <-- 来自: {addr[0]}")

                # --- 核心控制逻辑 ---
                if command == "vol_up":
                    # 模拟按下 PageUp 键 (PPT 上一页)
                    pyautogui.press('pageup')
                    print("   -> 执行: 上一页 (PageUp)")

                elif command == "vol_down":
                    # 模拟按下 PageDown 键 (PPT 下一页)
                    pyautogui.press('pagedown')
                    print("   -> 执行: 下一页 (PageDown)")

                # 你可以在这里扩展更多命令，比如 'black_screen' -> 'b'

            except Exception as e:
                print(f"[异常] {e}")


if __name__ == "__main__":
    # 为了防止打包成 exe 后双击一闪而过，加个 try-except
    try:
        start_server()
    except KeyboardInterrupt:
        print("\n程序已停止。")