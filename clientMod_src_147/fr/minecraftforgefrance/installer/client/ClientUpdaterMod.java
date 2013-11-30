package fr.minecraftforgefrance.installer.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "ClientUpdater", name = "Client Updater", version = "@VERSION@")
public class ClientUpdaterMod
{
	public static Logger logger;
	public static String versionFileURL, installerName;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		if(event.getSide().isServer())
		{
			logger.severe("CustomClient is a client mod");
			return;
		}
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try
		{
			cfg.load();
			versionFileURL = cfg.get(cfg.CATEGORY_GENERAL, "Version File URL", "http://files.minecraftforgefrance.fr/installercustom/version.txt").value;
			installerName = cfg.get(cfg.CATEGORY_GENERAL, "Installer Name", "Installer").value;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			cfg.save();
		}

		File updater = new File(Minecraft.getMinecraft().mcDataDir, installerName + ".jar");
		File newUpdater = new File(Minecraft.getMinecraft().mcDataDir, installerName + "new.jar");

		if(newUpdater.exists())
		{
			updater.delete();
			newUpdater.renameTo(updater);
		}

		if(!VersionUtils.isUpdated())
		{
			System.out.println(updater.getAbsolutePath());
			try
			{
				Runtime runtime = Runtime.getRuntime();
				final Process process = runtime.exec("java -jar " + updater.getAbsolutePath() + " --update");
				new Thread()
				{
					public void run()
					{
						try
						{
							BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
							BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "updaterLog.txt")));
							String line = "";
							try
							{
								while((line = reader.readLine()) != null)
								{
									writer.write(line);
								}
							}
							finally
							{
								reader.close();
							}
						}
						catch(IOException ioe)
						{
							ioe.printStackTrace();
						}
					}
				}.start();
				System.exit(0);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}