package com.guppy.IncreasedSpawners;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class IncreasedSpawners extends JavaPlugin implements Listener, CommandExecutor {
   public int defaultrange;
   public int extrarange;
   public int extremerange;
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        if (!new File(getDataFolder().getPath() + "/config").exists()) {
			saveDefaultConfig();
			reloadConfig();
        }     
    }
    
@EventHandler(priority = EventPriority.HIGHEST)
public void onBlockPlaced(BlockPlaceEvent event)
{
    Block block = event.getBlockPlaced();
    defaultrange = getConfig().getInt("default-range");
    extrarange = getConfig().getInt("extra-range");
    extremerange = getConfig().getInt("extreme-range");
    if(block.getType() == Material.SPAWNER && (event.getPlayer().hasPermission("increasedspawners.default") || !getConfig().getBoolean("default-range-requires-permission")))
    {
        CreatureSpawner spawner = (CreatureSpawner)block.getState();
        spawner.setRequiredPlayerRange(defaultrange);
        spawner.setMetadata("PLAYER", new FixedMetadataValue(this, block));
        spawner.update();
        }
        if(block.getType() == Material.SPAWNER && event.getPlayer().hasPermission("increasedspawners.extra"))
        {
        CreatureSpawner spawner = (CreatureSpawner)block.getState();
        spawner.setRequiredPlayerRange(extrarange);
        spawner.setMetadata("PLAYER", new FixedMetadataValue(this, block));
        spawner.update();
        }
        if(block.getType() == Material.SPAWNER && event.getPlayer().hasPermission("increasedspawners.extreme"))
        {
        CreatureSpawner spawner = (CreatureSpawner)block.getState();
        spawner.setRequiredPlayerRange(extremerange);
        spawner.setMetadata("PLAYER", new FixedMetadataValue(this, block));
        spawner.update();
        }
      }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent event){
        World w = event.getWorld();
        Chunk c = event.getChunk();
        Player p = (Player) event;
        int cx = c.getX(); // chunks x
        int cz = c.getZ(); // chunks z
       BukkitTask runTask = new BukkitRunnable() {
           @Override
           public void run() { // Check all 16 blocks of that chunk
               for (int x = cx; x < cx + 16; x++) { // x coords
                   for (int z = cz; z < cz + 16; z++) { // z coords
                       for (int y = 0; y < 256; y++) { // y coords
                           Block b = new Location(w, x, y, z).getBlock(); // Get any block in that chunk
                           if(b.getType() == Material.SPAWNER && b.getMetadata("PLAYER").equals(true) && !p.hasPermission("increasedspawners.extra")) { // Check if it has my custom lore and if the player isnt a donator
                               p.sendMessage("You have entered an area with a donator's spawner, you will not be able to activate it while your here."); // Tell player to piss off
                               // Here is where im trying to cancel the mobs from spawning if the above is true.
                               ;
                           }
                       }
                   }
               }
           }
       }.runTask(this);
                }
}
