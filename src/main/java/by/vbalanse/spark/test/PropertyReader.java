package by.vbalanse.spark.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Aliaksandr_Zhuk on 7/18/2017.
 */
public class PropertyReader {

    public static Properties properties;

    public static void initPropertyReader(){
        properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            InputStream stream = loader.getResourceAsStream("pom.properties");
            properties.load(stream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }
}
