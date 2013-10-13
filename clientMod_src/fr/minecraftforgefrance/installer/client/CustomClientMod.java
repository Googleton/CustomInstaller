package fr.minecraftforgefrance.installer.client;

import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "CustomClient", name = "Custom Client", version = "1.0.0")

public class CustomClientMod
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
			System.exit(0);
		}
	}
}