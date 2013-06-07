/*     */ package net.nl.ecp;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.HumanEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.block.Action;
/*     */ import org.bukkit.event.block.BlockBreakEvent;
/*     */ import org.bukkit.event.block.BlockPlaceEvent;
/*     */ import org.bukkit.event.inventory.InventoryClickEvent;
/*     */ import org.bukkit.event.inventory.InventoryCloseEvent;
/*     */ import org.bukkit.event.player.PlayerInteractEvent;
/*     */ import org.bukkit.event.player.PlayerTeleportEvent;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.InventoryView;
/*     */ 
/*     */ public class ChestListener
/*     */   implements Listener
/*     */ {
/*     */   private EnderChestProtect plugin;
/*     */ 
/*     */   public ChestListener(EnderChestProtect plugin)
/*     */   {
/*  28 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGHEST)
/*     */   public void onChestPlace(BlockPlaceEvent e) {
/*  33 */     if (e.getBlock().getType() != Material.ENDER_CHEST) return;
/*  34 */     if (e.isCancelled()) return;
/*  35 */     if (!plugin.canPlaceChest(e.getPlayer())) {
/*  36 */       e.setCancelled(true);
/*  37 */       return;
/*     */     }
/*  39 */     plugin.addChestLocation(e.getPlayer().getName(), e.getBlock().getLocation());
/*  40 */     e.getPlayer().sendMessage(ChatColor.BLUE + "You have placed " + plugin.getChestCount(e.getPlayer()) + "/" + plugin.getAllowedChestCount(e.getPlayer()) + " Protected EnderChests");
/*  41 */     plugin.saveChestFile(e.getBlock().getLocation(), null, e.getPlayer().getName());
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGHEST)
/*     */   public void onChestBreak(BlockBreakEvent e)
/*     */   {
/*  47 */     if (e.getBlock().getType() != Material.ENDER_CHEST) return;
/*  48 */     if ((e.isCancelled()) && (!plugin.getOwner(e.getBlock().getLocation()).equalsIgnoreCase(e.getPlayer().getName()))) return;
/*  49 */     if (!plugin.canBreakChest(e.getPlayer(), e.getBlock().getLocation())) {
/*  50 */       e.setCancelled(true);
/*  51 */       return;
/*     */     }
/*     */ 
/*  54 */     if (plugin.getOwner(e.getBlock().getLocation()) == null) {
/*  55 */       e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken a ProtectedEnderChest belonging to " + ChatColor.GOLD + "nobody");
/*  56 */       e.setCancelled(true);
/*  57 */       e.getBlock().setType(Material.AIR);
/*  58 */       File file = new File(plugin.getDataFolder(), e.getBlock().getLocation().toString() + ".yml");
/*  59 */       if (file.exists()) file.delete();
/*  60 */       return;
/*     */     }
/*  62 */     if (e.getPlayer().hasPermission("nlenderchest.admin"))
/*  63 */       e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken a ProtectedEnderChest belonging to " + ChatColor.GOLD + plugin.getOwner(e.getBlock().getLocation()));
/*  64 */     else e.getPlayer().sendMessage(ChatColor.BLUE + "You have broken your Protected EnderChest");
/*     */ 
/*  66 */     plugin.removeChestLocation(plugin.getOwner(e.getBlock().getLocation()), e.getBlock().getLocation());
/*  67 */     File file = new File(plugin.getDataFolder(), e.getBlock().getLocation().toString() + ".yml");
/*  68 */     file.delete();
/*  69 */     e.setCancelled(true);
/*  70 */     e.getBlock().setType(Material.AIR);
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.MONITOR)
/*     */   public void onChestOpen(PlayerInteractEvent e) {
/*  75 */     if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
/*  76 */     Block block = e.getClickedBlock();
/*  77 */     if (block.getType() != Material.ENDER_CHEST) return;
/*  78 */     e.setCancelled(true);
/*     */ 
/*  81 */     int cooldown = 2000;
/*  82 */     if ((plugin.cooldowns.containsKey(e.getPlayer().getName())) && (((Long)plugin.cooldowns.get(e.getPlayer().getName())).longValue() + cooldown - System.currentTimeMillis() > 0L)) return;
/*  83 */     plugin.cooldowns.put(e.getPlayer().getName(), Long.valueOf(System.currentTimeMillis()));
/*     */ 
/*  85 */     File file = new File(plugin.getDataFolder(), block.getLocation().toString() + ".yml");
/*     */ 
/*  87 */     if (file.exists()) {
/*  88 */       if (!plugin.canOpenChest(e.getPlayer(), block.getLocation())) return;
/*  89 */       openChestInventory(block.getLocation(), e.getPlayer());
/*     */     }
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onChestClose(InventoryCloseEvent e) {
/*  95 */     if (!e.getInventory().getTitle().equals("ProtectedEnderChest")) return;
/*  96 */     plugin.saveChestFile(plugin.getSelectedChest(e.getPlayer().getName()), e.getInventory(), e.getPlayer().getName());
/*     */   }
/*     */ 
/*     */   @EventHandler
/*     */   public void onChestInventoryClick(InventoryClickEvent e) {
/* 101 */     if (!e.getInventory().getTitle().equals("ProtectedEnderChest")) return;
/* 102 */     plugin.saveChestFile(plugin.getSelectedChest(e.getWhoClicked().getName()), e.getInventory(), e.getWhoClicked().getName());
/*     */   }
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGHEST)
/*     */   public void onTeleport(PlayerTeleportEvent e) {
/* 107 */     if (e.getPlayer().getInventory() == null) return;
/* 108 */     if (e.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase("ProtectedEnderChest")) plugin.saveChestFile(plugin.getSelectedChest(e.getPlayer().getName()), e.getPlayer().getOpenInventory().getTopInventory(), e.getPlayer().getName());
/* 109 */     e.getPlayer().closeInventory();
/*     */   }
/*     */ 
/*     */   public void openChestInventory(Location loc, Player p)
/*     */   {
/* 115 */     plugin.setSelectedChest(p, loc);
/*     */ 
/* 118 */     Inventory inventory = Bukkit.createInventory(p, 27, "ProtectedEnderChest");
/* 119 */     inventory = plugin.loadChestData(loc, inventory, p.getName());
/* 120 */     p.openInventory(inventory);
/*     */   }
/*     */ }

/* Location:           /Users/simplyianm/Desktop/EnderChestProtect.jar
 * Qualified Name:     net.nl.ecp.ChestListener
 * JD-Core Version:    0.6.2
 */