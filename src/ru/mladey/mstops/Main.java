package ru.mladey.mstops;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mladey.mstops.commands.MSTopsCommand;
import ru.mladey.mstops.handler.StatisticHandler;
import ru.mladey.mstops.manager.MySQLManager;
import ru.mladey.mstops.manager.OtherMySQLManager;
import ru.mladey.mstops.manager.StatisticManager;
import ru.mladey.mstops.manager.TopsManager;

import java.util.ArrayList;
import java.util.List;


public class Main extends JavaPlugin implements Listener {

    private static Main plugin;
    public static MySQLManager mysql;
    public static OtherMySQLManager otherMySQL;
    public static StatisticManager stats;
    public static TopsManager tops;
    public static List<String> cooldowns = new ArrayList<String>();

    private static Economy econ = null;
    public Main() {
        Main.plugin = this;
    }

    public static Main getPlugin() {
        return Main.plugin;
    }

    public void onEnable(){

        if (!setupEconomy() & getConfig().getBoolean("economy")) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.saveDefaultConfig();

        Main.mysql = new MySQLManager();
        Main.stats = new StatisticManager();
        Main.tops = new TopsManager();
        Main.otherMySQL = new OtherMySQLManager();

        if(getConfig().getBoolean("economy") || getConfig().getBoolean("bukkit")){
            tops.createDatabase();
        }

        getServer().getPluginManager().registerEvents(new StatisticHandler(), this);

        CommandRegister.reg(this, new MSTopsCommand(), new String[]{"mstops", "tops"}, "Загрузка всех игроков в базу плагина", "/mstops");
        Main.tops.voidHolograms();

        new BukkitRunnable() {

            public void run() {

                Main.stats.updateOnlinePlayers();
                return;

            }

        }.runTaskTimer(Main.getPlugin(), 0, getConfig().getInt("update") * 20);

    }

    private boolean setupEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;

    }

    public static Economy getEconomy() {
        return econ;
    }

}
