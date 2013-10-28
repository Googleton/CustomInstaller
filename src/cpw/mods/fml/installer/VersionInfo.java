package cpw.mods.fml.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import argo.jdom.JdomParser;
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

	public VersionInfo()
	{
		InputStream installerProfile = getClass().getResourceAsStream("/installer_profile.json");
		
		JdomParser parser = new JdomParser();
		try
		{
			installerData = parser.parse(new InputStreamReader(installerProfile, Charsets.UTF_8));
		}
		catch(Exception e)
		{
			throw Throwables.propagate(e);
		}
	}

	public static File getLibraryPath(File root)
	{
		String path = RemoteInfo.INSTANCE.remoteInstallerData.getStringValue("remoteInstall", "path");
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

	public static String getRemoteVersion()
	{
		String url = INSTANCE.installerData.getStringValue("install", "remoteVersionURL");
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

	public static File getMinecraftFile(File path)
	{
		return new File(new File(path, RemoteInfo.getMinecraftVersion()), RemoteInfo.getMinecraftVersion() + ".jar");
	}

	public static void extractFile(File path) throws IOException
	{
		INSTANCE.downloadForgeLib(path);
	}

	private void downloadForgeLib(File path) throws IOException
	{
		URL url = new URL(RemoteInfo.getFileLink());
		URLConnection urlconnection = url.openConnection();

		if((urlconnection instanceof HttpURLConnection))
		{
			urlconnection.setRequestProperty("Cache-Control", "no-cache");
			urlconnection.connect();
		}
		InputStream inputStream = urlconnection.getInputStream();
		OutputSupplier<FileOutputStream> outputSupplier = Files.newOutputStreamSupplier(path);
		ByteStreams.copy(inputStream, outputSupplier);
	}

	public static String getMirrorListURL()
	{
		return INSTANCE.installerData.getStringValue("install", "mirrorList");
	}

	public static boolean hasMirrors()
	{
		return INSTANCE.installerData.isStringValue("install", "mirrorList");
	}

	public static String getUpdaterURL()
	{
		return INSTANCE.installerData.getStringValue("install", "updaterURL");
	}
	
	public static String getWinProfile()
	{
		return INSTANCE.installerData.getStringValue("install", "winProfile");
	}
	
	public static String getUnixProfile()
	{
		return INSTANCE.installerData.getStringValue("install", "unixProfile");
	}
	
	public static String getRemoteInstall()
	{
		return INSTANCE.installerData.getStringValue("install", "remoteInstall");
	}
}