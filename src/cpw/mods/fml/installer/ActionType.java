package cpw.mods.fml.installer;

import java.io.File;

public interface ActionType
{
    boolean run(File target, boolean updateMode);

    boolean isPathValid(File targetDir);

    String getFileError(File targetDir);

    String getSuccessMessage();

    String getSponsorMessage();
}
