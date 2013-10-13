package fr.minecraftforgefrance.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.tukaani.xz.XZInputStream;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

public class DownloadUtils
{
	public static List<String> downloadList(String libURL)
	{
		try
		{
			URL url = new URL(libURL);
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
	
	public static void downloadAndExtractMod(List<String> host, File dest)
	{
		IMonitor monitor = new IMonitor()
		{
			private ProgressMonitor monitor;
			{
				monitor = new ProgressMonitor(null, "Download and extract mods and configs             ", "   ", 0, 1);
				monitor.setMillisToPopup(0);
				monitor.setMillisToDecideToPopup(0);
				monitor.setProgress(0);
			}

			@Override
			public void setMaximum(int max)
			{
				monitor.setMaximum(max);
			}

			@Override
			public void setNote(String note)
			{
				System.out.println(note);
				monitor.setNote(note);
			}

			@Override
			public void setProgress(int progress)
			{
				monitor.setProgress(progress);
			}

			@Override
			public void close()
			{
				monitor.close();
			}
		};
		
		monitor.setNote("Update of " + VersionInfo.getPackName() + " in progress ...");

		for(String downloadLink : host)
		{
			downloadMods(monitor, downloadLink, dest);
		}
		monitor.close();
	}

	public static void downloadMods(IMonitor monitor, String host, File dest)
	{
		String fileName = "unknow";
		byte[] buffer = new byte[65536];
		try
		{
			URL url = new URL(host);
			URLConnection urlconnection = url.openConnection();

			if((urlconnection instanceof HttpURLConnection))
			{
				urlconnection.setRequestProperty("Cache-Control", "no-cache");
				urlconnection.connect();
			}

			int fileLength = urlconnection.getContentLength();

			if(fileLength == -1)
			{
				JOptionPane.showMessageDialog(null, "Invalide download link, cannot install", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			monitor.setMaximum(fileLength);

			fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
			InputStream inputstream = urlconnection.getInputStream();
			FileOutputStream fos = new FileOutputStream(dest.toString() + File.separator + fileName);

			long downloadStartTime = System.currentTimeMillis();
			int downloadedAmount = 0;
			int bufferSize;
			int currentSizeDownload = 0;
			while((bufferSize = inputstream.read(buffer, 0, buffer.length)) != -1)
			{
				fos.write(buffer, 0, bufferSize);
				currentSizeDownload += bufferSize;
				monitor.setProgress(currentSizeDownload);

				downloadedAmount += bufferSize;
				long timeLapse = System.currentTimeMillis() - downloadStartTime;

				if(timeLapse >= 1000L)
				{
					float downloadSpeed = downloadedAmount / (float)timeLapse;
					downloadSpeed = (int)(downloadSpeed * 100.0F) / 100.0F;
					downloadedAmount = 0;
					downloadStartTime += 1000L;

					if(downloadSpeed > 1000F)
					{
						DecimalFormat df = new DecimalFormat();
						df.setMaximumFractionDigits(2);
						monitor.setNote("Download " + fileName + " at " + String.valueOf(df.format(downloadSpeed / 1000)) + " mo/s");
					}
					else
					{
						monitor.setNote("Download " + fileName + " at " + String.valueOf(downloadSpeed) + " ko/s");
					}
				}
			}
			inputstream.close();
			fos.close();
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error while trying to download " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		// extract
		try
		{
			monitor.setNote(String.format("Extract : %s", fileName));
			int totalSizeExtract = 0;

			File fileLocation = new File(dest, fileName);

			File target;
			if(fileName.contains("mods"))
			{
				target = new File(dest, "mods");
			}
			else if(fileName.contains("config"))
			{
				target = new File(dest, "config");
			}
			else
			{
				target = dest;
			}
			if(!target.exists())
			{
				target.mkdir();
			}

			String path = fileLocation.toString();

			if(fileName.endsWith(".xz"))
			{
				FileInputStream inFile = new FileInputStream(path);
				FileOutputStream outfile = new FileOutputStream(path.replace(".xz", ""));

				XZInputStream xzIn = new XZInputStream(inFile);

				byte[] buf = new byte[8192];
				int size;
				while((size = xzIn.read(buf)) != -1)
				{
					outfile.write(buf, 0, size);
				}

				outfile.close();
				xzIn.close();

				fileLocation.delete();
				path = path.replace(".xz", "");
				fileLocation = new File(path);
			}

			JarFile jarFile = new JarFile(path, true);
			Enumeration<JarEntry> entities = jarFile.entries();

			while(entities.hasMoreElements())
			{
				JarEntry entry = (JarEntry)entities.nextElement();
				if(entry.isDirectory())
				{
					continue;
				}
				totalSizeExtract = (int)(totalSizeExtract + entry.getSize());
			}
			System.out.println(totalSizeExtract);
			monitor.setMaximum(totalSizeExtract);

			entities = jarFile.entries();

			while(entities.hasMoreElements())
			{
				JarEntry entry = (JarEntry)entities.nextElement();
				if(entry.isDirectory())
				{
					continue;
				}
				File f = new File(target + File.separator + entry.getName());
				f.getParentFile().mkdirs();
				if((f.exists()) && (!f.delete()))
				{
					continue;
				}

				InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));
				OutputStream out = new FileOutputStream(target + File.separator + entry.getName());

				int bufferSize;
				int currentSizeExtract = 0;
				while((bufferSize = in.read(buffer, 0, buffer.length)) != -1)
				{
					out.write(buffer, 0, bufferSize);
					currentSizeExtract += bufferSize;
					monitor.setProgress(currentSizeExtract);
				}
				in.close();
				out.close();
			}
			jarFile.close();

			fileLocation.delete();
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error while trying to extract " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}