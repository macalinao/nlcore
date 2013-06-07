/*     */ package net.nl.ecp;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.command.PluginCommand;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.inventory.Inventory;
/*     */ import org.bukkit.inventory.ItemStack;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public class EnderChestProtect extends JavaPlugin
/*     */ {
/*  20 */   private Map<String, Location> selectedChest = new HashMap();
/*  21 */   public Map<String, ArrayList<Location>> chestLocations = new HashMap();
/*  22 */   public Map<String, Long> clearChests = new HashMap();
/*  23 */   public HashMap<String, Long> cooldowns = new HashMap();
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*  28 */     getCommand("enderchest").setExecutor(new Commands(this));
/*  29 */     getServer().getPluginManager().registerEvents(new ChestListener(this), this);
/*  30 */     loadChestLocations();
/*  31 */     getLogger().info("has been enabled");
/*     */   }
/*     */ 
/*     */   public void onDisable() {
/*  35 */     getLogger().info("has been disabled");
/*     */   }
/*     */ 
/*     */   public void loadChestLocations() {
/*  39 */     if (!getDataFolder().exists()) return;
/*  40 */     for (File file : getDataFolder().listFiles()) {
/*  41 */       FileConfiguration chestConfig = YamlConfiguration.loadConfiguration(file);
/*  42 */       World world = getServer().getWorld(file.getName().substring(file.getName().indexOf("CraftWorld{name=") + 16, file.getName().indexOf("},x=")));
/*  43 */       int x = Integer.parseInt(file.getName().substring(file.getName().indexOf("},x=") + 4, file.getName().indexOf(".0,y=")));
/*  44 */       int y = Integer.parseInt(file.getName().substring(file.getName().indexOf(",y=") + 3, file.getName().indexOf(".0,z=")));
/*  45 */       int z = Integer.parseInt(file.getName().substring(file.getName().indexOf(",z=") + 3, file.getName().indexOf(".0,pitch=")));
/*  46 */       if (chestConfig.getString("owner") != null)
/*  47 */         addChestLocation(chestConfig.getString("owner"), new Location(world, x, y, z));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSelectedChest(Player p, Location loc)
/*     */   {
/*  58 */     selectedChest.put(p.getName(), loc);
/*     */   }
/*     */ 
/*     */   public Location getSelectedChest(String playerName)
/*     */   {
/*  65 */     if (selectedChest.containsKey(playerName)) return (Location)selectedChest.get(playerName);
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public void addChestLocation(String p, Location loc)
/*     */   {
/*  73 */     if (chestLocations.containsKey(p)) {
/*  74 */       ((ArrayList)chestLocations.get(p)).add(loc);
/*     */     } else {
/*  76 */       chestLocations.put(p, new ArrayList());
/*  77 */       ((ArrayList)chestLocations.get(p)).add(loc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeChestLocation(String p, Location loc)
/*     */   {
/*  85 */     ((ArrayList)chestLocations.get(p)).remove(((ArrayList)chestLocations.get(p)).indexOf(loc));
/*     */   }
/*     */ 
/*     */   public int getChestCount(Player p)
/*     */   {
/*  92 */     if (chestLocations.containsKey(p.getName())) return ((ArrayList)chestLocations.get(p.getName())).size();
/*  93 */     return 0;
/*     */   }
/*     */ 
/*     */   public void saveChestFile(Location loc, Inventory inv, String playerName)
/*     */   {
/* 100 */     File file = new File(getDataFolder(), loc.toString() + ".yml");
/* 101 */     FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);
/*     */ 
/* 104 */     if (!file.exists()) try {
/* 105 */         file.createNewFile();
/*     */       } catch (IOException e) {
/* 107 */         getLogger().severe("Could not create a new chest file for chest at " + loc.toString());
/*     */       }
/*     */ 
/*     */ 
/* 111 */     if (inv != null) {
/* 112 */       int slot = -1;
/* 113 */       chestFile.set("inventory", null);
/* 114 */       for (ItemStack stack : inv.getContents()) {
/* 115 */         slot++;
/* 116 */         if (stack != null) chestFile.set("inventory." + slot, stack);
/*     */       }
/*     */     }
/*     */ 
/* 120 */     if (getOwner(loc) == null) chestFile.set("owner", playerName);
/*     */ 
/*     */     try
/*     */     {
/* 124 */       chestFile.save(file);
/*     */     } catch (IOException e) {
/* 126 */       getLogger().severe("Could not save chest file for chest at " + loc.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Inventory loadChestData(Location loc, Inventory inv, String playerName)
/*     */   {
/* 134 */     File file = new File(getDataFolder(), loc.toString() + ".yml");
/* 135 */     FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);
/*     */ 
/* 138 */     if (!file.exists()) saveChestFile(loc, inv, playerName);
/* 141 */     else if (chestFile.getConfigurationSection("inventory") != null) {
/* 142 */       for (int i = -1; i <= inv.getSize(); i++) {
/* 143 */         if (chestFile.get("inventory." + i) != null) inv.setItem(i, (ItemStack)chestFile.get("inventory." + i));
/*     */       }
/*     */     }
/* 146 */     return inv;
/*     */   }
/*     */ 
/*     */   public String getOwner(Location loc)
/*     */   {
/* 153 */     File file = new File(getDataFolder(), loc.toString() + ".yml");
/* 154 */     FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);
/*     */ 
/* 156 */     if (!file.exists()) return null;
/* 157 */     return chestFile.getString("owner");
/*     */   }
/*     */ 
/*     */   public int getAllowedChestCount(Player p)
/*     */   {
/* 164 */     for (int i = 15; i > 0; i--) {
/* 165 */       if (p.hasPermission("nlenderchest.place." + i)) return i;
/*     */     }
/* 167 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean canPlaceChest(Player p)
/*     */   {
/* 176 */     if (getAllowedChestCount(p) == -1) {
/* 177 */       p.sendMessage(ChatColor.RED + "You are not allowed to place EnderChests");
/* 178 */       return false;
/*     */     }
/*     */ 
/* 181 */     if (getChestCount(p) >= getAllowedChestCount(p)) {
/* 182 */       p.sendMessage(ChatColor.RED + "You have placed your maximum number of Protected EnderChests!");
/* 183 */       return false;
/*     */     }
/* 185 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean canBreakChest(Player p, Location loc)
/*     */   {
/* 192 */     File file = new File(getDataFolder(), loc.toString() + ".yml");
/* 193 */     FileConfiguration chestFile = YamlConfiguration.loadConfiguration(file);
/*     */ 
/* 195 */     if (getOwner(loc) == null) return true;
/*     */ 
/* 198 */     if ((!getOwner(loc).equals(p.getName())) && (!p.hasPermission("nlenderchest.admin"))) {
/* 199 */       p.sendMessage(ChatColor.BLUE + "This is not your Protected EnderChest. It belongs to " + ChatColor.GOLD + getOwner(loc));
/* 200 */       return false;
/*     */     }
/*     */ 
/* 204 */     if (chestFile.getConfigurationSection("inventory") != null) {
/* 205 */       p.sendMessage(ChatColor.RED + "You cannot break this chest while there are items in it!");
/* 206 */       return false;
/*     */     }
/*     */ 
/* 209 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean canOpenChest(Player p, Location loc)
/*     */   {
/* 218 */     if (getOwner(loc) == null) {
/* 219 */       p.sendMessage(ChatColor.RED + "This EnderChest belongs to no one, and cannot be opened.");
/* 220 */       return false;
/*     */     }
/*     */ 
/* 224 */     if ((!getOwner(loc).equals(p.getName())) && (!p.hasPermission("nlenderchest.admin"))) {
/* 225 */       p.sendMessage(ChatColor.BLUE + "You cannot use this EnderChest, it belongs to " + ChatColor.GOLD + getOwner(loc));
/* 226 */       return false;
/*     */     }
/*     */ 
/* 230 */     if (p.hasPermission("nlenderchest.admin")) {
/* 231 */       p.sendMessage(ChatColor.BLUE + "This EnderChest belongs to " + getOwner(loc));
/*     */     }
/*     */ 
/* 234 */     return true;
/*     */   }
/*     */ }

/* Location:           /Users/simplyianm/Desktop/EnderChestProtect.jar
 * Qualified Name:     net.nl.ecp.EnderChestProtect
 * JD-Core Version:    0.6.2
 */