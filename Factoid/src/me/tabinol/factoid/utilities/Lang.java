package me.tabinol.factoid.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Lang extends Thread{
    
	private static String lang = null;
	private File langFile;
	private static FileConfiguration langconfig;
        private JavaPlugin plugin;
	
	public Lang(File folder,String lang,JavaPlugin plugin){
                this.lang = lang;
		this.langFile = new File(folder+"/lang/", lang+".yml");
		this.langconfig = new YamlConfiguration();
                this.plugin = plugin;
                Make();
		loadYamls();
	}
	
	public static String getMessage(String path){
		return langconfig.getString(path);
	}
	
	private void loadYamls() {
            try {
                langconfig.load(langFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
	}
        
        private void Make(){
            try{
                if(!langFile.exists()){
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
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
