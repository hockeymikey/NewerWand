package mn.hockeymikey.newerwand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;

public class NewerListener implements Listener {



	String prefix = "";

	List<Map<?, ?>> RightPointMessage;

	List<Map<?, ?>> LeftPointMessage;
	
	String WandRemoved = "";
	

	Material LeftPointBlock = NewerWand.LeftPointBlock;
	byte LeftPBData = NewerWand.LeftPBData;

	Material RightPointBlock = NewerWand.RightPointBlock;
	byte RightPBData = NewerWand.RightPBData;
	
	HashMap<String, HashMap<String, Location>> points = NewerWand.points;
	HashMap<String, HashMap<String, Location>> OldPoints = NewerWand.OldPoints;

	Player p;
	
	String pl;
	
	Location click;
	
	Boolean Survival_Wand;

	String WandDisplayName = NewerWand.WandDisplayName;
	
	List<String> WandLore = NewerWand.WandLore;

	Material WandItem = NewerWand.WandItem;


	NewerWand newerwand;
	
	WorldEditPlugin we = NewerWand.getWorldEdit();
	

	public NewerListener(NewerWand instance) {
		this.newerwand = instance;
		
		if (newerwand.getConfig().contains("Message_Prefix")) {
			this.prefix = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("Message_Prefix"));
		}
		
		else {
			this.prefix = "";
		}
		
		if (newerwand.getConfig().contains("Wand_Removed_Message")) {
			this.WandRemoved = ChatColor.translateAlternateColorCodes('&', this.newerwand.getConfig().getString("Wand_Removed_Message"));
		}
		
		else {
			this.WandRemoved = "";
		}
		
		
		// Right point messages
		if (newerwand.getConfig().contains("Right_Point_Message")) {
			this.RightPointMessage = newerwand.getConfig().getMapList("Right_Point_Message");
		}
		
		else {
			Map<String, String> test = new HashMap<>();
			test.put("message", "testing me out");
			
			this.RightPointMessage = new ArrayList<Map<?, ?>>();
			
			RightPointMessage.add(test);
		}
		
		// Left Point Messages
		if (newerwand.getConfig().contains("Left_Point_Message")) {
			this.LeftPointMessage = newerwand.getConfig().getMapList("Left_Point_Message");
		}
		
		else {

			
			Map<String, String> test = new HashMap<>();
			test.put("message", "testing me out");
			
			this.LeftPointMessage = new ArrayList<Map<?, ?>>();
			
			this.LeftPointMessage.add(test);

		}
		
		if (newerwand.getConfig().contains("Survival_Wand")) {
			this.Survival_Wand = newerwand.getConfig().getBoolean("Survival_Wand");
		}
		
