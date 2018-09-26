package pl.aogiri.tsbot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection conn;
    private Statement statement;

    private final static String hostname = "localhost";
    private final static String port = "3306";
    private final static String dbname = "ts3";
    private final static String dbuser = "root";
    private final static String dbpass = "";

    public Database(){

    }

    public void connect(){
        try {
            conn = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/"+dbname+"?serverTimezone=UTC&characterEncoding=utf8",dbuser, dbpass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<ChannelMini> getPrivates(){
        List<ChannelMini> toR = new ArrayList<>();
        String query= "SELECT cid,name,prefix from privates where sub = 0";

        try {
            statement = conn.createStatement();
        ResultSet result = statement.executeQuery(query);

        while(result.next()){
            ChannelMini tmp = new ChannelMini();
            tmp.setPrefix(result.getString("prefix"));
            tmp.setName(result.getString("name"));
            tmp.setCid(result.getInt("cid"));
            toR.add(tmp);
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toR;
    }

    public ChannelMini getPrivate(int cid){
        ChannelMini toR = new ChannelMini();
        String query= "SELECT cid,name,prefix from privates where cid = " + cid;

        try {
            statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            while(result.next()){
                toR.setPrefix(result.getString("prefix"));
                toR.setName(result.getString("name"));
                toR.setCid(result.getInt("cid"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toR;
    }

    public void disconnect(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateChannelName(int cid, String name){
        String query= "UPDATE privates SET name = \"" + name + "\" where cid = " + cid;
        try {
            statement = conn.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getLast(int i){
        String query = "SELECT prefix,cid FROM `privates` WHERE sub = 0 ORDER BY cid DESC limit 0,1";
        try {
            statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            if(result.next()) {
                if(i==0)
                    return String.valueOf(result.getInt("cid"));
                return result.getString("prefix").replace(".", "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addChannel(String name, int cid, String prefix, int sub){
        String query = "Insert into privates(name,cid,prefix,sub) values(\""+name+"\",\""+cid+"\",\""+prefix+"\","+sub+")";
        try{
            statement = conn.createStatement();
            statement.execute(query);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
