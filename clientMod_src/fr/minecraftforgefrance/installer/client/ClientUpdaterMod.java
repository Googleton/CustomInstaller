package fr.minecraftforgefrance.installer.client;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "ClientUpdater", name = "Client Updater", version = "1.0.0")
public class ClientUpdaterMod
{
	public static Logger logger;
	public static String versionFileURL;

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
			versionFileURL = cfg.get(cfg.CATEGORY_GENERAL, "Version File URL", "change me").getString();
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

		if(!VersionUtils.isUpdated())
		{
			this.startUpdater(new File(Minecraft.getMinecraft().mcDataDir, "Updater.jar"));
		}
	}

	@SuppressWarnings("resource")
	public void startUpdater(final File updaterJar)
	{
		logger.info("Starting launcher.");
		try
		{
			final Class aClass = new URLClassLoader(new URL[] {updaterJar.toURI().toURL()}).loadClass("fr.minecraftforgefrance.updater.Updater");
			final Constructor constructor = aClass.getConstructor(new Class[] {File.class});
			constructor.newInstance(new Object[] {Minecraft.getMinecraft().mcDataDir});
			System.exit(0);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
			logger.severe("Coundn't run updater");
		}
	}
}