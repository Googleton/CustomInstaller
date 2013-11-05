package fr.minecraftforgefrance.updater;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Main
{
    public static void main(String[] args)
    {
        final Class aClass = Updater.class;
        Constructor constructor;
        try
        {
            constructor = aClass.getConstructor(new Class[] {File.class});
            try
            {
                constructor.newInstance(new Object[] {new File(".")});
            }
            catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch(NoSuchMethodException | SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}