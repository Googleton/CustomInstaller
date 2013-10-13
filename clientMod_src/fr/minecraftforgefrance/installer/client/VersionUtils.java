package fr.minecraftforgefrance.installer.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

public class VersionUtils
{
	public static boolean hasCheckVersion;
	public static List<String> readRemoteFile(String fileURL)
	{
		try
		{
			URL url = new URL(fileURL);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			InputSupplier<InputStream> urlSupplier = new URLISSupplier(connection);
			return CharStreams.readLines(CharStreams.newReaderSupplier(urlSupplier, Charsets.UTF_8));
		}
		catch(Exception e)
		{
			return Collections.emptyList();
		}
	}

	static class URLISSupplier implements InputSupplier<InputStream>
	{
		private final URLConnection connection;

		private URLISSupplier(URLConnection connection)
		{
			this.connection = connection;
		}

		@Override
		public InputStream getInput() throws IOException
		{
			return connection.getInputStream();
		}
	}

	public static boolean isUpdated()
	{
		String localVersion;
		String remoteVersion;
		
		List<String> list = readRemoteFile(CustomClientMod.versionFileURL);
		if(list.isEmpty())
		{
			CustomClientMod.logger.severe("Couldn't get remote version, check your network");
			hasCheckVersion = false;
			return true;
		}
		
		remoteVersion = list.get(0);
		File localVersionFile = new File(Minecraft.getMinecraft().mcDataDir, "version.txt");
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(localVersionFile));
			localVersion = br.readLine();
			br.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			hasCheckVersion = false;
			return true;
		}
		return localVersion.equals(remoteVersion);
	}
}