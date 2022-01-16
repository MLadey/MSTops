package ru.mladey.mstops.manager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mladey.mstops.Main;
import ru.mladey.mstops.utils.LocationUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TopsManager {

    public void createDatabase() {

        String tables = "";

        if(Main.getPlugin().getConfig().getBoolean("bukkit")) {

            List<String> statistics = Main.getPlugin().getConfig().getStringList("statistics");

            for (int i = 0; i < statistics.size(); ++i) {

                String str = statistics.get(i);
                String[] parse = str.split(";");


                if (i == 0) {

                    tables = "`<name>` INT(16)".replace("<name>", parse[1]);

                }

                if (i != 0) {

                    tables = tables + ", `<name>` INT(16)".replace("<name>", parse[1]);

                }


            }

        }

        if(Main.getPlugin().getConfig().getBoolean("economy")){

            if(Main.getPlugin().getConfig().getBoolean("bukkit")) {
                tables = tables + ", `economy` INT(16)";
            }

            if(!Main.getPlugin().getConfig().getBoolean("bukkit")) {
                tables = tables + "`economy` INT(16)";
            }

        }

        Main.mysql.executeStatement("CREATE TABLE IF NOT EXISTS {table} (`username` VARCHAR(16), " + tables + ")", null, false);

    }

    public void voidHolograms(){

       List<String> load = Main.getPlugin().getConfig().getStringList("load");

       for(int i = 0; i < load.size(); ++i){

           String name = load.get(i);
           startUpdater(name);

       }

    }

    public void startUpdater(String name){

        int time = Main.getPlugin().getConfig().getInt("holograms." + name + ".settings.update");

        Hologram hologram = parseHologram(name);

        new BukkitRunnable() {

            public void run() {

                if(hologram.isDeleted()){
                    this.cancel();
                    return;
                }

                hologram.delete();
                startUpdater(name);
                this.cancel();
                return;

            }

        }.runTaskTimer(Main.getPlugin(), 20 * time, 0);

    }

    public void createCooldown(String name){

        String nameL = "";
        int time = 0;

        List<String> cooldowns = Main.getPlugin().getConfig().getStringList("cooldowns");

        for(int i = 0; i < cooldowns.size(); ++i){

            String cooldown_str = cooldowns.get(i);
            String[] cooldown_parse = cooldown_str.split(";");

            if(cooldown_str.contains(name)){

                nameL = cooldown_str;
                time = Integer.parseInt(cooldown_parse[cooldown_parse.length - 1]);

            }

        }

        Main.cooldowns.add(nameL);

        String finalName = nameL;

        new BukkitRunnable() {

            public void run() {


                Main.cooldowns.remove(finalName);
                this.cancel();
                return;

            }

        }.runTaskTimer(Main.getPlugin(), 20 * time, 0);

    }

    public boolean isCooldown(String name){

        List<String> cooldowns = Main.cooldowns;
        boolean bol = false;

        for(int i = 0; i < cooldowns.size(); ++i){

            String str = cooldowns.get(i);
            String[] parse = str.split(";");

            for(int i2 = 0; i2 < parse.length; i2++){

                if(parse[i2].equals(name)){

                    bol = true;

                }

            }

        }

        return bol;


    }

    public Hologram parseHologram(String name){

        FileConfiguration cfg = Main.getPlugin().getConfig();
        List<String> lines = cfg.getStringList("holograms." + name + ".lines");

        String loc_str = cfg.getString("holograms." + name + ".settings.location");
        Location loc = LocationUtil.fromStringWXYZYP(loc_str);

        String type = cfg.getString("holograms." + name + ".settings.type");

        final Hologram hologram = HologramsAPI.createHologram(Main.getPlugin(), loc);

        for(int i = 0; i < lines.size(); ++i){


            String line = lines.get(i);
            List<String> tops = null;

            if(type.equals("bukkit")) {

                String statistic = cfg.getString("holograms." + name + ".settings.statistic");
                tops = getPlayersTopBukkitAndEconomy(statistic, cfg.getInt("holograms." + name + ".settings.positions"));

            }

            if(type.equals("economy")) {

                tops = getPlayersTopBukkitAndEconomy("economy", cfg.getInt("holograms." + name + ".settings.positions"));

            }

            if(type.equals("mysql")) {

                tops = getPlayersTopMysql(name, cfg.getInt("holograms." + name + ".settings.positions"));

            }

            if(line.contains("clickable")){

                String[] parse = line.split(";");
                String clear = parse[0].replace("<clickable_", "").replace(">", "");

                List<String> clickable = cfg.getStringList("holograms." + name + ".settings.clickable." + clear);

                TouchableLine clickable_line = hologram.appendTextLine(setPlaceholders(tops, parse[1]));

                clickable_line.setTouchHandler(new TouchHandler() {
                    @Override
                    public void onTouch(Player p) {

                        for(int i2 = 0; i2 < clickable.size(); ++i2){

                            String clickable_str = clickable.get(i2);
                            String[] clickable_parse = clickable_str.split(";");

                            String action_type = clickable_parse[0];
                            String action = clickable_parse[1];

                            if(action_type.equals("change")){


                                if(isCooldown(name)){
                                    p.sendMessage(Main.getPlugin().getConfig().getString("messages.cooldown")
                                            .replace("&", "§")
                                            .replace("$player$", p.getName()));
                                    return;
                                }

                                createCooldown(name);
                                hologram.delete();
                                startUpdater(action);

                            }

                            if(action_type.equals("message")){

                                p.sendMessage(action.replace("&", "§").replace("$player$", p.getName()));


                            }

                            if(action_type.equals("console")){

                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("$player$", p.getName()));

                            }

                            if(action_type.equals("player")){

                                Bukkit.dispatchCommand(p, action.replace("$player$", p.getName()));

                            }


                        }

                    }
                });

            }

            if(line.contains("ICON;")){

                hologram.appendItemLine(new ItemStack(Material.getMaterial(line.replace("ICON;", "")), 1));

            }

            if(!line.contains("ICON;") & !line.contains("clickable")){

                hologram.appendTextLine(setPlaceholders(tops, line));

            }


        }

        return hologram;

    }

    public String setPlaceholders(List<String> tops, String line){

        String setLine = line;
        setLine = setLine.replace("&", "§");

        for(int i = 0; i < tops.size(); ++i){

            String top_str = tops.get(i);
            String[] top_parse = top_str.split(";");
            int id = i + 1;

            setLine = setLine.replace("<name_$ID$>".replace("$ID$", id + ""), top_parse[0]);
            setLine = setLine.replace("<score_$ID$>".replace("$ID$", id+ ""), top_parse[1]);


        }

        return setLine;

    }

    public List getPlayersTopBukkitAndEconomy(String top, int size){

        Statement stmt = null;
        List<String> players = new ArrayList<String>();
        try {
            stmt = Main.mysql.getConnection().createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {

            String table = Main.getPlugin().getConfig().getString("mysql.table");

            ResultSet rs = stmt.executeQuery("select * FROM $table$ order BY $top$ desc limit 0,$size$"
                         .replace("$table$", table)
                         .replace("$top$", top)
                         .replace("$size$", size + ""));

            while(rs.next()){

                String username = rs.getString("username");
                int count = rs.getInt(top);

                players.add(username + ";" + count);

            }
            rs.close();
        } catch (SQLException throwables) {
            players = null;
        }

        return players;


    }

    public List getPlayersTopMysql(String name, int size){

        Statement stmt = null;
        List<String> players = new ArrayList<String>();
        try {

            FileConfiguration cfg = Main.getPlugin().getConfig();
            String hostname = cfg.getString("holograms.$name$.settings.mysql.hostname".replace("$name$", name));
            String username = cfg.getString("holograms.$name$.settings.mysql.username".replace("$name$", name));
            String password = cfg.getString("holograms.$name$.settings.mysql.password".replace("$name$", name));
            String database = cfg.getString("holograms.$name$.settings.mysql.database".replace("$name$", name));
            String port = cfg.getString("holograms.$name$.settings.mysql.port".replace("$name$", name));
            String options = cfg.getString("holograms.$name$.settings.mysql.options".replace("$name$", name));

            stmt = Main.otherMySQL.newConnection(hostname,
                    username, password, database, port, options).createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {

            FileConfiguration cfg = Main.getPlugin().getConfig();
            String table = cfg.getString("holograms.$name$.settings.mysql.table".replace("$name$", name));
            String column_name = cfg.getString("holograms.$name$.settings.mysql.column_name".replace("$name$", name));
            String column_score = cfg.getString("holograms.$name$.settings.mysql.column_score".replace("$name$", name));

            ResultSet rs = stmt.executeQuery("select * FROM $table$ order BY $top$ desc limit 0,$size$"
                    .replace("$table$", table)
                    .replace("$top$", column_score)
                    .replace("$size$", size + ""));

            while(rs.next()){

                String username = rs.getString(column_name);
                int count = rs.getInt(column_score);

                players.add(username + ";" + count);

            }
            rs.close();
        } catch (SQLException throwables) {
            players = null;
        }

        return players;


    }

}
