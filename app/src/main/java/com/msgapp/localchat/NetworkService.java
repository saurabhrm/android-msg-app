package com.msgapp.localchat;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkService {
    private static final String TAG = "NetworkService";
    private static final String MULTICAST_ADDRESS = "224.0.0.1";
    private static final int DISCOVERY_PORT = 8888;
    private static final int CHAT_PORT = 8889;
    
    private Context context;
    private ExecutorService executorService;
    private MulticastSocket multicastSocket;
    private DatagramSocket chatSocket;
    private boolean isDiscovering = false;
    private Gson gson;
    private WifiManager.MulticastLock multicastLock;

    public interface GroupDiscoveryListener {
        void onGroupsFound(List<Group> groups);
    }

    public interface MessageListener {
        void onMessageReceived(Message message);
    }

    public NetworkService(Context context) {
        this.context = context;
        this.executorService = Executors.newCachedThreadPool();
        this.gson = new Gson();
        
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifiManager.createMulticastLock("LocalChatMulticast");
    }

    public void startDiscovery(GroupDiscoveryListener listener) {
        if (isDiscovering) return;
        
        isDiscovering = true;
        multicastLock.acquire();
        
        executorService.execute(() -> {
            try {
                multicastSocket = new MulticastSocket(DISCOVERY_PORT);
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                multicastSocket.joinGroup(group);
                
                List<Group> discoveredGroups = new ArrayList<>();
                byte[] buffer = new byte[1024];
                
                while (isDiscovering) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(packet);
                    
                    String data = new String(packet.getData(), 0, packet.getLength());
                    try {
                        GroupAnnouncement announcement = gson.fromJson(data, GroupAnnouncement.class);
                        if ("GROUP_ANNOUNCEMENT".equals(announcement.type)) {
                            Group discoveredGroup = new Group(
                                announcement.groupName,
                                packet.getAddress().getHostAddress(),
                                announcement.memberCount
                            );
                            
                            // Update or add group
                            boolean found = false;
                            for (int i = 0; i < discoveredGroups.size(); i++) {
                                if (discoveredGroups.get(i).getName().equals(discoveredGroup.getName()) &&
                                    discoveredGroups.get(i).getHostAddress().equals(discoveredGroup.getHostAddress())) {
                                    discoveredGroups.set(i, discoveredGroup);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                discoveredGroups.add(discoveredGroup);
                            }
                            
                            listener.onGroupsFound(new ArrayList<>(discoveredGroups));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing group announcement", e);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error in group discovery", e);
            }
        });
    }

    public void stopDiscovery() {
        isDiscovering = false;
        if (multicastSocket != null && !multicastSocket.isClosed()) {
            multicastSocket.close();
        }
        if (multicastLock.isHeld()) {
            multicastLock.release();
        }
    }

    public void announceGroup(String groupName, int memberCount) {
        executorService.execute(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                GroupAnnouncement announcement = new GroupAnnouncement();
                announcement.type = "GROUP_ANNOUNCEMENT";
                announcement.groupName = groupName;
                announcement.memberCount = memberCount;
                
                String data = gson.toJson(announcement);
                byte[] buffer = data.getBytes();
                
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, DISCOVERY_PORT);
                
                socket.send(packet);
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error announcing group", e);
            }
        });
    }

    public void startChatServer(MessageListener listener) {
        executorService.execute(() -> {
            try {
                chatSocket = new DatagramSocket(CHAT_PORT);
                byte[] buffer = new byte[1024];
                
                while (!chatSocket.isClosed()) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    chatSocket.receive(packet);
                    
                    String data = new String(packet.getData(), 0, packet.getLength());
                    try {
                        Message message = gson.fromJson(data, Message.class);
                        listener.onMessageReceived(message);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing message", e);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error in chat server", e);
            }
        });
    }

    public void sendMessage(String hostAddress, Message message) {
        executorService.execute(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                String data = gson.toJson(message);
                byte[] buffer = data.getBytes();
                
                InetAddress address = InetAddress.getByName(hostAddress);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, CHAT_PORT);
                
                socket.send(packet);
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error sending message", e);
            }
        });
    }

    public void shutdown() {
        stopDiscovery();
        if (chatSocket != null && !chatSocket.isClosed()) {
            chatSocket.close();
        }
        executorService.shutdown();
    }

    private static class GroupAnnouncement {
        String type;
        String groupName;
        int memberCount;
    }
}