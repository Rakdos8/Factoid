package me.tabinol.factoid.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Load app.properties from Maven properties
 *
 * @author Tabinol
 */
public class MavenAppProperties {

    Properties properties;

    public MavenAppProperties() {
        this.properties = new Properties();
    }

    public void loadProperties() {

        try {
            
            File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
            JarFile jar = new JarFile(jarloc);
            JarEntry entry = jar.getJarEntry("app.properties");
            InputStream resource = jar.getInputStream(entry);
            properties.load(resource);
            resource.close();
        
        } catch (URISyntaxException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPropertyString(String path) {

        return properties.getProperty(path);
    }

    public int getPropertyInt(String path) {

        return Integer.parseInt(properties.getProperty(path));
    }
}
