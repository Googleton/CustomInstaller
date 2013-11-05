package cpw.mods.fml.installer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

public class SimpleInstaller
{
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        OptionParser parser = new OptionParser();
        OptionSpecBuilder update = parser.accepts("update", "Run a update");
        OptionSpecBuilder helpOption = parser.acceptsAll(Arrays.asList("h", "help"), "Help with this installer");
        OptionSet optionSet = parser.parse(args);
        if(optionSet.specs().size() > 0)
        {
            handleOptions(parser, optionSet, update, helpOption);
        }
        else
        {
            launchGui(false);
        }
    }

    private static void handleOptions(OptionParser parser, OptionSet optionSet, OptionSpecBuilder update, OptionSpecBuilder helpOption) throws IOException
    {
        if(optionSet.has(update))
        {
            try
            {
                launchGui(true);
            }
            catch(Throwable e)
            {
                System.err.println("A problem extracting the file was detected, extraction failed");
                System.exit(1);
            }
        }
        else
        {
            parser.printHelpOn(System.err);
        }
    }

    public static EnumOs getPlatform()
    {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if(osName.contains("win"))
        {
            return EnumOs.WINDOWS;
        }
        if(osName.contains("mac"))
        {
            return EnumOs.MACOS;
        }
        if(osName.contains("solaris") || osName.contains("sunos") || osName.contains("linux") || osName.contains("unix"))
        {
            return EnumOs.UNIX;
        }
        return EnumOs.UNKNOWN;
    }

    public enum EnumOs
    {
        WINDOWS, MACOS, UNIX, UNKNOWN;
    }

    private static void launchGui(boolean updateMode)
    {
        String userHomeDir = System.getProperty("user.home", ".");
        File targetDir = null;
        String mcDir = ".minecraft";
        if(getPlatform().equals(EnumOs.WINDOWS) && System.getenv("APPDATA") != null)
        {
            targetDir = new File(System.getenv("APPDATA"), mcDir);
        }
        else if(getPlatform().equals(EnumOs.MACOS))
        {
            targetDir = new File(new File(new File(userHomeDir, "Library"), "Application Support"), "minecraft");
        }
        else if(getPlatform().equals(EnumOs.UNIX))
        {
            targetDir = new File(userHomeDir, mcDir);
        }
        else
        {
            JOptionPane.showMessageDialog(null, "OS unrecognized, cannot install", "Error", JOptionPane.ERROR_MESSAGE);
        }

        try
        {
        	RemoteInfo.getVersionTarget();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Corrupt download detected, cannot install", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {}

        InstallerPanel panel = new InstallerPanel(targetDir, updateMode);
        panel.run(updateMode);
    }
}