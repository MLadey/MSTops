package ru.mladey.mstops;

import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import java.lang.reflect.*;

public class CommandRegister extends Command implements PluginIdentifiableCommand
{
    protected Plugin plugin;
    protected final CommandExecutor owner;
    protected final Object registeredWith;

    public CommandRegister(final String[] aliases, final String desc, final String usage, final CommandExecutor owner, final Object registeredWith, final Plugin plugin2) {
        super(aliases[0], desc, usage, (List)Arrays.asList(aliases));
        this.owner = owner;
        this.plugin = plugin2;
        this.registeredWith = registeredWith;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        return this.owner.onCommand(sender, (Command)this, alias, args);
    }

    public Object getRegisteredWith() {
        return this.registeredWith;
    }

    public static void reg(final Plugin plugin, final CommandExecutor cxecutor, final String[] aliases, final String desc, final String usage) {
        try {
            final CommandRegister reg = new CommandRegister(aliases, desc, usage, cxecutor, new Object(), plugin);
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            final CommandMap map = (CommandMap)field.get(Bukkit.getServer());
            map.register(plugin.getDescription().getName(), (Command)reg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}