/*
 FactoidInventory: Minecraft plugin for Inventory change (works with Factoid)
 Copyright (C) 2014  Michel Blanchet

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoidinventory.utils;

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
            final File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
            final JarFile jar = new JarFile(jarloc);
            final JarEntry entry = jar.getJarEntry("app.properties");
            final InputStream resource = jar.getInputStream(entry);
            properties.load(resource);
            resource.close();
            jar.close();

        } catch (final URISyntaxException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final IOException ex) {
            Logger.getLogger(MavenAppProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPropertyString(final String path) {

        return properties.getProperty(path);
    }

    public int getPropertyInt(final String path) {

        return Integer.parseInt(properties.getProperty(path));
    }
}
