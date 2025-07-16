package com.msgapp.localchat;

public class Group {
    private String name;
    private String hostAddress;
    private int memberCount;
    private long timestamp;

    public Group(String name, String hostAddress, int memberCount) {
        this.name = name;
        this.hostAddress = hostAddress;
        this.memberCount = memberCount;
        this.timestamp = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}