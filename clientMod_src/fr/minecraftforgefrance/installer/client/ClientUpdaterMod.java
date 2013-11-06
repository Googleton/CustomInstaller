package fr.minecraftforgefrance.installer.client;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "ClientUpdater", name = "Client Updater", version = "@VERSION@")
public class ClientUpdaterMod
{
	public static Logger logger;
	public static String versionFileURL, installerName;

	@EventHandler
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
			versionFileURL = cfg.get(cfg.CATEGORY_GENERAL, "Version File URL", "http://files.minecraftforgefrance.fr/installercustom/version.txt").getString();
			installerName = cfg.get(cfg.CATEGORY_GENERAL, "Installer Name", "Installer").getString();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(cfg.hasChanged())
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
			try
			{
				Runtime.getRuntime().exec("java -jar " + updater.getAbsolutePath() + " --update");
				System.exit(0);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}