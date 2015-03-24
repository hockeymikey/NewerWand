/**
 * 
 */
package mn.hockeymikey.newerwand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

/**
 * @author Mikey Hajostek
 *
 */
public class NewerWand extends JavaPlugin{

	public static FileConfiguration config;
	public static FileConfiguration settings;
	public static File settingsFile;

	public static List<String> WandLore = new ArrayList<String>();
	public static String WandDisplayName = "";

	public static Material LeftPointBlock;
	public static byte LeftPBData;

	public static Material RightPointBlock;
	public static byte RightPBData;

	String RootCommand = "";

	public static Material WandItem;

	private static Plugin plugin;
	

    public static String PluginPrefix = ChatColor.DARK_PURPLE+"["+ChatColor.GOLD+"NewerWand"+ChatColor.DARK_PURPLE+"] "+ChatColor.AQUA;
	
	public static HashMap<String, HashMap<String, Location>> points = new HashMap<String, HashMap<String, Location>>();
	public static HashMap<String, HashMap<String, Location>> OldPoints = new HashMap<String, HashMap<String, Location>>();
	
	String prefix = "";
	
    
    

	public void onEnable() {

		plugin = this;
		
	    
		Variables();

		init();

		getWorldEdit();
		//getWorldGuard();

		saveDefaultConfig();
		saveSettings();

		config = getConfig();
		
		System.out.print(RootCommand);

//		if (RootCommand != null) {
//			getCommand(RootCommand).setExecutor(new NewerCommands(this));
//		}
		
		getCommand("nw").setExecutor(new NewerCommands(this));

	}

	
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(PluginPrefix+"Removing Guide blocks.  Please wait.");
		
		RevertBlocks();
		
		getServer().getConsoleSender().sendMessage(PluginPrefix+ChatColor.RED+"NewerWand disabled.");
		plugin = null;
	}
	
	
	public void Variables() {
		
		if (getConfig().contains("Verion")){
			if (getConfig().getInt("Version") != 1) {
				getServer().getConsoleSender().sendMessage(PluginPrefix+ChatColor.RED+"Config is outdated or too new.  Please reload it.");
			}
		}
		
		else {
			getServer().getConsoleSender().sendMessage(PluginPrefix+ChatColor.RED+"Version line removed. Please reload config!");
		}
		
		if (getConfig().contains("Message_Prefix")) {
			prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Message_Prefix"));
		}
		
		else {
			prefix = ChatColor.translateAlternateColorCodes('&', "&d[&6NW&d] &6");
		}
		
//		if (getConfig().contains("Root_Command")) {
//			RootCommand = getConfig().getString("Root_Command");
//		}
//		
//		else {
//			getServer().getConsoleSender().sendMessage(PluginPrefix+ChatColor.RED+"No Command defined! Using default /NW ");
//		}
//		
		if (getConfig().contains("Wand_Item")) {
			int witem = Integer.parseInt(getConfig().getString("Wand_Item"));
			WandItem = Material.getMaterial(witem);
		}
		
		else {
			WandItem = Material.getMaterial(283);
		}




		// Block of the points
		String lc = "";
		
		if (getConfig().contains("Left_Point_Block")) {
			
			lc = getConfig().getString("Left_Point_Block");
	
		}
		
		else {
			lc = "95:5";
		}
		
		if (lc.contains(":")) {
			String[] v2 = lc.split(":");
			int block = Integer.parseInt(v2[0]);
			LeftPointBlock = Material.getMaterial(block);

			int data = Integer.parseInt(v2[1]);
			LeftPBData = (byte) data;
		}

		else {
			int lbb = Integer.parseInt(lc);
			LeftPointBlock = Material.getMaterial(lbb);
			LeftPBData = (byte) 0;
		}

		
		String rc = "";
		
		if (getConfig().contains("Right_Point_Block")) {
			
			rc = getConfig().getString("Right_Point_Block");

		}
		
		else {
			rc = "95:10";
		}
		

		if (rc.contains(":")) {
			String[] v2 = rc.split(":");

			int rblock = Integer.parseInt(v2[0]);

			RightPointBlock = Material.getMaterial(rblock);

			int data = Integer.parseInt(v2[1]);
			RightPBData = (byte) data;
		}

		else {
			int lbb = Integer.parseInt(rc);
			RightPointBlock = Material.getMaterial(lbb);
			RightPBData = (byte) 0;
		}



		if (getConfig().contains("Wand_Lore")) {
			
			List<String> lorelist = new ArrayList();
			
			lorelist = getConfig().getStringList("Wand_Lore");

			for (String l : lorelist) {
				WandLore.add(ChatColor.translateAlternateColorCodes('&', l));
			}
		}
		
		else {
			WandLore.add(ChatColor.translateAlternateColorCodes('&', "&bSuper Cool"));
			WandLore.add(ChatColor.translateAlternateColorCodes('&', "&bMagic wand"));
		}

		if (getConfig().contains("Wand_Display_Name")) {
			WandDisplayName = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("Wand_Display_Name"));
		}
		
		else {
			WandDisplayName = ChatColor.translateAlternateColorCodes('&', "&3Magic Wand");
		}
		
		
	}

	public void reloadSettings() {
		if (settingsFile == null) {
			settingsFile = new File(getDataFolder(), "settings.yml");
		}
		settings = YamlConfiguration.loadConfiguration(settingsFile);
	}

	public FileConfiguration getSettings() {
		if (settings == null) {
			reloadSettings();
		}
		return settings;
	}

	public void saveCustomConfig() {
		if (settings == null || settingsFile == null) {
			return;
		}
		try {
			getSettings().save(settingsFile);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + settingsFile, ex);
		}
	}

	public void saveDefaultSettings() {
		if (settingsFile == null) {
			settingsFile = new File(getDataFolder(), "settings.yml");
		}
		if (!settingsFile.exists()) {
			plugin.saveResource("settings.yml", false);
		}
	}

	public void saveSettings() {
		if (settings == null || settingsFile == null) {
			return;
		}
		try {
			getSettings().save(settingsFile);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + settingsFile, ex);
		}
	}


	private void init() {
		registerListeners();
	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new NewerListener(this), this);

	}

	public static Plugin getPlugin() {
		return plugin;
	}

	public static WorldEditPlugin getWorldEdit() {
		Plugin plugin = Bukkit.getServer().getPluginManager()
				.getPlugin("WorldEdit");
		if ((plugin == null) || (!(plugin instanceof WorldEditPlugin))) {
			return null;
		}
		return (WorldEditPlugin) plugin;
	}

//	public static WorldGuardPlugin getWorldGuard() {
//		Plugin plugin = Bukkit.getServer().getPluginManager()
//				.getPlugin("WorldGuard");
//		if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {
//			return null;
//		}
//		return (WorldGuardPlugin) plugin;
//	}
	
	public void RevertBlocks() {
		for (Player vp : Bukkit.getOnlinePlayers()) {

			if (OldPoints.containsKey(vp)) {

				for (Map.Entry<String, Location> ds: OldPoints.get(vp).entrySet()) {
					Location ds2 = ds.getValue();
					vp.sendBlockChange(ds2, ds2.getBlock().getType(), ds2.getBlock().getData());
					
				
			      }
				vp.sendMessage(prefix+getConfig().getString("Plugin_ShutDown_Cancel_Claiming"));
			}
			
		}
	}
}