		else {
			this.Survival_Wand = false;
		}
		
	}
	
	public void RevertBlocks(Player p) {
		
		if (OldPoints.containsKey(pl)) {
			for (Location bl : OldPoints.get(pl).values()) {
				p.sendBlockChange(bl, bl.getBlock().getType(), bl
						.getBlock().getData());
			}
		}
		
		if (points.containsKey(pl)) {
			points.remove(pl);
		}
		
		if (OldPoints.containsKey(pl)) {
			OldPoints.remove(pl);
		}
		
	}
	
	@EventHandler
	public void LeaveClearHash(PlayerQuitEvent e) {
		String p = e.getPlayer().getName();
		
		if (points.containsKey(pl)) {
			points.remove(pl);
		}
		
		if (OldPoints.containsKey(pl)) {
			OldPoints.remove(pl);
		}
		
		
	}
	
	// Cancel wand dragging

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChestDrag(InventoryDragEvent event) {

		if (Survival_Wand == true &&
				event.getOldCursor().getType().equals(WandItem)
				&& event.getOldCursor().getItemMeta().getLore()
						.equals(WandLore)
				&& event.getOldCursor().getItemMeta().getDisplayName()
						.equals(WandDisplayName)
				&& event.getInventory().getType() != null
				&& (event.getInventory().getType() == InventoryType.CHEST
						|| event.getInventory().getType() == InventoryType.FURNACE
						|| event.getInventory().getType() == InventoryType.ENDER_CHEST
						|| event.getInventory().getType() == InventoryType.BREWING
						|| event.getInventory().getType() == InventoryType.WORKBENCH
						|| event.getInventory().getType() == InventoryType.DISPENSER
						|| event.getInventory().getType() == InventoryType.DROPPER
						|| event.getInventory().getType() == InventoryType.CREATIVE
						|| event.getInventory().getType() == InventoryType.ANVIL
						|| event.getInventory().getType() == InventoryType.ENCHANTING
						|| event.getInventory().getType() == InventoryType.MERCHANT
						|| event.getInventory().getType() == InventoryType.HOPPER
						|| event.getInventory().getType() == InventoryType.FURNACE
						|| event.getInventory().getType() == InventoryType.CRAFTING || event
						.getInventory().getType() == InventoryType.PLAYER)) {

			event.setCancelled(true);
		}
	}

	// Remove wand on moving to chest/inventory

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChestMove(InventoryClickEvent event) {

		if (Survival_Wand == true &&
				event.getCursor().getType().equals(WandItem)
				&& event.getCursor().getItemMeta().getDisplayName()
						.equals(WandDisplayName)
				&& event.getCursor().getItemMeta().getLore().equals(WandLore)
				&& event.getClickedInventory() != null
				&& (event.getClickedInventory().getType() == InventoryType.CHEST
						|| event.getClickedInventory().getType() == InventoryType.FURNACE
						|| event.getClickedInventory().getType() == InventoryType.ENDER_CHEST
						|| event.getClickedInventory().getType() == InventoryType.BREWING
						|| event.getClickedInventory().getType() == InventoryType.WORKBENCH
						|| event.getClickedInventory().getType() == InventoryType.DISPENSER
						|| event.getClickedInventory().getType() == InventoryType.DROPPER
						|| event.getClickedInventory().getType() == InventoryType.CREATIVE
						|| event.getClickedInventory().getType() == InventoryType.ANVIL
						|| event.getClickedInventory().getType() == InventoryType.ENCHANTING
						|| event.getClickedInventory().getType() == InventoryType.MERCHANT
						|| event.getClickedInventory().getType() == InventoryType.HOPPER
						|| event.getClickedInventory().getType() == InventoryType.CRAFTING || event
						.getClickedInventory().getType() == InventoryType.FURNACE)) {

			event.setCancelled(true);
		}

		if (Survival_Wand == true &&
				event.isShiftClick()
				&& event.getCurrentItem().getType().equals(WandItem)
				&& event.getCurrentItem().getItemMeta().getDisplayName()
						.equals(WandDisplayName)
				&& event.getCurrentItem().getItemMeta().getLore()
						.equals(WandLore)
				&& event.getInventory().getType() != null
				&& (event.getInventory().getType() == InventoryType.CHEST
						|| event.getInventory().getType() == InventoryType.FURNACE
						|| event.getInventory().getType() == InventoryType.ENDER_CHEST
						|| event.getInventory().getType() == InventoryType.BREWING
						|| event.getInventory().getType() == InventoryType.WORKBENCH
						|| event.getInventory().getType() == InventoryType.DISPENSER
						|| event.getInventory().getType() == InventoryType.DROPPER
						|| event.getInventory().getType() == InventoryType.CREATIVE
						|| event.getInventory().getType() == InventoryType.ANVIL
						|| event.getInventory().getType() == InventoryType.ENCHANTING
						|| event.getInventory().getType() == InventoryType.MERCHANT
						|| event.getInventory().getType() == InventoryType.HOPPER
						|| event.getInventory().getType() == InventoryType.CRAFTING || event
						.getInventory().getType() == InventoryType.FURNACE)) {

			event.setCancelled(true);
		}

	}

	// Remove wand on drop

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDrop(PlayerDropItemEvent e) {

		if (Survival_Wand == true && 
				e.getItemDrop().getItemStack().getType().equals(WandItem)
				&& e.getItemDrop().getItemStack().getItemMeta()
						.getDisplayName().equals(WandDisplayName)
				&& e.getItemDrop().getItemStack().getItemMeta().getLore()
						.equals(WandLore)) {

			e.getItemDrop().remove();
			e.getPlayer().sendMessage(prefix + WandRemoved);

			RevertBlocks(e.getPlayer());
		}
	}

	// Remove wand on death

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		
		if (Survival_Wand == true) {

			ArrayList<ItemStack> remove = new ArrayList<>();

			for (ItemStack drops : e.getDrops()) {
				if (drops.getType().equals(WandItem)
						&& drops.hasItemMeta()
						&& drops.getItemMeta().getLore().equals(WandLore)
						&& drops.getItemMeta().getDisplayName()
								.equals(WandDisplayName)) {
					remove.add(drops);
				}
			}

			for (ItemStack is : remove) {
				e.getDrops().remove(is);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void WandClick(PlayerInteractEvent e) {

		if ((e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			
			

			Player p = (Player) e.getPlayer();
			String pl = e.getPlayer().getName();
			
			if (!points.containsKey(pl)) {
				points.put(pl, new HashMap<String, Location>() );
			}

			
			
			/** Start of math stuff **/
				
				Block block = e.getClickedBlock();

				click = block.getLocation();
				
				
				if (OldPoints.containsKey(pl)) {
					for (Map.Entry<String, Location> ds: OldPoints.get(pl).entrySet()) {
						if (ds.getValue().equals(click)) {
							
							e.setCancelled(true);
							
							Material bl = null;
							Byte bld = null;
							
							if (ds.getKey().equals("left")) {
								bl = LeftPointBlock;
								bld = LeftPBData;
							}
							
							else if (ds.getKey().equals("right")) {
								bl = RightPointBlock;
								bld = RightPBData;
							}

							
							final Player fp= p;
							final Material fbl = bl;
							final Byte fbld = bld;
							final Location fds = ds.getValue();
							
							Bukkit.getScheduler().scheduleSyncRepeatingTask(
									NewerWand.getPlugin(), new Runnable() {
										public void run() {
											fp.sendBlockChange(fds, fbl, fbld);
										}
									}, 4, -1);
							break;
						}
					}
				}


				if (p.getItemInHand().hasItemMeta() 
					&& p.getItemInHand().getItemMeta().hasDisplayName() 
					&& (p.getItemInHand().getItemMeta().getDisplayName().equals(WandDisplayName)) 
					&& (p.getItemInHand().getItemMeta().getLore().equals(WandLore)) 
					&& p.getItemInHand().getType().equals(WandItem)) {
					

					if (e.getAction() == Action.LEFT_CLICK_BLOCK
							&& 
							(!points.get(pl).containsKey("left") ||
							 !block.getLocation().equals(points.get(pl).get("left")) ) ) {
						


						if (points.get(pl).containsKey("left")) {
							points.get(pl).remove("left");
							
						}
						
						points.get(pl).put("left", block.getLocation() );
						
						//Region work
						if (!points.get(pl).containsKey("right")) {
							CuboidSelection cuboid = new CuboidSelection(
									p.getWorld(), points.get(pl).get("left"), points.get(pl).get("left") );
							we.setSelection(p, cuboid);
						} else {
							CuboidSelection cuboid = new CuboidSelection(
									p.getWorld(), points.get(pl).get("left"), points.get(pl).get("right"));
							we.setSelection(p, cuboid);

						}
						
						e.setCancelled(true);

						//Send message
						Util(p, LeftPointMessage, "", "", "", "");
						
					}
					
					
					else if (e.getAction() == Action.RIGHT_CLICK_BLOCK
							&& 
							(!points.get(pl).containsKey("right") ||
							 !block.getLocation().equals(points.get(pl).get("right")) ) ) {
						
						
						if (points.get(pl).containsKey("right")) {
							points.get(pl).remove("right");
						}
						
						points.get(pl).put("right", block.getLocation() );

						//If left is null
						if (!points.get(pl).containsKey("left")) {
							CuboidSelection cuboid = new CuboidSelection(p.getWorld(), points.get(pl).get("right"), points.get(pl).get("right"));
							we.setSelection(p, cuboid);

						} else {
							CuboidSelection cuboid = new CuboidSelection(p.getWorld(), points.get(pl).get("left"), points.get(pl).get("right"));
							we.setSelection(p, cuboid);

						}
						e.setCancelled(true);

						//Send message
						
						Util(p, RightPointMessage, "", "", "", "");

					}
				}
				
				
				/** Reverts Fake Blocks if the point changes **/
				if (!OldPoints.equals(points) && OldPoints.containsKey(pl)) {
					for (Map.Entry<String, Location> ds: OldPoints.get(pl).entrySet()) {
						Location dsf = ds.getValue();
						p.sendBlockChange(dsf, dsf.getBlock().getType(), dsf.getBlock().getData());
						
						final Player fp= p;
						final Material fbl = dsf.getBlock().getType();
						final Byte fbld = dsf.getBlock().getData();
						final Location fds = dsf;

						Bukkit.getScheduler().scheduleSyncRepeatingTask(
								NewerWand.getPlugin(), new Runnable() {
									public void run() {
										fp.sendBlockChange(fds, fbl, fbld);
									}
								}, 3, -1);
					
				      }
				}

				/** Send new blocks! **/

				if (!points.get(pl).isEmpty()) {

					for (Map.Entry<String, Location> ds: points.get(pl).entrySet()) {
						
							Material bl = null;
							Byte bld = null;
							
							if (ds.getKey() == "left") {
								bl = LeftPointBlock;
								bld = LeftPBData;
							}
							
							else if (ds.getKey() == "right") {
								bl = RightPointBlock;
								bld = RightPBData;
							}
							
							
							final Player fp= p;
							final Material fbl = bl;
							final Byte fbld = bld;
							final Location fds = ds.getValue();

							Bukkit.getScheduler().scheduleSyncRepeatingTask(
									NewerWand.getPlugin(), new Runnable() {
										public void run() {
											fp.sendBlockChange(fds, fbl, fbld);
										}
									}, 4, -1);
							
						
					}
				}
				
				if (!OldPoints.containsKey(pl)) {
					OldPoints.put(pl, new HashMap<String, Location>());
				}
				
				OldPoints.get(pl).clear();
				OldPoints.get(pl).putAll(points.get(pl));
				
				

		}
		
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