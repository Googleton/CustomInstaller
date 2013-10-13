package fr.minecraftforgefrance.updater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.common.collect.Lists;

public class Updater
{
	public Updater(File workDir)
	{
        if(this.run(workDir))
        {
			JOptionPane.showMessageDialog(null, "Update is finished, you can restart the game", "Complete", JOptionPane.INFORMATION_MESSAGE);
        }
	}
	
	public boolean run(File target)
	{
		File modsFolder = new File(target, "mods");
		File configFolder = new File(target, "config");
		
		File reiminimap = new File(modsFolder, "rei_minimap");
		File reiminimaptemp = new File(target, "rei_minimap_temp");
		File versionFile = new File(target, "version.txt");

		// backup reiminimap config if exist
		if(reiminimap.exists())
		{
			reiminimap.renameTo(reiminimaptemp);
		}

		// Remove old mod folder
		if(modsFolder.exists())
		{
			try
			{
				recursifDelete(modsFolder);
			}
			catch(Exception ex)
			{}
		}
		
		if(configFolder.exists())
		{
			try
			{
				recursifDelete(configFolder);
			}
			catch(Exception ex)
			{}
		}
		
		try
		{
	        List<String> downloadLink = Lists.newArrayList();
	        downloadLink.add(VersionInfo.getModsURL());
	        downloadLink.add(VersionInfo.getConfigsURL());
	        if(VersionInfo.hasAdditionPack())
	        {
	            downloadLink.add(VersionInfo.getAdditionPackURL());
	        }
			DownloadUtils.downloadAndExtractMod(downloadLink, target);
			
			// readd reiminimap backup in mod folder
			if(reiminimaptemp.exists())
			{
				reiminimaptemp.renameTo(reiminimap);
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(versionFile));
			bw.write(VersionInfo.getRemoteVersion());
			bw.close();
		}
		catch(Exception ex)
		{
        	JOptionPane.showMessageDialog(null, "Couldn't update, fatal error", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	
	public static void recursifDelete(File path) throws IOException
	{
		if(!path.exists())
		{
			throw new IOException("File not found '" + path.getAbsolutePath() + "'");
		}
		if(path.isDirectory())
		{
			File[] children = path.listFiles();
			for(int i = 0; children != null && i < children.length; i++)
				recursifDelete(children[i]);
			if(!path.delete())
			{
				throw new IOException("No delete path '" + path.getAbsolutePath() + "'");
			}
		}
		else if(!path.delete())
		{
			throw new IOException("No delete file '" + path.getAbsolutePath() + "'");
		}
	}
}