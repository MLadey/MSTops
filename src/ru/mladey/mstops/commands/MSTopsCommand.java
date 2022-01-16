package ru.mladey.mstops.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import ru.mladey.mstops.Main;

public class MSTopsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        FileConfiguration cfg = Main.getPlugin().getConfig();

        if(!sender.hasPermission(cfg.getString("permissions.command"))){
            sender.sendMessage(cfg.getString("messages.nopermission")
                  .replace("$command$", "/" + alias)
                  .replace("$player$", sender.getName())
                  .replace("&", "§"));
            return true;
        }

        Main.stats.loadAllPlayers(sender);
        return true;

    }
}
