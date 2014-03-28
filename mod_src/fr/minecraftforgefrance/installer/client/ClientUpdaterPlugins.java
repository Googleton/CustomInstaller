package fr.minecraftforgefrance.installer.client;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.7.2")
public class ClientUpdaterPlugins implements IFMLLoadingPlugin
{
    public static File mcDir;

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
	public String getAccessTransformerClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		mcDir = (File)data.get("mcLocation");
	}
	
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{};
	}
}