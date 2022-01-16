package ru.mladey.mstops.manager;

import javax.sql.rowset.*;
import com.sun.rowset.*;
import ru.mladey.mstops.Main;

import java.sql.*;
import java.util.*;

public class MySQLManager
{
    public synchronized Connection newConnection() {
        try {

            String hostname = Main.getPlugin().getConfig().getString("mysql.hostname");
            String username = Main.getPlugin().getConfig().getString("mysql.username");
            String password = Main.getPlugin().getConfig().getString("mysql.password");
            String database = Main.getPlugin().getConfig().getString("mysql.database");
            String port = Main.getPlugin().getConfig().getString("mysql.port");
            String options = Main.getPlugin().getConfig().getString("mysql.options");

            String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + options;

            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);

        }
        catch (Exception e) {
            return null;
        }
    }

    public Connection getConnection() {
        return this.newConnection();
    }

    public CachedRowSet executeStatement(final String request, final ArrayList<String> options, final boolean query) {
        final Connection connection = this.getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        CachedRowSet rowSet = null;
        try {
            rowSet = new CachedRowSetImpl();
            String table = Main.getPlugin().getConfig().getString("mysql.table");
            statement = connection.prepareCall(request.replaceFirst("\\{table}", table));
            if (options != null) {
                for (final String values : options) {
                    final String[] svalues = values.split(":");
                    if (svalues[0].equals("String")) {
                        statement.setString(Integer.parseInt(svalues[1]), svalues[2]);
                    }
                    else {
                        if (!svalues[0].equals("int")) {
                            continue;
                        }
                        statement.setInt(Integer.parseInt(svalues[1]), Integer.parseInt(svalues[2]));
                    }
                }
            }
            if (query) {
                rs = statement.executeQuery();
                rowSet.populate(rs);
            }
            else {
                statement.executeUpdate();
            }
        }
        catch (SQLException ex) {}
        finally {
            try {
                connection.close();
            }
            catch (SQLException ex2) {}
            try {
                if (statement != null) {
                    statement.close();
                }
            }
            catch (SQLException ex3) {}
            if (query) {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException ex4) {}
            }
        }
        return rowSet;
    }
}
