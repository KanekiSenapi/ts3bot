package pl.aogiri.tsbot;

public class ChannelMini {

    private String prefix;
    private String name;
    private int cid;

    public ChannelMini() {
    }

    public ChannelMini(String prefix, String name, int cid) {
        this.prefix = prefix;
        this.name = name;
        this.cid = cid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
