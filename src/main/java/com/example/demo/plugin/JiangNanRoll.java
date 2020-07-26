package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class JiangNanRoll extends SuperPlugin {

    static String personListFileAddr = "data/jiangnan_list/pool.txt";
    List<Persons> personsList = new ArrayList<>();
    RandomResultGenerator randomResultGenerator;
    String helpInfo = "江南百景图 - 画池模拟插件\n格式:\n  /jn [Amount]\n其中:\n" +
            "  /jn 插件名, 等价于/jiangnan或者/江南\n  [Amount]抽卡数量\n" +
            "注意掉率0.3%的物品为\"物(0.3%)\"级, 掉率0.5%的物品为\"物(0.5%)\"级" +
            "本插件由云玩家Horizon瞎写, 有问题找他就好" +
            "祝欧🍻";

    class Persons {
        String name;
        Category category;
        double startPoint = 0.0, endPoint = 0.0;

        Persons(String name, String category) {
            this.name = name;
            this.category = Category.valueOf(category);
        }
    }

    enum Category {
        tian, hou, qing, wuI, wuII;

        static double getPercentage(Category category) {
            if (category == tian)
                return 0.003;
            if (category == hou)
                return 0.017;
            if (category == qing)
                return 0.07;
            if (category == wuI)
                return 0.003;
            if (category == wuII)
                return 0.005;
            return -1.0;
        }
    }

    class RandomResultGenerator {
        List<Persons> personsList;
        double upperBound;
        Random rng = new Random();

        RandomResultGenerator(List<Persons> inPersonsList) {
            this.personsList = inPersonsList;
            if (inPersonsList.size() == 0) {
                upperBound = 0.0;
                return;
            }

            this.personsList.get(0).endPoint = Category.getPercentage(inPersonsList.get(0).category);
            for (int i = 1; i < personsList.size(); i++) {
                Persons currPersons = personsList.get(i);
                currPersons.startPoint = personsList.get(i - 1).endPoint;
                currPersons.endPoint = currPersons.startPoint + Category.getPercentage(currPersons.category);
            }
            upperBound = personsList.get(personsList.size() - 1).endPoint;
        }

        Persons nextPerson() {
            double rngRes;
            do {
                rngRes = rng.nextDouble();
            } while (rngRes >= upperBound);

            for (Persons currPersons : personsList) {
                if (rngRes >= currPersons.startPoint && rngRes < currPersons.endPoint)
                    return currPersons;
            }
            return null;
        }
    }

    JiangNanRoll() {
        plugin_name = "JiangNanRoll";
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(new File(personListFileAddr));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String currLine = bufferedReader.readLine();
            String[] lineParsed;
            while (currLine != null) {
                lineParsed = currLine.split(" ");
                personsList.add(new Persons(lineParsed[0], lineParsed[1]));
                currLine = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(personsList.size() + " Persons Loaded from File: " + personListFileAddr);
        randomResultGenerator = new RandomResultGenerator(personsList);
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event) {
        if (!is_enabled)
            return MESSAGE_IGNORE;
        long user_id = event.getUserId();
        cq.sendPrivateMsg(user_id, "This function is only accessible from Group Talk...", false);
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
        if (!is_enabled)
            return MESSAGE_IGNORE;
        String msg = event.getMessage();
        if (msg.length() < 3)
            return MESSAGE_IGNORE;

        String[] msgs = msg.split(" ");
        if (!msgs[0].toLowerCase().equals("/jiangnan")
                && !msgs[0].toLowerCase().equals("/jn")
                && !msgs[0].equals("/江南"))
            return MESSAGE_IGNORE;
        if (msgs.length < 2) {
            cq.sendGroupMsg(event.getGroupId(), helpInfo, false);
            return MESSAGE_BLOCK;
        }

        StringBuilder returnMsg = new StringBuilder();
        returnMsg.append(CQCode.at(event.getUserId())).append(" ");
        Map<String, Integer> multiRes = new HashMap<>();
        int ssr = 0, sr = 0, r = 0, obj_higher = 0, obj_lower = 0;
        Persons currPerson;

        int rolls;
        try {
            rolls = Integer.parseInt(msgs[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            returnMsg.append("\nAmount参数无效(应为正整数, 但你输入的是").append(msgs[1]).append(")");
            cq.sendGroupMsg(event.getGroupId(), returnMsg.toString(), false);
            return MESSAGE_BLOCK;
        }

        returnMsg.append("\n").append(rolls).append("次绘画结果如下\n");

        if (rolls == 1) {
            currPerson = randomResultGenerator.nextPerson();
            returnMsg.append("你获得了 ").append(currPerson.category).append(" ").append(currPerson.name);
            cq.sendGroupMsg(event.getGroupId(), returnMsg.toString(), false);
            returnMsg = new StringBuilder();
            switch (currPerson.category) {
                case qing:
                    returnMsg.append("Better Luck Next Time😂");
                    break;
                case hou:
                    returnMsg.append("手气不错(赞赏)");
                    break;
                case wuI:
                case tian:
                    returnMsg.append("啊这...欧皇!(非提+云玩家の羡慕)");
                    break;
                case wuII:
                    returnMsg.append("0.5%...你也是欧皇!(非提+云玩家の羡慕)");
                    break;
            }
            cq.sendGroupMsg(event.getGroupId(), returnMsg.toString(), false);
            return MESSAGE_BLOCK;
        }

        for (int i = 0; i < rolls; i++) {
            currPerson = randomResultGenerator.nextPerson();
            switch (currPerson.category) {
                case tian:
                    ssr++;
                    break;
                case hou:
                    sr++;
                    break;
                case qing:
                    r++;
                    break;
                case wuI:
                    obj_higher++;
                    break;
                case wuII:
                    obj_lower++;
                    break;
            }
            if (multiRes.containsKey(currPerson.name)) {
                Integer val = multiRes.get(currPerson.name);
                val += 1;
                multiRes.put(currPerson.name, val);
            } else {
                multiRes.put(currPerson.name, 1);
            }
        }
        for (String currName : multiRes.keySet()) {
            returnMsg.append(currName).append("  ").append(multiRes.get(currName)).append("次\n");
        }
        returnMsg.append("-----------\n天 总计 ").append(ssr).append("\n侯 总计 ").append(sr)
                .append("\n卿 总计 ").append(r).append("\n物(0.3%) 总计 ").append(obj_higher)
                .append("\n物(0.5%) 总计 ").append(obj_lower);
        returnMsg.append("\n祝欧🍻");
        cq.sendGroupMsg(event.getGroupId(), returnMsg.toString(), false);
        return MESSAGE_BLOCK;
    }
}
