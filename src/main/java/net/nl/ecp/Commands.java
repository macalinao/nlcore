/*    */ package net.nl.ecp;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandExecutor;
/*    */ import org.bukkit.command.CommandSender;
/*    */ 
/*    */ public class Commands
/*    */   implements CommandExecutor
/*    */ {
/*    */   private EnderChestProtect plugin;
/*    */ 
/*    */   public Commands(EnderChestProtect plugin)
/*    */   {
/* 16 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*    */   {
/* 21 */     if (args.length == 0) {
/* 22 */       sender.sendMessage(ChatColor.BLUE + "Invalid arguments! " + ChatColor.RED + "/enderchest [list/clear]");
/* 23 */       return true;
/*    */     }
/*    */     Object localObject1;
/* 24 */     if (args.length == 1)
/*    */     {
/*    */       Location loc;
/* 25 */       if (args[0].equalsIgnoreCase("list")) {
/* 26 */         if ((plugin.chestLocations.get(sender.getName()) != null) && (!((ArrayList)plugin.chestLocations.get(sender.getName())).isEmpty())) {
/* 27 */           int i = 0;
/* 28 */           sender.sendMessage(ChatColor.BLUE + "Here are the locations of your Protected EnderChests:");
/* 29 */           sender.sendMessage(ChatColor.BLUE + "Chests in " + ChatColor.RED + "red " + ChatColor.BLUE + "are in the Nether");
/* 30 */           for (Iterator localIterator = ((ArrayList)plugin.chestLocations.get(sender.getName())).iterator(); localIterator.hasNext(); ) { loc = (Location)localIterator.next();
/* 31 */             i++;
/* 32 */             if (loc.getWorld().getName().equalsIgnoreCase("world_nether"))
/* 33 */               sender.sendMessage(ChatColor.RED + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());
/* 34 */             else sender.sendMessage(ChatColor.BLUE + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());  } 
/*    */         }
/*    */         else {
/* 36 */           sender.sendMessage(ChatColor.RED + "You do not have any Protected EnderChests!");
/*    */         } } else if (args[0].equalsIgnoreCase("clear")) {
/* 38 */         if ((plugin.chestLocations.get(sender.getName()) != null) && (!((ArrayList)plugin.chestLocations.get(sender.getName())).isEmpty())) {
/* 39 */           sender.sendMessage(ChatColor.BLUE + "This will remove and clear any Protected EnderChests you have! This process is not reversable! If you want to do this, type " + ChatColor.GOLD + "/enderchest confirm");
/* 40 */           sender.sendMessage(ChatColor.BLUE + "This option will only be available for the next 30 seconds");
/* 41 */           plugin.clearChests.put(sender.getName(), Long.valueOf(System.currentTimeMillis())); } else {
/* 42 */           sender.sendMessage(ChatColor.RED + "You do not have any Protected EnderChests!");
/*    */         } } else if (args[0].equalsIgnoreCase("confirm")) {
/* 44 */         if (plugin.clearChests.containsKey(sender.getName())) {
/* 45 */           if (System.currentTimeMillis() - ((Long)plugin.clearChests.get(sender.getName())).longValue() <= 30000L) {
/* 46 */             Location localLocation1 = (localObject1 = ((ArrayList)plugin.chestLocations.get(sender.getName())).toArray()).length; for (loc = 0; loc < localLocation1; loc++) { Object loc = localObject1[loc];
/* 47 */               if ((loc instanceof Location)) {
/* 48 */                 File f = new File(plugin.getDataFolder(), loc.toString() + ".yml");
/* 49 */                 ((Location)loc).getBlock().setType(Material.AIR);
/* 50 */                 plugin.removeChestLocation(sender.getName(), (Location)loc);
/* 51 */                 f.delete();
/*    */               }
/*    */             }
/* 54 */             sender.sendMessage(ChatColor.BLUE + "Your Protected EnderChests have been successfully cleared");
/* 55 */             plugin.clearChests.remove(sender.getName());
/*    */           } else {
/* 57 */             sender.sendMessage(ChatColor.RED + "Your prompt has timed out. Type /enderchest clear to try again!");
/* 58 */             plugin.clearChests.remove(sender.getName());
/*    */           }
/*    */         } else sender.sendMessage(ChatColor.RED + "You have nothing to confirm!"); 
/*    */       } else sender.sendMessage(ChatColor.BLUE + "Invalid arguments! " + ChatColor.RED + "/enderchest [list/clear]"); 
/*    */     }
/* 62 */     else if (args.length == 2) {
/* 63 */       if ((args[0].equalsIgnoreCase("list")) && (sender.hasPermission("nlenderchest.admin"))) {
/* 64 */         String playerName = args[1];
/* 65 */         if ((plugin.chestLocations.get(playerName) != null) && (!((ArrayList)plugin.chestLocations.get(playerName)).isEmpty())) {
/* 66 */           int i = 0;
/* 67 */           sender.sendMessage(ChatColor.BLUE + "Here are the locations of " + ChatColor.GOLD + playerName + "'s " + ChatColor.BLUE + "Protected EnderChests:");
/* 68 */           sender.sendMessage(ChatColor.BLUE + "Chests in " + ChatColor.RED + "red " + ChatColor.BLUE + "are in the Nether");
/* 69 */           for (localObject1 = ((ArrayList)plugin.chestLocations.get(playerName)).iterator(); ((Iterator)localObject1).hasNext(); ) { Location loc = (Location)((Iterator)localObject1).next();
/* 70 */             i++;
/* 71 */             if (loc.getWorld().getName().equalsIgnoreCase("world_nether"))
/* 72 */               sender.sendMessage(ChatColor.RED + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ());
/*    */             else
/* 74 */               sender.sendMessage(ChatColor.BLUE + i + ". x = " + loc.getX() + ", y = " + loc.getY() + ", z = " + loc.getZ()); }
/*    */         }
/*    */         else {
/* 77 */           sender.sendMessage(ChatColor.RED + "That player has no Protected EnderChests! Warning, case sensitive!");
/*    */         } } else { sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!"); } 
/* 79 */     }return true;
/*    */   }
/*    */ }

/* Location:           /Users/simplyianm/Desktop/EnderChestProtect.jar
 * Qualified Name:     net.nl.ecp.Commands
 * JD-Core Version:    0.6.2
 */