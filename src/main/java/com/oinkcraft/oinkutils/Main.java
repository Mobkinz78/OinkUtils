package com.oinkcraft.oinkutils;

import com.oinkcraft.oinkutils.chatcolorchange.ChatColorChangeListener;
import com.oinkcraft.oinkutils.chatcolorchange.ColorChangeCommand;
import com.oinkcraft.oinkutils.modtools.clockbreaker.ClockBreakInventoryMoveListener;
import com.oinkcraft.oinkutils.modtools.clockbreaker.ClockBreakListener;
import com.oinkcraft.oinkutils.modtools.clockbreaker.ClockBreakerDropListener;
import com.oinkcraft.oinkutils.modtools.CommandBase;
import com.oinkcraft.oinkutils.compassnav.CompassClickListener;
import com.oinkcraft.oinkutils.compassnav.InventoryClickListener;
import com.oinkcraft.oinkutils.compassnav.PlayerGiveCompassListener;
import com.oinkcraft.oinkutils.redstoneworld.NoTNT;
import com.oinkcraft.oinkutils.submission.JoinNotificationListener;
import com.oinkcraft.oinkutils.submission.SubmitCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    // Prefix for the plugin
    private String prefix = "§8[§dOinkUtils§8]§d ";

    private static Main main;
    // ClockBreaker thing
    private static HashMap<Player, Integer> clockBreakerClicks = new HashMap<Player, Integer>(); // Used for limiting clicks for the clockbreaker item.
    // Submissions file creation
    private File submissionsf;
    private FileConfiguration submissions;

    @Override
    public void onEnable() {
        // Get the plugin manager
        PluginManager pm = Bukkit.getServer().getPluginManager();

        // Create configuration
        createConfig();
        createSubmissionYML();

        // Register the com.oinkcraft.oinkutils.Main instance
        main = this;

        /* Register events */
        // Compass Navigation
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerGiveCompassListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CompassClickListener(), this);
        // ChatColorChange
        Bukkit.getServer().getPluginManager().registerEvents(new ChatColorChangeListener(), this);
        // ModTools/ClockBreaker
        Bukkit.getServer().getPluginManager().registerEvents(new ClockBreakListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ClockBreakerDropListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ClockBreakInventoryMoveListener(), this);
        // Submissions
        Bukkit.getServer().getPluginManager().registerEvents(new JoinNotificationListener(), this);
        // NoTNT
        Bukkit.getServer().getPluginManager().registerEvents(new NoTNT(), this);

        /* Register commands */
        // Oink utils base
        getCommand("oinkutils").setExecutor(new BaseCommand());
        // ChatColorChange
        getCommand("chatcolorchange").setExecutor(new ColorChangeCommand());
        // Modtools
        getCommand("modtools").setExecutor(new CommandBase());
        getCommand("sneeze").setExecutor(new CommandBase());
        // Submissions
        getCommand("submit").setExecutor(new SubmitCommand());

        getLogger().log(Level.INFO, "OinkUtils v" + getDescription().getVersion() + " has successfully been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "OinkUtils v" + getDescription().getVersion() + " has successfully been disabled!");
    }

    public void createConfig() {
        // Create the directory
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        // Create the base configuration
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            getLogger().log(Level.INFO, "No configuration found for OinkUtils v" + getDescription().getVersion());
            saveDefaultConfig();
        } else {
            getLogger().log(Level.INFO, "Configuration found for OinkUtils v" + getDescription().getVersion() + "!");
        }
    }

    public static Main getInstance() {
        return main;
    }

    public String getPrefix() {
        return prefix;
    }

    /* For the compass nav component - I don't want to make a new class */
    public ItemStack navCompass(){
        ItemStack compass = new ItemStack(Material.COMPASS, 1);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Oinkcraft Navigator");
        compassMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        compass.setItemMeta(compassMeta);
        return compass;
    }
    /* For the ClockBreaker hashmap above */
    public static HashMap<Player, Integer> getClockBreakerClicks(){
        return clockBreakerClicks;
    }

    /* For the submissions YML file */
    private void createSubmissionYML() {
        // Factions config file
        submissionsf = new File(getDataFolder(), "submissions.yml");
        if(!submissionsf.exists()){
            getLogger().log(Level.INFO, "No submissions file found... Generating now!");
            saveResource("submissions.yml", false);
        }
        submissions = new YamlConfiguration();
        try {
            submissions.load(submissionsf);
        } catch (IOException | InvalidConfigurationException e){
            System.out.println(prefix + "Something has gone wrong with the submissions.yml!");
            e.printStackTrace();
        }
    }

    public FileConfiguration getSubmissions(){
        return this.submissions;
    }

    public void saveSubmissions() {
        if ((this.submissions == null) || (this.submissionsf == null)) {
            return;
        }
        try {
            getSubmissions().save(this.submissionsf);
        }
        catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + this.submissions, ex);
        }
    }

    public void reloadSubmissions() {
        if (this.submissionsf == null) {
            this.submissionsf = new File(getDataFolder(), "submissions.yml");
        }
        this.submissions = YamlConfiguration.loadConfiguration(this.submissionsf);

        InputStream defConfigStream = getResource("submissions.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "submissions.yml"));
            this.submissions.setDefaults(defConfig);
        }
    }

}