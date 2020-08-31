package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Component
public class HitokotoPlugin extends SuperPlugin {
    String request_url = "https://v1.hitokoto.cn/?c=";
    Random randomSuffix = new Random();


    public HitokotoPlugin() {
        plugin_name = "HitokotoPlugin";
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
        return MESSAGE_IGNORE;
    }


    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        if (msg.length() == 9 && (msg.equals("/hitokoto") || msg.equals("/Hitokoto"))) {
            try {
                URL url = new URL(request_url + "?c=" + (char) (randomSuffix.nextInt(12) + 'a'));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                JSONObject jsonObject = new JSONObject(new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8)).readLine());
                String hitokoto_msg = jsonObject.getString("hitokoto") + "\n—— " + jsonObject.getString("from");

                cq.sendGroupMsg(groupId, hitokoto_msg, false);

                httpURLConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }

        if (msg.length() > 9 && (msg.startsWith("/hitokoto") || msg.startsWith("/Hitokoto"))) {
            String[] str = msg.split(" ");
            if (str.length < 2) {
                cq.sendGroupMsg(groupId, "用法：/hitokoto {参数}\n参数为可选选项，多余的参数会被忽略", false);
                return MESSAGE_BLOCK;
            }
            String type = str[1];
            try {
                URL url = new URL(request_url + type);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                JSONObject jsonObject = new JSONObject(new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8)).readLine());
                String hitokoto_msg = jsonObject.getString("hitokoto") + "\n—— " + jsonObject.getString("from");

                cq.sendGroupMsg(groupId, hitokoto_msg, false);

                httpURLConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }

        return MESSAGE_IGNORE;
    }
}
