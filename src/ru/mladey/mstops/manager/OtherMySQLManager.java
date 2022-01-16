package ru.mladey.mstops.manager;

import javax.sql.rowset.*;
import com.sun.rowset.*;
import ru.mladey.mstops.Main;

import java.sql.*;
import java.util.*;

public class OtherMySQLManager
{
    public synchronized Connection newConnection(String hostname, String username
             , String password, String database, String port, String options) {
        try {


            String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + options;

            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);

        }
        catch (Exception e) {
            return null;
        }
    }

    public CachedRowSet executeStatement(String hostname, String username, String password, String database, String port, String optionsBD, String table, final String request, final ArrayList<String> options, final boolean query) {
        final Connection connection = newConnection(hostname, username, password, database, port, optionsBD);
        PreparedStatement statement = null;
        ResultSet rs = null;
        CachedRowSet rowSet = null;
        try {
            rowSet = new CachedRowSetImpl();

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
