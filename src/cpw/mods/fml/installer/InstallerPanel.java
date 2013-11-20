package cpw.mods.fml.installer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.google.common.base.Throwables;

public class InstallerPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private File targetDir;
    private JLabel infoLabel;
    private JDialog dialog;
    // private JLabel sponsorLogo;
    private JPanel fileEntryPanel;

    public InstallerPanel(File targetDir, boolean updateMode)
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        BufferedImage image;
        try
        {
            image = ImageIO.read(SimpleInstaller.class.getResourceAsStream(VersionInfo.getLogoFileName()));
        }
        catch(IOException e)
        {
            throw Throwables.propagate(e);
        }

        JPanel logoSplash = new JPanel();
        logoSplash.setLayout(new BoxLayout(logoSplash, BoxLayout.Y_AXIS));
        ImageIcon icon = new ImageIcon(image);
        JLabel logoLabel = new JLabel(icon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        logoLabel.setAlignmentY(CENTER_ALIGNMENT);
        logoLabel.setSize(image.getWidth(), image.getHeight());
        logoSplash.add(logoLabel);
        String welcome = updateMode ? Language.getLocalizedString("update.available") : RemoteInfo.getWelcomeMessage();
        JLabel tag = new JLabel(welcome);
        tag.setAlignmentX(CENTER_ALIGNMENT);
        tag.setAlignmentY(CENTER_ALIGNMENT);
        logoSplash.add(tag);
        tag = new JLabel(VersionInfo.getRemoteVersion());
        tag.setAlignmentX(CENTER_ALIGNMENT);
        tag.setAlignmentY(CENTER_ALIGNMENT);
        logoSplash.add(tag);

        logoSplash.setAlignmentX(CENTER_ALIGNMENT);
        logoSplash.setAlignmentY(TOP_ALIGNMENT);
        this.add(logoSplash);


        // sponsorLogo = new JLabel();
        // sponsorLogo.setSize(50, 20);
        // sponsorLogo.setAlignmentX(CENTER_ALIGNMENT);
        // sponsorLogo.setAlignmentY(CENTER_ALIGNMENT);
        // sponsorPanel.add(sponsorLogo);

        new ButtonGroup();

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));

        choicePanel.setAlignmentX(RIGHT_ALIGNMENT);
        choicePanel.setAlignmentY(CENTER_ALIGNMENT);
        add(choicePanel);
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));

        this.targetDir = targetDir;

        entryPanel.setAlignmentX(LEFT_ALIGNMENT);
        entryPanel.setAlignmentY(TOP_ALIGNMENT);

        infoLabel = new JLabel();
        infoLabel.setHorizontalTextPosition(JLabel.LEFT);
        infoLabel.setVerticalTextPosition(JLabel.TOP);
        infoLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoLabel.setAlignmentY(TOP_ALIGNMENT);
        infoLabel.setForeground(Color.RED);
        infoLabel.setVisible(false);

        fileEntryPanel = new JPanel();
        fileEntryPanel.setLayout(new BoxLayout(fileEntryPanel, BoxLayout.Y_AXIS));
        fileEntryPanel.add(infoLabel);
        fileEntryPanel.add(Box.createVerticalGlue());
        fileEntryPanel.add(entryPanel);
        fileEntryPanel.setAlignmentX(CENTER_ALIGNMENT);
        fileEntryPanel.setAlignmentY(TOP_ALIGNMENT);
        this.add(fileEntryPanel);
        updateFilePath();
    }

    private void updateFilePath()
    {
        try
        {
            targetDir = targetDir.getCanonicalFile();
        }
        catch(IOException e)
        {

        }

        InstallerAction action = InstallerAction.CLIENT;
        boolean valid = action.isPathValid(targetDir);

        if(valid)
        {
            infoLabel.setVisible(false);
            fileEntryPanel.setBorder(null);
        }
        else
        {
            fileEntryPanel.setBorder(new LineBorder(Color.RED));
            infoLabel.setText("<html>" + action.getFileError(targetDir) + "</html>");
            infoLabel.setVisible(true);
        }
        if(dialog != null)
        {
            dialog.invalidate();
            dialog.pack();
        }
    }

    public void run(boolean updateMode)
    {
        String buttonName = updateMode ? Language.getLocalizedString("button.update") : Language.getLocalizedString("button.install");
        JOptionPane optionPane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new String[] {buttonName, Language.getLocalizedString("button.cancel")}, "defaut");

        Frame emptyFrame = new Frame(Language.getLocalizedString("frame.install"));
        emptyFrame.setUndecorated(true);
        emptyFrame.setVisible(true);
        emptyFrame.setLocationRelativeTo(null);
        String frameName = updateMode ? Language.getLocalizedString("frame.update") : Language.getLocalizedString("frame.install");
        dialog = optionPane.createDialog(emptyFrame, frameName);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        String result = (String)(optionPane.getValue() != null ? optionPane.getValue() : Language.getLocalizedString("error"));
        if(result.equals(Language.getLocalizedString("button.install")) || result.equals(Language.getLocalizedString("button.update")))
        {
            InstallerAction action = InstallerAction.CLIENT;
            if(action.run(targetDir, updateMode))
            {
                JOptionPane.showMessageDialog(null, action.getSuccessMessage(), Language.getLocalizedString("complete"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
        dialog.dispose();
        emptyFrame.dispose();
    }
}