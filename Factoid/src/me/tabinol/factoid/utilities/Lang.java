package me.tabinol.factoid.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import me.tabinol.factoid.Factoid;
import org.bukkit.ChatColor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class Lang extends Thread {

    public static final int ACTUAL_VERSION = 1; // +1 if there is a new version of the .conf
    private String lang = null;
    private File langFile;
    private FileConfiguration langconfig;
    private JavaPlugin plugin;

    public Lang() {
        this.langconfig = new YamlConfiguration();
        this.plugin = Factoid.getThisPlugin();
        reloadConfig();
        checkVersion();
    }

    public final void reloadConfig() {
        this.lang = Factoid.getConf().Lang;
        this.langFile = new File(plugin.getDataFolder() + "/lang/", lang + ".yml");
        if (Factoid.getConf().Lang != null) {
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

    // Notify with a message
    public static void notifyPlayer(String message, Permission permission) {

        for (Player players : Factoid.getThisPlugin().getServer().getOnlinePlayers()) {
            if (players.hasPermission(permission)) {
                players.sendMessage(ChatColor.GREEN + "[Factoid] " + message);
            }
        }

        Factoid.getThisPlugin().getLogger().log(Level.INFO, "[Factoid] " + message);
    }
}
