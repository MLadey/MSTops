package ru.mladey.mstops.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.mladey.mstops.Main;

public class StatisticHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if(Main.getPlugin().getConfig().getBoolean("economy") || Main.getPlugin().getConfig().getBoolean("bukkit")) {

            if (!Main.stats.isData(p.getName())) {
                Main.stats.createData(p.getName(), p.getUniqueId().toString());
                return;
            }

            return;

        }

        return;

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){

        Player p = e.getPlayer();

        if(Main.getPlugin().getConfig().getBoolean("economy") || Main.getPlugin().getConfig().getBoolean("bukkit")) {

            Main.stats.updateData(p.getName(), p.getUniqueId().toString());
            return;

        }

        return;

    }

}
