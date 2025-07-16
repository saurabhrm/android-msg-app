package com.msgapp.localchat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;
    private MessageAdapter messageAdapter;
    private List<Message> messages;
    private NetworkService networkService;
    private String username, groupName, groupHost;
    private boolean isCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getIntentData();
        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupNetworking();
        
        setTitle(groupName);
    }

    private void getIntentData() {
        username = getIntent().getStringExtra("username");
        groupName = getIntent().getStringExtra("groupName");
        groupHost = getIntent().getStringExtra("groupHost");
        isCreator = getIntent().getBooleanExtra("isCreator", false);
    }

    private void initViews() {
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages, username);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messageAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void setupNetworking() {
        networkService = new NetworkService(this);
        
        // Start chat server to receive messages
        networkService.startChatServer(message -> {
            runOnUiThread(() -> {
                messages.add(message);
                messageAdapter.notifyItemInserted(messages.size() - 1);
                rvMessages.scrollToPosition(messages.size() - 1);
            });
        });

        // If creator, keep announcing the group
        if (isCreator) {
            startGroupAnnouncement();
        }
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        Message message = new Message(username, messageText);
        
        // Add to local messages
        messages.add(message);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        rvMessages.scrollToPosition(messages.size() - 1);
        
        // Send to network
        if (isCreator) {
            // Creator broadcasts to all connected clients
            // For now, just add locally since we need client management
        } else {
            // Send to group host
            networkService.sendMessage(groupHost, message);
        }
        
        etMessage.setText("");
    }

    private void startGroupAnnouncement() {
        // Periodically announce the group
        new Thread(() -> {
            while (!isFinishing()) {
                networkService.announceGroup(groupName, messages.size());
                try {
                    Thread.sleep(5000); // Announce every 5 seconds
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkService != null) {
            networkService.shutdown();
        }
    }
}