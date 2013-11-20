package cpw.mods.fml.installer;

import java.io.InputStream;
import java.io.InputStreamReader;

import argo.jdom.JdomParser;
import argo.jdom.JsonRootNode;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

public class Language
{
    public static final Language INSTANCE = new Language();
    public final JsonRootNode langData;
    
    public static String language;

    public Language()
    {
        language = System.getProperty("user.language");
        
        InputStream langStream = getClass().getResourceAsStream("/lang.json");

        JdomParser parser = new JdomParser();
        try
        {
            langData = parser.parse(new InputStreamReader(langStream, Charsets.UTF_8));
        }
        catch(Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

    public static String getLocalizedString(String str)
    {
        if(INSTANCE.langData.isStringValue(language, str))
        {
            return INSTANCE.langData.getStringValue(language, str);
        }
        else if(INSTANCE.langData.isStringValue("en", str))
        {
            return INSTANCE.langData.getStringValue("en", str);
        }
        else
        {
            return "Missing Translate";
        }
    }
}