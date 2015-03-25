package mn.hockeymikey.newerwand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mn.hockeymikey.newerwand.NewerListener;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
// import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;

import java.util.Map.Entry;

public class NewerCommands implements CommandExecutor {

	NewerWand newerwand;
	
/*	WandListener wandlis;*/
	/*
	 * Location left = WandListener.left; Location leftX = WandListener.leftX;
	 * Location leftZ = WandListener.leftZ; Location leftY = WandListener.leftY;
	 * Location right = WandListener.right; Location rightX =
	 * WandListener.rightX; Location rightY = WandListener.rightY; Location
	 * rightZ = WandListener.rightZ;
	 */
	Player p;
	String pl;
	
	String configReloaded = "";
	String noPermissions = "";
	String incorrectUsage = "";
	String givenwand = "";
	String prefix = "";

	String full = "";
	String already = "";
	String player = "";
	


	List<String> WandLore = NewerWand.WandLore;
	String WandDisplayName = NewerWand.WandDisplayName;

	List<Map<?, ?>> HelpMenu;
	List<Map<?, ?>> SelectionCancelled;
	List<Map<?, ?>> NoSelection;

	Material WandItem = NewerWand.WandItem;

	boolean checker = false;
	
	HashMap<String, HashMap<String, Location>> points = NewerWand.points;
	HashMap<String, HashMap<String, Location>> OldPoints = NewerWand.OldPoints;

	// HashMap<String, Boolean> mode = WandListener.mode;

	public NewerCommands(NewerWand instance) {
		this.newerwand = instance;

		this.configReloaded = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("config-reloaded"));

		this.noPermissions = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("no-permissions"));
		this.incorrectUsage = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("incorrect-usage"));

		this.givenwand = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("given-a-wand"));
		this.prefix = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("Message_Prefix"));

		this.full = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("Full-Inventory"));
		this.already = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("Already-Have-Wand"));

		this.HelpMenu = newerwand.getConfig().getMapList("Help_Menu");
		
		this.SelectionCancelled = newerwand.getConfig().getMapList("Selection_Cancelled");
		
		this.NoSelection = newerwand.getConfig().getMapList("No_Selection_To_Cancel");

		}
	
