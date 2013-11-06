package cpw.mods.fml.installer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

public class RemoteInfo
{
    public static final RemoteInfo INSTANCE = new RemoteInfo();
    public final JsonRootNode profileData;
    public final JsonRootNode remoteInstallerData;

    public RemoteInfo()
    {
        InputStream launcherProfile = null;
        InputStream remoteInstallerProfile = null;
        try
        {
            URL url = null;
            URL url2 = new URL(VersionInfo.getRemoteInstall());
            if(SimpleInstaller.getPlatform().equals(SimpleInstaller.EnumOs.WINDOWS))
            {
                url = new URL(VersionInfo.getWinProfile());
            }
            else
            {
                url = new URL(VersionInfo.getUnixProfile());
            }
            URLConnection urlconnection = url.openConnection();
            URLConnection urlconnection2 = url2.openConnection();

            if((urlconnection instanceof HttpURLConnection))
            {
                urlconnection.setRequestProperty("Cache-Control", "no-cache");
                urlconnection.connect();
            }
            if((urlconnection2 instanceof HttpURLConnection))
            {
                urlconnection2.setRequestProperty("Cache-Control", "no-cache");
                urlconnection2.connect();
            }
            launcherProfile = urlconnection.getInputStream();
            remoteInstallerProfile = urlconnection2.getInputStream();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Couldn't get installer profile, check your network", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JdomParser parser = new JdomParser();
        try
        {
            profileData = parser.parse(new InputStreamReader(launcherProfile, Charsets.UTF_8));
            remoteInstallerData = parser.parse(new InputStreamReader(remoteInstallerProfile, Charsets.UTF_8));
        }
        catch(Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public static JsonNode getVersionInfo()
    {
        return INSTANCE.profileData.getNode("versionInfo");
    }

    public static String getProfileName()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "profileName");
    }

    public static String getVersionTarget()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "target");
    }

    public static String getNameAndVersion()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "installerName") + " " + VersionInfo.getRemoteVersion();
    }

    public static String getWelcomeMessage()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "welcome");
    }

    public static String getMinecraftVersion()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "minecraft");
    }

    public static String getFileLink()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "fileLink");
    }

    public static String getJVMArguments()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "JVMArguments");
    }

    public static boolean hasJVMArguments()
    {
        return INSTANCE.remoteInstallerData.isStringValue("remoteInstall", "JVMArguments");
    }

    public static String getModsURL()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "modsURL");
    }

    public static String getUpdaterURL()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "updaterURL");
    }

    public static String getConfigsURL()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "configsURL");
    }

    public static String getAdditionPackURL()
    {
        return INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "additionPackURL");
    }

    public static boolean hasAdditionPack()
    {
        return INSTANCE.remoteInstallerData.isStringValue("remoteInstall", "additionPackURL");
    }
}