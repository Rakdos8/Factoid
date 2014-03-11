/*
 Factoid: Lands and Factions plugin for Minecraft server
 Copyright (C) 2014 Kaz00, Tabinol

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.tabinol.factoid.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.logging.Level;
import me.tabinol.factoid.Factoid;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Lang extends Thread {

    public static final int ACTUAL_VERSION = Factoid.getMavenAppProperties().getPropertyInt("langVersion");
    private String lang = null;
    private File langFile;
    private final FileConfiguration langconfig;
    private final JavaPlugin plugin;

    public Lang() {
        this.langconfig = new YamlConfiguration();
        this.plugin = Factoid.getThisPlugin();
        reloadConfig();
        checkVersion();
    }

    public final void reloadConfig() {
        this.lang = Factoid.getConf().getLang();
        this.langFile = new File(plugin.getDataFolder() + "/lang/", lang + ".yml");
        if (Factoid.getConf().getLang() != null) {
            Make();
            loadYamls();
        }
    }

    // Check if it is the next version, if not, the file will be renamed
    public final void checkVersion() {

        int fileVersion = langconfig.getInt("VERSION");

        // We must rename the file and activate the new file
        if (ACTUAL_VERSION != fileVersion) {
            langFile.renameTo(new File(plugin.getDataFolder() + "/lang/", lang + ".yml.v" + fileVersion));
            reloadConfig();
            plugin.getLogger().log(Level.INFO, "There is a new language file. Your old language file was renamed \""
                    + lang + ".yml.v" + fileVersion + "\".");
        }
    }

    public String getMessage(String path, String... param) {

        String message = langconfig.getString(path);

        if (message == null) {
            return "MESSAGE NOT FOUND FOR PATH: " + path;
        }
        if (param.length >= 1) {
            int occurence = getOccurence(message, '%');
            if (occurence == param.length) {
                for (int i = 0; i < occurence; i++) {
                    message = replace(message, "%", param[i]);
                    // System.out.print(message);
                }
            } else {
                return "Error! variable missing for Entries.";
            }
        }

        return message;
    }

    public boolean isMessageExist(String path) {

        return langconfig.getString(path) != null;
    }

    public String replace(String s_original, String s_cherche, String s_nouveau) {
        if ((s_original == null) || (s_original.equals(""))) {
            return "";
        }
        if ((s_nouveau == null) || (s_nouveau.equals("")) || (s_cherche == null) || (s_cherche.equals(""))) {
            return new String(s_original);
        }

        StringBuffer s_final;
        int index = s_original.indexOf(s_cherche);

        s_final = new StringBuffer(s_original.substring(0, index));
        s_final.append(s_nouveau);
        s_final.append(s_original.substring(index + s_cherche.length()));

        return s_final.toString();
    }

    private void loadYamls() {
        try {
            langconfig.load(langFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Make() {
        try {
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                copy(plugin.getResource(lang + ".yml"), langFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getOccurence(String s, char r) {
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == r) {
                counter++;
            }
        }
        return counter;
    }

    public String getHelp(String commandName) {
        
        ConfigurationSection helpSec = langconfig.getConfigurationSection("HELP." + commandName);
        
        // No help for this command?
        if(helpSec == null) {
            return null;
        }
        
        Map<String, Object> valueList = helpSec.getValues(false);
        StringBuilder sb = new StringBuilder();
        
        for(int t = 1; t <= valueList.size(); t ++) {
            sb.append((String) valueList.get(t + "")).append(Factoid.getConf().NEWLINE);
        }
        
        return sb.toString();
    }
}
