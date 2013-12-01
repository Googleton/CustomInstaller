package fr.minecraftforgefrance.installer.client;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.Configuration;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

public class ClientUpdaterModContainer extends DummyModContainer
{
	public static Logger logger;
	public static String versionFileURL, installerName;

	public ClientUpdaterModContainer()
	{
		super(new ModMetadata());
		ModMetadata meta = super.getMetadata();
		meta.authorList = Arrays.asList(new String[] {"robin4002", "Woryk", "Shadt", "Arzchimonde"});
		meta.modId = "ClientUpdater";
		meta.version = "1.6.4";
		meta.name = "Client Updater";
		meta.version = "@VERSION@";

		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			Configuration cfg = new Configuration(new File(new File(ClientUpdaterPlugins.mcDir, "config"), "ClientUpdater.cfg"));
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
				System.out.println(updater.getAbsolutePath());
				if(!updater.exists())
				{
					try
					{
						throw(new IOException("Fatal error, updater no found, please run the installer manually"));
					}
					catch(IOException e)
					{
						e.printStackTrace();
						System.exit(-1);
					}
				}
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

	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		return true;
	}
}