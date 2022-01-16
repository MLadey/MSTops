package ru.mladey.mstops.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ru.mladey.mstops.Main;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatisticManager {

    public void createData(String username, String uuid) {

        final ArrayList<String> options = new ArrayList<String>();
        options.add("String:1:" + username);

        String statisticsName = ""; String q = "";

        if(Main.getPlugin().getConfig().getBoolean("bukkit")) {
            List<String> statistics = Main.getPlugin().getConfig().getStringList("statistics");

            for (int i = 0; i < statistics.size(); ++i) {

                int id = i + 2;
                String str = statistics.get(i);
                String[] parse = str.split(";");

                int count = Integer.parseInt(getStatistic(uuid, parse[0]));
                options.add("int:" + id + ":" + count);

                if (i == 0) {
                    statisticsName = parse[1] + "";
                    q = "?";
                }
                if (i != 0) {
                    statisticsName = statisticsName + ", " + parse[1] + "";
                    q = q + ", ?";
                }
            }
        }
        if(Main.getPlugin().getConfig().getBoolean("economy")){

            if(Main.getPlugin().getConfig().getBoolean("bukkit")) {

                List<String> statistics = Main.getPlugin().getConfig().getStringList("statistics");
                options.add("int:" + (statistics.size() + 2) + ":" + Math.round(Double.parseDouble(Main.getEconomy().getBalance(username) + "")));
                statisticsName = statisticsName + ", " + "economy";
                q = q + ", ?";

            }

            if(!Main.getPlugin().getConfig().getBoolean("bukkit")) {

                options.add("int:" + "2" + ":" + Math.round(Double.parseDouble(Main.getEconomy().getBalance(username) + "")));
                statisticsName = statisticsName + "" + "economy";
                q = q + "?";

            }

        }
        Main.mysql.executeStatement("INSERT INTO {table} (username, $statistics$) VALUES (?, $n$);"
                .replace("$statistics$", statisticsName)
                .replace("$n$", q), options, false);
    }

    public String getStatistic(String uuid, String statsName){

        File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
        String filename = "/" + uuid +  ".json";

        try (Reader reader = new FileReader(playerfolder.getPath() + filename)){

            JSONParser jsonParser = new JSONParser();
            Object parsed = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) parsed;
            long stats = (long) jsonObject.get(statsName);
            return stats + "";

        } catch (IOException | org.json.simple.parser.ParseException e) { return "0";
        } catch (NullPointerException e) { return "0"; }

    }

    public boolean isData(String username) {

        final ArrayList<String> options = new ArrayList<String>();
        options.add("String:1:" + username);
        final ResultSet rs = Main.mysql.executeStatement("SELECT * FROM {table} WHERE username=? LIMIT 1;", options, true);
        try {

            if (rs.next()) { return true;
            } return false; }

        catch (SQLException e) { e.printStackTrace(); return false; }

    }

    public void deleteData(String username) {
        final ArrayList<String> options = new ArrayList<String>();
        options.add("String:1:" + username);
        Main.mysql.executeStatement("DELETE FROM {table} WHERE `username`=?", options, false);
    }

    public void updateData(String username, String uuid){

        deleteData(username);
        createData(username, uuid);

        return;


    }

    public void loadAllPlayers(CommandSender sender){

        for(OfflinePlayer p : Bukkit.getOfflinePlayers()){

            String name = p.getName();
            String uuid = p.getUniqueId().toString();

            updateData(name, uuid);

        }

        sender.sendMessage(Main.getPlugin().getConfig().getString("messages.loadallplayers")
              .replace("&", "§")
              .replace("$player$", sender.getName()));

        return;
    }

    public void updateOnlinePlayers(){

        for(Player p : Bukkit.getOnlinePlayers()){
            updateData(p.getName(), p.getUniqueId().toString());
        }

        return;

    }
}
