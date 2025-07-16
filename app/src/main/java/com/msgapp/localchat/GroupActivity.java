package com.msgapp.localchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GroupActivity extends AppCompatActivity {
    private EditText etGroupName;
    private Button btnCreateGroup;
    private String username;
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        username = getIntent().getStringExtra("username");
        
        initViews();
        setupClickListeners();
        
        networkService = new NetworkService(this);
    }

    private void initViews() {
        etGroupName = findViewById(R.id.etGroupName);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
    }

    private void setupClickListeners() {
        btnCreateGroup.setOnClickListener(v -> createGroup());
    }

    private void createGroup() {
        String groupName = etGroupName.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toast.makeText(this, "Please enter group name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start announcing the group
        networkService.announceGroup(groupName, 1);
        
        // Navigate to chat activity
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("groupName", groupName);
        intent.putExtra("isCreator", true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkService != null) {
            networkService.shutdown();
        }
    }
}