package com.example.demo.plugin;

import a3lib.SuperPlugin;
import biz.k11i.rng.GaussianRNG;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class AzurLaneRoll extends SuperPlugin {

    class Ships {
        String name;
        Category category;

        Ships(String name, String category) {
            this.name = name;
            this.category = Category.valueOf(category);
        }
    }

    enum Category {
        N, R, SR, SSR;
    }

    List<Ships> light_n = new ArrayList<>();
    List<Ships> light_r = new ArrayList<>();
    List<Ships> light_sr = new ArrayList<>();
    List<Ships> light_ssr = new ArrayList<>();
    List<Ships> heavy_n = new ArrayList<>();
    List<Ships> heavy_r = new ArrayList<>();
    List<Ships> heavy_sr = new ArrayList<>();
    List<Ships> heavy_ssr = new ArrayList<>();
    List<Ships> sp_n = new ArrayList<>();
    List<Ships> sp_r = new ArrayList<>();
    List<Ships> sp_sr = new ArrayList<>();
    List<Ships> sp_ssr = new ArrayList<>();
    List<Ships> timeLimited = new ArrayList<>();
    boolean isExpired = false;
    String helpInfo = "碧蓝航线建造模拟器插件\n格式:\n  /al <Source> [Amount]\n其中:\n" +
            "  /al 插件名, 等价于/azurlane\n  <Source> 池子, 有以下选项:\n" +
            "    L: 轻型池\n    H: 重型池\n    S: 特型池\n    T: 限时建造(还没写完呢)\n" +
            "  [Amount] 连抽次数, 可不填(即为单抽)\n" +
            "需要注意的是, 限时建造不一定在此刻可用. 这时的限时建造将按照上一次的概率进行, 并会给出提示\n" +
            "祝欧🍻\n" +
            "[限时建造还没写完...]";

    public AzurLaneRoll() {
        plugin_name = "AzurLaneRoll";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File("data/ship_list/light.txt"));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = bufferedReader.readLine();
            String[] parsed;
            while(line != null) {
                parsed = line.split(" ");
                if(parsed[1].equals("N"))
                    light_n.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("R"))
                    light_r.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("SR"))
                    light_sr.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("SSR"))
                    light_ssr.add(new Ships(parsed[0], parsed[1]));
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            /*
            line = bufferedReader.readLine();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            isExpired = dateFormat.parse(line).before(new Date());
            while(!line.equals("------")) {
                //timeLimited.add(line);
                line = bufferedReader.readLine();
            }
            */

            fileInputStream = new FileInputStream("data/ship_list/heavy.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            line = bufferedReader.readLine();
            while(line != null) {
                parsed = line.split(" ");
                if(parsed[1].equals("N"))
                    heavy_n.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("R"))
                    heavy_r.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("SR"))
                    heavy_sr.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("SSR"))
                    heavy_ssr.add(new Ships(parsed[0], parsed[1]));
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            fileInputStream = new FileInputStream("data/ship_list/sp.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            line = bufferedReader.readLine();
            while(line != null) {
                parsed = line.split(" ");
                if(parsed[1].equals("N"))
                    sp_n.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("R"))
                    sp_r.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("SR"))
                    sp_sr.add(new Ships(parsed[0], parsed[1]));
                if(parsed[1].equals("SSR"))
                    sp_ssr.add(new Ships(parsed[0], parsed[1]));
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        long user_id = event.getUserId();
        cq.sendPrivateMsg(user_id, "This function is only accessible from Group Talk...", false);
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event) {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        String msg = event.getMessage();
        if(msg.length() < 3)
            return MESSAGE_IGNORE;
        String[] msgs = msg.split(" ");
        if(msgs[0].toLowerCase().equals("/al") == false && msgs[0].toLowerCase().equals("/azurlane") == false)
            return MESSAGE_IGNORE;
        if(msgs.length < 2) {
            cq.sendGroupMsg(event.getGroupId(), helpInfo, false);
            return MESSAGE_BLOCK;
        }

        Random rng = new Random();
        int rand = (int) (GaussianRNG.FAST_RNG.generate(rng)*100);
        Ships result = null;
        Map<String, Integer> multiRes = new HashMap<>();
        int n = 0, r = 0, sr = 0, ssr = 0;
        StringBuilder returnMsg = new StringBuilder();
        returnMsg.append(CQCode.at(event.getUserId())).append(" ");
        if(msgs.length > 2) {
            int rolls = 0;
            try {
                rolls = Integer.parseInt(msgs[2]);
            }catch (NumberFormatException e) {
                e.printStackTrace();
                returnMsg.append("\n第三个参数无效, 将被忽视\n");
            }
            returnMsg.append("\n").append(rolls).append("次建造结果如下\n");
            switch (msgs[1]) {
                case "L":
                    for(int i = 0;i < rolls;i++) {
                        if(rand < 7) {
                            result = (light_ssr.get(rng.nextInt(light_ssr.size())));
                            ssr++;
                        } else if(rand < (7 + 12)) {
                            result = (light_sr.get(rng.nextInt(light_sr.size())));
                            sr++;
                        } else if(rand < (7 + 12 + 26)) {
                            result = (light_r.get(rng.nextInt(light_r.size())));
                            r++;
                        } else {
                            result = (light_n.get(rng.nextInt(light_n.size())));
                            n++;
                        }
                        rand = rng.nextInt(100);
                        if(multiRes.containsKey(result.name)) {
                            Integer val = multiRes.get(result.name);
                            val += 1;
                            multiRes.put(result.name, val);
                        } else {
                            multiRes.put(result.name, 1);
                        }
                    }
                    break;
                case "H":
                    for(int i = 0;i < rolls;i++) {
                        if(rand < 7) {
                            result = (heavy_ssr.get(rng.nextInt(heavy_ssr.size())));
                            ssr++;
                        } else if(rand < (7 + 12)) {
                            result = (heavy_sr.get(rng.nextInt(heavy_r.size())));
                            sr++;
                        } else if(rand < (7 + 12 + 26)) {
                            result = (heavy_r.get(rng.nextInt(heavy_r.size())));
                            r++;
                        } else {
                            result = (heavy_n.get(rng.nextInt(heavy_n.size())));
                            n++;
                        }
                        rand = (int) (GaussianRNG.FAST_RNG.generate(rng)*100);
                        Integer val = 0;
                        if(multiRes.containsKey(result.name)) {
                            val = multiRes.get(result.name);
                            val += 1;
                            multiRes.put(result.name, val);
                        } else {
                            multiRes.put(result.name, 1);
                        }
                    }
                    break;
                case "S":
                    for(int i = 0;i < rolls;i++) {
                        if(rand < 7) {
                            result = (sp_ssr.get(rng.nextInt(sp_ssr.size())));
                            ssr++;
                        } else if(rand < (7 + 12)) {
                            result = (sp_sr.get(rng.nextInt(sp_sr.size())));
                            sr++;
                        } else if(rand < (7 + 12 + 26)) {
                            result = (sp_r.get(rng.nextInt(sp_r.size())));
                            r++;
                        } else {
                            result = (sp_n.get(rng.nextInt(sp_n.size())));
                            n++;
                        }
                        rand = rng.nextInt(100);
                        if(multiRes.containsKey(result.name)) {
                            Integer val = multiRes.get(result.name);
                            val += 1;
                            multiRes.put(result.name, val);
                        } else {
                            multiRes.put(result.name, 1);
                        }
                    }
                    break;
                case  "T":
                    //TODO
                    break;
                default:
                    break;
            }

            for(String currName : multiRes.keySet()) {
                returnMsg.append(currName).append("  ").append(multiRes.get(currName)).append("次\n");
            }
            returnMsg.append("-----------\nSSR总计 ").append(ssr).append("\nSR总计 ")
                    .append(sr).append("\nR总计 ").append(r).append("\nN总计 ").append(n);
            returnMsg.append("\n祝欧🍻");
            cq.sendGroupMsg(event.getGroupId(), returnMsg.toString(), false);
            return MESSAGE_BLOCK;
        }
        switch (msgs[1]) {
            case "L":
                if(rand < 7) {
                    result = light_ssr.get(rng.nextInt(light_ssr.size()));
                } else if(rand < (7 + 12)) {
                    result = light_sr.get(rng.nextInt(light_sr.size()));
                } else if(rand < (7 + 12 + 26)) {
                    result = light_r.get(rng.nextInt(light_r.size()));
                } else {
                    result = light_n.get(rng.nextInt(light_n.size()));
                }
                break;
            case "H":
                if(rand < 7) {
                    result = heavy_ssr.get(rng.nextInt(heavy_ssr.size()));
                } else if(rand < (7 + 12)) {
                    result = heavy_sr.get(rng.nextInt(heavy_sr.size()));
                } else if(rand < (7 + 12 + 51)) {
                    result = heavy_r.get(rng.nextInt(heavy_r.size()));
                } else {
                    result = heavy_n.get(rng.nextInt(heavy_n.size()));
                }
                break;
            case "S":
                if(rand < 7) {
                    result = sp_ssr.get(rng.nextInt(sp_ssr.size()));
                } else if(rand < (7 + 12)) {
                    result = sp_sr.get(rng.nextInt(sp_sr.size()));
                } else if(rand < (7 + 12 + 51)) {
                    result = sp_r.get(rng.nextInt(sp_r.size()));
                } else {
                    result = sp_n.get(rng.nextInt(sp_n.size()));
                }
                break;
            case "T":
                returnMsg.append("当前还没写限时建造的部分(另外限时建造不是刚过去一波吗...)");
                break;
            default:
                returnMsg.append(helpInfo);
        }
        returnMsg.append("你获得了 ").append(result.category).append(" ").append(result.name);
        cq.sendGroupMsg(event.getGroupId(), returnMsg.toString(), false);
        returnMsg = new StringBuilder();
        switch (result.category) {
            case N:
                returnMsg.append("其实N也不错🍻");
                break;
            case R:
                returnMsg.append("所以说是必蓝航线嘛😂");
                break;
            case SR:
                returnMsg.append("手气不错(赞赏)");
                break;
            case SSR:
                returnMsg.append("啊这...欧皇!(非提の羡慕)");
                break;
        }
        cq.sendGroupMsg(event.getGroupId(), returnMsg.toString(), false);
        return MESSAGE_BLOCK;
    }
}