/*	public WandCommand(WandListener instance) {
		this.wandlis = instance;
		
	}*/

	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

		// Check if sender is a player
		if (commandSender instanceof Player) {
			p = Bukkit.getPlayer(commandSender.getName());
			pl = p.getName();
		}

		if (strings.length >= 1) {
			if (strings[0].equalsIgnoreCase("reload") && strings[1].isEmpty()) {

				if (commandSender.hasPermission("newerwand.admin")) {
					this.newerwand.reloadConfig();

					// commandSender.sendMessage(this.configReloaded);
				}

				else {
					commandSender.sendMessage(this.noPermissions);
				}
			}

			
			else if (strings[0].equalsIgnoreCase("about") && strings.length == 1) {
				p.sendMessage(prefix+ " NewerWand custom WorldEdit plugin. Created by Hockeymikey.");
			}

			
			else if (strings[0].equalsIgnoreCase("cancel") || strings[0].equalsIgnoreCase("c")) {
				
				if (commandSender instanceof Player) {
					if (commandSender.hasPermission("newerwand.use")) {
						
						if (OldPoints.containsKey(pl)) {
							
							for (Location bl : OldPoints.get(pl).values()) {
								p.sendBlockChange(bl, bl.getBlock().getType(), bl.getBlock().getData());
							}
							
							OldPoints.remove(pl);
							
							if (points.containsKey(pl)) {
								points.remove(pl);
							}
							
							Util(p, SelectionCancelled, "","","","");
						}
						
						else {
							Util(p, NoSelection, "","","","");
						}
						
					}
					
					else {
						p.sendMessage(noPermissions);
					}
				}
				
				else {
					commandSender.sendMessage(prefix+"No console support for this command.");
				}


				
			}
			
			else if (strings[0].equalsIgnoreCase("help") || strings[0].equalsIgnoreCase("h")) {

				
				if (commandSender.hasPermission("newerwand.use") && commandSender instanceof Player) {
					// Entire Menu

					for (Map<?, ?> map : HelpMenu) {

						TextComponent message = null;
						TextComponent line = new TextComponent("");
						int NumOfMessages = 0;

						// A single line
						for (Map.Entry<?, ?> entry : map.entrySet()) {

							if (Objects.toString(entry.getKey()).startsWith("message")) {

								if (message != null) {

									line.addExtra(message);

								}

								message = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', entry.getValue().toString())));

								NumOfMessages++;

							}

							else if (Objects.toString(entry.getKey()).startsWith("hover")) {

								String btext = ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava(Objects.toString(entry.getValue())));
								String trans = btext.replaceAll("\\n", "\n" + org.bukkit.ChatColor.getLastColors(btext));

								BaseComponent[] text = TextComponent.fromLegacyText(trans);

							}

							else if (Objects.toString(entry.getKey()).equals("suggest")) {
								message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, entry.getValue().toString()));
							}

							else if (Objects.toString(entry.getKey()).equals("website")) {
								message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, entry.getValue().toString()));
							}

							else if (Objects.toString(entry.getKey()).equals("run")) {
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, entry.getValue().toString()));
							}

						}

						if (NumOfMessages == 1 || map.size() == 1) {

							p.spigot().sendMessage(message);
						}

						else {
							line.addExtra(message);
							p.spigot().sendMessage(line);

						}

					}
				}

				else if (!(commandSender instanceof Player)) {
					commandSender.sendMessage("No support for Console help menu yet :/");
				}

				else {
					commandSender.sendMessage(this.noPermissions);
				}
			}

			else  {
				// bug check if console
				if (commandSender.hasPermission("newerwand.use") && commandSender instanceof Player) {
					// Entire Menu

					for (Map<?, ?> map : HelpMenu) {

						TextComponent message = null;
						TextComponent line = new TextComponent("");
						int NumOfMessages = 0;

						// A single line
						for (Map.Entry<?, ?> entry : map.entrySet()) {

							if (Objects.toString(entry.getKey()).startsWith("message")) {

								if (message != null) {

									line.addExtra(message);

								}

								message = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', entry.getValue().toString())));

								NumOfMessages++;

							}

							else if (Objects.toString(entry.getKey()).startsWith("hover")) {

								BaseComponent[] text = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Objects.toString(entry.getValue())));
								message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text));

							}

							else if (Objects.toString(entry.getKey()).equals("suggest")) {
								message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, entry.getValue().toString()));
							}

							else if (Objects.toString(entry.getKey()).equals("website")) {
								message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, entry.getValue().toString()));
							}

							else if (Objects.toString(entry.getKey()).equals("run")) {
								message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, entry.getValue().toString()));
							}

						}

						if (NumOfMessages == 1 || map.size() == 1) {

							p.spigot().sendMessage(message);
						}

						else {
							line.addExtra(message);
							p.spigot().sendMessage(line);

						}

					}
				}
				
				else if (!commandSender.hasPermission("newerwand.use")) {
					p.sendMessage(prefix+this.noPermissions);
				}
				
				else {
					commandSender.sendMessage(prefix+" Not supported by the console, but the command is wrong.");
				}
			}
		}

		else if ((commandSender instanceof Player)) {

			if (commandSender.hasPermission("newerwand.use")) {
				if (p.getInventory().firstEmpty() == -1) {
					commandSender.sendMessage(prefix + full);
				}

				else {
					Boolean HasWand = false;

					for (ItemStack item : p.getInventory().getContents()) {
						if (item != null && item.getType() == WandItem && item.getAmount() == 1 && item.getItemMeta().getDisplayName().equals(WandDisplayName) && item.getItemMeta().getLore().equals(WandLore)) {

							HasWand = true;
							commandSender.sendMessage(prefix + already);
							break;
						}
					}

					if (HasWand == false) {
						p.getInventory().setItem(p.getInventory().firstEmpty(), p.getInventory().getItem(0));
						ItemStack blazewand = new ItemStack(WandItem, 1);
						ItemMeta itemMeta = blazewand.getItemMeta();
						itemMeta.setDisplayName(WandDisplayName);
						itemMeta.setLore(WandLore);

						blazewand.setItemMeta(itemMeta);
						p.getInventory().setItem(0, blazewand);
						p.getInventory().setHeldItemSlot(0);
						commandSender.sendMessage(prefix + givenwand);
					}

				}
			} 
			
			else {
				commandSender.sendMessage(this.noPermissions);
			}
		}

		else {
			commandSender.sendMessage(ChatColor.DARK_RED + "You need to be a player to use this command!");
		}

		return false;

	}
	
	public void Util (Player p, List<Map<?,?>> config, String v1, String v2, String v3, String v4) {

		for (Map<?, ?> map : config) {

			TextComponent message = null;
			TextComponent line = null;
			int NumOfMessages = 0;

			// A single line
			for (Map.Entry<?, ?> entry : map.entrySet()) {

				if (Objects.toString(entry.getKey()).startsWith("message")) {

					if (line == null && message != null) {
						line = new TextComponent("");
						line.addExtra(message);

					}

					else if (line != null && message != null) {

						line.addExtra(message);

					}

					message = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', entry.getValue().toString().replaceAll("{1}", v1)
							.replaceAll("{2}", v2).replaceAll("{3}", v3).replaceAll("{4}", v4)  )));

					NumOfMessages++;

				}

				else if (Objects.toString(entry.getKey()).startsWith("hover")) {

					BaseComponent[] text = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Objects.toString(entry.getValue()).replaceAll("{1}", v1)
							.replaceAll("{2}", v2).replaceAll("{3}", v3).replaceAll("{4}", v4) ));
					message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text));

				}

				else if (Objects.toString(entry.getKey()).equals("suggest")) {
					message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, entry.getValue().toString().replaceAll("{1}", v1)
							.replaceAll("{2}", v2).replaceAll("{3}", v3).replaceAll("{4}", v4) ));
				}

				else if (Objects.toString(entry.getKey()).equals("website")) {
					message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, entry.getValue().toString().replaceAll("{1}", v1)
							.replaceAll("{2}", v2).replaceAll("{3}", v3).replaceAll("{4}", v4) ));
				}

				else if (Objects.toString(entry.getKey()).equals("run")) {
					message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, entry.getValue().toString().replaceAll("{1}", v1)
							.replaceAll("{2}", v2).replaceAll("{3}", v3).replaceAll("{4}", v4) ));
				}

			}

			if (NumOfMessages == 1 || map.size() == 1) {

				p.spigot().sendMessage(message);
			}

			else {
				line.addExtra(message);
				p.spigot().sendMessage(line);

			}

		}
	

	}
}

