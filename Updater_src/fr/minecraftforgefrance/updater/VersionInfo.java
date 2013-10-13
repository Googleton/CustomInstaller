package fr.minecraftforgefrance.updater;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

public class VersionInfo
{
    public static final VersionInfo INSTANCE = new VersionInfo();
    public final JsonRootNode updaterData;

    public VersionInfo()
    {
        InputStream updaterProfile = getClass().getResourceAsStream("/updater_profile.json");
        JdomParser parser = new JdomParser();

        try
        {
            updaterData = parser.parse(new InputStreamReader(updaterProfile, Charsets.UTF_8));
        }
        catch(Exception e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    public static String getRemoteVersion()
    {
        String url = INSTANCE.updaterData.getStringValue("install", "remoteVersionURL");
        List<String> version = DownloadUtils.downloadList(url);
        if(version.isEmpty())
        {
        	return "unknow";
        }
        else
        {
            return version.get(0);
        }
    }
    
    public static String getPackName()
    {
        return INSTANCE.updaterData.getStringValue("install", "packName");
    }
    
    public static String getModsURL()
    {
        return INSTANCE.updaterData.getStringValue("install", "modsURL");
    }
    
    public static String getConfigsURL()
    {
        return INSTANCE.updaterData.getStringValue("install", "configsURL");
    }
    
    public static String getAdditionPackURL()
    {
        return INSTANCE.updaterData.getStringValue("install", "additionPackURL");
    }
    
    public static boolean hasAdditionPack()
    {
        return INSTANCE.updaterData.isStringValue("install", "additionPackURL");
    }
}