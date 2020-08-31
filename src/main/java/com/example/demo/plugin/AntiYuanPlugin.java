package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AntiYuanPlugin extends SuperPlugin {
    static String[] ansPool = {"源佬...又开始了", "源佬, 差不多得了...", "这都多少句了...", "再这样下去机器人都要疯了..."};

    public AntiYuanPlugin() {
        plugin_name = "AntiYuanPlugin";
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
        if (!is_enabled)
            return MESSAGE_IGNORE;
        long user_id = event.getUserId();
        if (event.getUserId() == 1783861062L) {
            Boolean flag = true;
            for (int i = 0; i < event.getMessage().length(); i++) {
                if ((event.getMessage().charAt(i) != '哇') && (event.getMessage().charAt(i) != '啊')) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                StringBuilder ans = new StringBuilder();
                ans.append(CQCode.at(event.getUserId())).append(" ").append(ansPool[new Random().nextInt(ansPool.length)]).append("\n");
                cq.sendPrivateMsg(user_id, ans.toString(), false);
                return MESSAGE_BLOCK;
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
        if (!is_enabled)
            return MESSAGE_IGNORE;
        if (event.getUserId() == 1175468238L) {
            Boolean flag = true;
            for (int i = 0; i < event.getMessage().length(); i++) {
                if ((event.getMessage().charAt(i) != '哇') && (event.getMessage().charAt(i) != '啊')) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                StringBuilder ans = new StringBuilder();
                ans.append(CQCode.at(event.getUserId())).append(" ").append(ansPool[new Random().nextInt(ansPool.length)]).append("\n");
                cq.sendGroupMsg(event.getGroupId(), ans.toString(), false);
                return MESSAGE_BLOCK;
            }

        }
        return MESSAGE_IGNORE;
    }
}
