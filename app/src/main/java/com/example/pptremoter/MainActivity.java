package com.example.pptremoter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    // 界面控件变量
    private EditText etIp;
    private Button btnLock;
    private Button btnBlackMode;
    private View viewFakeLock;      // 全黑遮罩层
    private View layoutControl;     // 操作界面容器
    private TextView tvCopyright;   // 底部版权链接

    // 核心数据
    private String targetIp = "";
    private final int targetPort = 9999; // 必须与电脑端 Python 脚本端口一致

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 初始化控件 (确保 XML 里 ID 正确)
        etIp = findViewById(R.id.et_ip);
        btnLock = findViewById(R.id.btn_lock);
        btnBlackMode = findViewById(R.id.btn_black_mode);
        viewFakeLock = findViewById(R.id.view_fake_lock);
        layoutControl = findViewById(R.id.layout_control);
        tvCopyright = findViewById(R.id.tv_copyright);

        // 2. 设置“锁定/重置”按钮逻辑
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 情况 A：当前已锁定 -> 执行重置
                if (!targetIp.isEmpty()) {
                    targetIp = ""; // 清空 IP
                    etIp.setEnabled(true); // 解锁输入框
                    btnLock.setText("锁定 IP 并开始");
                    btnLock.setBackgroundColor(0xFF2196F3); // 变回蓝色
                    btnBlackMode.setEnabled(false); // 禁用黑屏模式
                    Toast.makeText(MainActivity.this, "已重置，请重新输入 IP", Toast.LENGTH_SHORT).show();
                }
                // 情况 B：当前未锁定 -> 执行锁定
                else {
                    String inputIp = etIp.getText().toString().trim();
                    if (inputIp.isEmpty()) {
                        Toast.makeText(MainActivity.this, "IP 不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    targetIp = inputIp; // 保存 IP
                    etIp.setEnabled(false); // 锁定输入框
                    btnLock.setText("已锁定 (点击重置)");
                    btnLock.setBackgroundColor(0xFF4CAF50); // 变绿色
                    btnBlackMode.setEnabled(true); // 启用黑屏模式

                    etIp.clearFocus(); // 收起键盘
                    Toast.makeText(MainActivity.this, "准备就绪！请按音量键测试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3. 设置“进入演示模式”按钮逻辑
        btnBlackMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetIp.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请先锁定 IP！", Toast.LENGTH_SHORT).show();
                    return;
                }
                enterBlackMode();
            }
        });

        // 4. 设置“退出演示模式”逻辑 (点击黑屏任意位置)
        viewFakeLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitBlackMode();
            }
        });

        // 5. 设置“GitHub 跳转”逻辑
        tvCopyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GitHub 仓库地址
                String url = "https://github.com/LuciferCrazy/LAN-PPT-Remote";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                Toast.makeText(MainActivity.this, "正在前往项目主页...", Toast.LENGTH_SHORT).show();
            }
        });

        // 6. 初始状态设置
        btnBlackMode.setEnabled(false); // 默认禁用黑屏按钮

        // 启动时的欢迎/致谢 (可选)
        Toast.makeText(this, "欢迎使用！Powered by Gemini AI", Toast.LENGTH_LONG).show();
    }

    /**
     * 进入全黑沉浸模式 (伪锁屏)
     */
    private void enterBlackMode() {
        viewFakeLock.setVisibility(View.VISIBLE); // 显示黑幕
        layoutControl.setVisibility(View.GONE);   // 隐藏操作界面

        // 隐藏系统状态栏和导航栏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Toast.makeText(this, "进入演示模式，双击或点击屏幕退出", Toast.LENGTH_SHORT).show();
    }

    /**
     * 退出全黑模式
     */
    private void exitBlackMode() {
        viewFakeLock.setVisibility(View.GONE);    // 隐藏黑幕
        layoutControl.setVisibility(View.VISIBLE);// 显示操作界面

        // 恢复系统 UI
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    /**
     * 核心：拦截物理音量键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果没设 IP，不拦截，让系统正常调音量
        if (targetIp.isEmpty()) {
            return super.onKeyDown(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                sendUdpCommand("vol_up"); // 发送上一页指令
                return true; // 返回 true 表示拦截成功

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                sendUdpCommand("vol_down"); // 发送下一页指令
                return true; // 返回 true 表示拦截成功
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 后台发送 UDP 指令
     */
    private void sendUdpCommand(String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    byte[] data = command.getBytes();
                    InetAddress address = InetAddress.getByName(targetIp);
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, targetPort);
                    socket.send(packet);
                    socket.close();
                    System.out.println("发送成功: " + command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
