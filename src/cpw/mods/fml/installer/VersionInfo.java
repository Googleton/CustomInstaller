package cpw.mods.fml.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

public class VersionInfo
{
    public static final VersionInfo INSTANCE = new VersionInfo();
    public final JsonRootNode installerData;
    public final JsonRootNode profileData;

    public VersionInfo()
    {
        InputStream installerProfile = getClass().getResourceAsStream("/installer_profile.json");
        InputStream launcherProfile = null;
        if(SimpleInstaller.getPlatform().equals(SimpleInstaller.EnumOs.WINDOWS))
        {
            launcherProfile = getClass().getResourceAsStream("/launcher_profile_WIN.json");
        }
        else
        {
            launcherProfile = getClass().getResourceAsStream("/launcher_profile_UNIX.json");
        }
        JdomParser parser = new JdomParser();

        try
        {
            installerData = parser.parse(new InputStreamReader(installerProfile, Charsets.UTF_8));
            profileData = parser.parse(new InputStreamReader(launcherProfile, Charsets.UTF_8));
        }
        catch(Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public static String getProfileName()
    {
        return INSTANCE.installerData.getStringValue("install", "profileName");
    }

    public static String getVersionTarget()
    {
        return INSTANCE.installerData.getStringValue("install", "target");
    }

    public static File getLibraryPath(File root)
    {
        String path = INSTANCE.installerData.getStringValue("install", "path");
        String[] split = Iterables.toArray(Splitter.on(':').omitEmptyStrings().split(path), String.class);
        File dest = root;
        Iterable<String> subSplit = Splitter.on('.').omitEmptyStrings().split(split[0]);
        for(String part : subSplit)
        {
            dest = new File(dest, part);
        }
        dest = new File(new File(dest, split[1]), split[2]);
        String fileName = split[1] + "-" + split[2] + ".jar";
        return new File(dest, fileName);
    }

    public static String getVersion()
    {
        return INSTANCE.installerData.getStringValue("install", "version");
    }

    public static String getWelcomeMessage()
    {
        return INSTANCE.installerData.getStringValue("install", "welcome");
    }

    public static String getLogoFileName()
    {
        return INSTANCE.installerData.getStringValue("install", "logo");
    }

    public static boolean getStripMetaInf()
    {
        try
        {
            return INSTANCE.installerData.getBooleanValue("install", "stripMeta");
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static JsonNode getVersionInfo()
    {
        return INSTANCE.profileData.getNode("versionInfo");
    }

    public static File getMinecraftFile(File path)
    {
        return new File(new File(path, getMinecraftVersion()), getMinecraftVersion() + ".jar");
    }

    public static String getContainedFile()
    {
        return INSTANCE.installerData.getStringValue("install", "filePath");
    }

    public static void extractFile(File path) throws IOException
    {
        INSTANCE.doFileExtract(path);
    }

    private void doFileExtract(File path) throws IOException
    {
        InputStream inputStream = getClass().getResourceAsStream("/" + getContainedFile());
        OutputSupplier<FileOutputStream> outputSupplier = Files.newOutputStreamSupplier(path);
        ByteStreams.copy(inputStream, outputSupplier);
    }

    public static String getMinecraftVersion()
    {
        return INSTANCE.installerData.getStringValue("install", "minecraft");
    }

    public static String getMirrorListURL()
    {
        return INSTANCE.installerData.getStringValue("install", "mirrorList");
    }

    public static boolean hasMirrors()
    {
        return INSTANCE.installerData.isStringValue("install", "mirrorList");
    }
}
