package cpw.mods.fml.installer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.common.collect.Lists;

public class ExtractAction implements ActionType
{

    public static boolean headless;

    @Override
    public boolean run(File target)
    {
        File file = new File(target, VersionInfo.getContainedFile());
        try
        {
            VersionInfo.extractFile(file);
        }
        catch(IOException e)
        {
            if(!headless)
                JOptionPane.showMessageDialog(null, "An error occurred extracting file", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
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
        }
        catch(Exception e)
        {
            if(!headless)
                JOptionPane.showMessageDialog(null, "An error occurred extracting file", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public boolean isPathValid(File targetDir)
    {
        return targetDir.exists() && targetDir.isDirectory();
    }

    @Override
    public String getFileError(File targetDir)
    {
        return !targetDir.exists() ? "Target directory does not exist" : !targetDir.isDirectory() ? "Target is not a directory" : "";
    }

    @Override
    public String getSuccessMessage()
    {
        return "Extracted successfully";
    }

    @Override
    public String getSponsorMessage()
    {
        return null;
    }
}
