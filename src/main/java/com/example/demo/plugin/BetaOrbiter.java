package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;

@Component
public class BetaOrbiter extends SuperPlugin {
    String request_url = "https://api.too.pub/?encode=text";
    static String[] weather = new String[]{"大雨", "暴雨", "中雨", "小雨", "雷阵雨⛈", "阴天☁", "冰雹"};

    public BetaOrbiter() {
        plugin_name = "BetaOrbiter";
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
        long userId = event.getUserId();
        String msg = event.getMessage();

        return MESSAGE_IGNORE;
    }


    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        if (msg.toLowerCase().equals("/betaorbiter") || msg.toLowerCase().equals("/bo") ||
                        msg.toLowerCase().equals("/tg") || msg.toLowerCase().equals("/舔狗")) {
            try {
                URL url = new URL(request_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                StringBuilder sendMsg = new StringBuilder();
                sendMsg.append(CQCode.at(event.getUserId())).append("\n");
                sendMsg.append("🔥 ").append("舔🐕日记\n");
                sendMsg.append("📅 ").append(new Date()).append("\n");
                sendMsg.append("☁ ").append(weather[new Random().nextInt(weather.length)]).append("\n");
                sendMsg.append("📃 " + new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8")).readLine());

                cq.sendGroupMsg(groupId, sendMsg.toString(), false);
                httpURLConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }

        if ((msg.toLowerCase().equals("/betaorbiter help") || msg.toLowerCase().equals("/bo help") ||
                msg.toLowerCase().equals("/tg help") || msg.toLowerCase().equals("/舔狗 help"))) {

            cq.sendGroupMsg(groupId, "舔狗日记\n用法：/betaorbitor\n 该命令等价于\n /bo  /tg  /舔狗\n\n关于\"舔狗\"一词的翻译, " +
                    "参见 https://zhuanlan.zhihu.com/p/54398690", false);
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }
}
