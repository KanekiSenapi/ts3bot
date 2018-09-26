package pl.aogiri.tsbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.*;

public class Bot {
    List<ChannelMini> privates;
    final String ADDRESS = ""; //ip address ts3
    final String USERNAME = ""; //user to serverquery
    final String PASSWORD = ""; // password to serverquery
    final String DISPLAYNAME = "Just Bot" + new Random().nextInt(100)+1; //Bot name


    final String UNDEFINEDCOMMAND = "Undefined command. Use !help";

    final int idOnline = 235; //Channel where online user will be showed (-1 off)
    final int maxEmpty = 1209600; //Max empty time for channel (-1 off)

    final TS3Config config = new TS3Config();
    final TS3Query query = new TS3Query(config);
    final TS3Api api = query.getApi();

    final Database db = new Database();

    public Bot(){
    }

    public void build(){
        config.setHost(ADDRESS);
        query.connect();
        api.login(USERNAME, PASSWORD);
        api.selectVirtualServerById(1);
        api.setNickname(DISPLAYNAME);

        api.registerEvent(TS3EventType.SERVER);
        api.registerEvent(TS3EventType.CHANNEL);
        api.registerEvent(TS3EventType.TEXT_PRIVATE);
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientJoin(ClientJoinEvent e) {
                setServerOnline();
            }

            @Override
            public void onClientLeave(ClientLeaveEvent e) {
                setServerOnline();
            }

            @Override
            public void onChannelEdit(ChannelEditedEvent e) {
                checkChannelName(e);
            }

            @Override
            public void onTextMessage(TextMessageEvent e) {
                System.out.println(e);
                if(e.getInvokerName().equals(DISPLAYNAME))
                    return;
                String text = e.getMessage().toLowerCase();
                System.out.println(text);
                int targetId = e.getInvokerId();
                if(text.startsWith("!")){
                    switch (text.split("!")[1]){
                        case "newchannel":
                            System.out.println("Generate channel");
                            api.sendPrivateMessage(targetId, "Created channel number #" + createPrivateChannel());
                            break;
                    }
                }else{
                    api.sendPrivateMessage(targetId, UNDEFINEDCOMMAND);
                }
            }
        });

//        api.sendChannelMessage(DISPLAYNAME + " is online!");

    }

    public void setCheckChannels(){
        if(maxEmpty==-1) return;
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                checkExpireChannels();
            }
        };
        timer.schedule(timerTask, 0, 1000 * 60 * 5);
    }

    public void disconnect(){
        query.exit();
    }

    public List<Channel> getChannels(){
        return api.getChannels();
    }

    public ChannelInfo getChannel(int cid){
        return api.getChannelInfo(cid);
    }

    public List<ChannelInfo> getPrivateChannels(){
        List<ChannelInfo> channelInfos = new ArrayList<>();
        db.connect();
        privates = db.getPrivates();
        db.disconnect();

        for(int i = 0 ; i < privates.size(); i++){
            channelInfos.add(this.getChannel(privates.get(i).getCid()));
        }
        return channelInfos;
    }

    public void setServerOnline(){
        if(idOnline == -1) return;
       List<Client> clients =  api.getClients();
       int size = 0;
       for(int i = 0 ; i < clients.size() ; i++){
           if(clients.get(i).isServerQueryClient())
               continue;
           size++;
       }
       System.out.println(time() + "Set the number of users online to " + size);
       setChannelName(idOnline,"Użytkownicy online : " + size);
    }

    private void checkExpireChannels(){
            System.out.println(time() + "Checking expired channels" );
            List<ChannelInfo> channelInfos = getPrivateChannels();
            for (int i = 0; i < channelInfos.size(); i++) {
                ChannelInfo channelInfo = channelInfos.get(i);
                String x = channelInfo.getName().split(" ")[0];
                if (channelInfo.getSecondsEmpty() > maxEmpty && (channelInfo.getId() != 36 && channelInfo.getId() != 62) && !x.equals("╔") && !x.equals("╠") && !x.equals("╚")) {
                    System.out.println(time() + "Deleting channel id #" + channelInfo.getId() + " | " + channelInfo.getName());
                    api.deleteChannel(channelInfo.getId());
                }
            }
            System.out.println(time() + "End of checking" );
    }


    private void checkChannelName(ChannelEditedEvent e){
        System.out.println(time() + "Checking channel name" );
        Database db = new Database();
        db.connect();
        ChannelMini channelDB = db.getPrivate(e.getChannelId());
        int cid = channelDB.getCid();
        if( cid != 0){
            //If private
            ChannelInfo channelTS = getChannel(cid);

            String[] channelName = channelTS.getName().split(" ");
            if (!channelName[0].equals(channelDB.getPrefix())) {
                System.out.println(time() + "Set old channel name #" + cid );
                setChannelName(cid, channelDB.getName());

            }
            else{
                System.out.println(time() + "Update channel name in database #" + cid );
                db.updateChannelName(cid,channelTS.getName());

            }


        }else{
            //if not private
        }
        db.disconnect();

    }

    private String time(){
        return new Date() + " : " ;
    }

    private void setChannelName(int cid, String name){
        Map<ChannelProperty, String> properties = new HashMap<>();
        properties.put(ChannelProperty.CHANNEL_NAME, name);
        System.out.println(time() + "Channel #" + cid + " | Set name to " + name);
        api.editChannel(cid,properties);
    }

    private int createPrivateChannel(){
        Database db = new Database();
        db.connect();
        int number = Integer.valueOf(db.getLast(1)) + 1;
        String cidLast = db.getLast(0);
        int tmp;

        //Main
        String name = number + ". name";
        Map<ChannelProperty, String> prop = new HashMap<>();
        prop.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
        prop.put(ChannelProperty.CHANNEL_NAME, name);
        prop.put(ChannelProperty.CHANNEL_ORDER, cidLast);
        int mainCid = api.createChannel(name, prop);
        db.addChannel(name,mainCid,number+".",0);

        //Subs
        prop = new HashMap<>();
        prop.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
        prop.put(ChannelProperty.CHANNEL_NAME, "╔ sub");
        prop.put(ChannelProperty.CPID, String.valueOf(mainCid));
        tmp = api.createChannel("╔ sub", prop);
        db.addChannel("╔ sub",tmp,"╔",1);

        prop = new HashMap<>();
        prop.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
        prop.put(ChannelProperty.CHANNEL_NAME, "╠ sub");
        prop.put(ChannelProperty.CPID, String.valueOf(mainCid));
        prop.put(ChannelProperty.CHANNEL_ORDER, String.valueOf(tmp));
        tmp = api.createChannel("╠ sub", prop);
        db.addChannel("╠ sub",tmp,"╠",1);

        prop = new HashMap<>();
        prop.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
        prop.put(ChannelProperty.CHANNEL_NAME, "╚ sub");
        prop.put(ChannelProperty.CPID, String.valueOf(mainCid));
        prop.put(ChannelProperty.CHANNEL_ORDER, String.valueOf(tmp));
        tmp = api.createChannel("╚ sub", prop);
        db.addChannel("╚ sub",tmp,"╚",1);



        return number;



    }
}




