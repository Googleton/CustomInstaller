package fr.minecraftforgefrance.installer.client;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class ClientUpdaterPlugins implements IFMLLoadingPlugin
{
    public static File mcDir;
	@Override
	public String[] getLibraryRequestClass()
	{
		return null;
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{};
	}

	@Override
	public String getModContainerClass()
	{
		return ClientUpdaterModContainer.class.getName();
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		mcDir = (File)data.get("mcLocation");
	}
}