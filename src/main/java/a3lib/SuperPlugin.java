package a3lib;

import net.lz1998.cq.robot.CQPlugin;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SuperPlugin extends CQPlugin
{
    public String plugin_name = null;
    public boolean is_enabled = true;
    public static Date lastActiveTime = new Date();

    @Override
    public String toString()
    {
        return plugin_name;
    }
}
