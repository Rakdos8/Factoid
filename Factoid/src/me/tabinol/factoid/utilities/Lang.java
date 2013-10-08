package me.tabinol.factoid.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import me.tabinol.factoid.Factoid;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Lang extends Thread {

    private static String lang = null;
    private File langFile;
    private static FileConfiguration langconfig;
    private JavaPlugin plugin;

    public Lang() {
        this.plugin = Factoid.getThisPlugin();
        this.lang = Factoid.getConf().Lang;
        this.langFile = new File(plugin.getDataFolder() + "/lang/", lang + ".yml");
        this.langconfig = new YamlConfiguration();
        Make();
        loadYamls();
    }

    public static String getMessage(String path,String... param) {
       if(param != null){
          String returnmessage = null;
          int i = 0;
          String message = langconfig.getString(path);
          String[] messages = message.split("%");
          for(String mess : messages){
            returnmessage = replace(message,"%",param[i]);
          }
          return returnmessage;
       }
        return langconfig.getString(path);
    }
    
    public static String replace(String s_original, String s_cherche, String s_nouveau)  
    {  
      if ((s_original == null) || (s_original.equals("")))  
         return "";  
      if ((s_nouveau == null) || (s_nouveau.equals("")) || (s_cherche == null) || (s_cherche.equals("")))  
         return new String(s_original);  

      StringBuffer s_final;  
      int index = s_original.indexOf(s_cherche);  

      s_final = new StringBuffer(s_original.substring(0,index));  
      s_final.append(s_nouveau);  
      s_final.append(s_original.substring(index+s_cherche.length()));  

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
                copy(plugin.getResource("english.yml"), langFile);
                copy(plugin.getResource("french.yml"), langFile);
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
}
