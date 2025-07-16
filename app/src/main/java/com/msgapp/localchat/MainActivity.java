package com.msgapp.localchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText etUsername;
    private Button btnCreateGroup, btnRefreshGroups;
    private RecyclerView rvGroups;
    private GroupAdapter groupAdapter;
    private List<Group> availableGroups;
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        
        networkService = new NetworkService(this);
        startNetworkDiscovery();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        btnRefreshGroups = findViewById(R.id.btnRefreshGroups);
        rvGroups = findViewById(R.id.rvGroups);
    }

    private void setupRecyclerView() {
        availableGroups = new ArrayList<>();
        groupAdapter = new GroupAdapter(availableGroups, this::joinGroup);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        rvGroups.setAdapter(groupAdapter);
    }

    private void setupClickListeners() {
        btnCreateGroup.setOnClickListener(v -> createGroup());
        btnRefreshGroups.setOnClickListener(v -> refreshGroups());
    }

    private void createGroup() {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("isCreator", true);
        startActivity(intent);
    }

    private void joinGroup(Group group) {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("groupName", group.getName());
        intent.putExtra("groupHost", group.getHostAddress());
        intent.putExtra("isCreator", false);
        startActivity(intent);
    }

    private void startNetworkDiscovery() {
        networkService.startDiscovery(groups -> {
            runOnUiThread(() -> {
                availableGroups.clear();
                availableGroups.addAll(groups);
                groupAdapter.notifyDataSetChanged();
            });
        });
    }

    private void refreshGroups() {
        startNetworkDiscovery();
        Toast.makeText(this, "Refreshing groups...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkService != null) {
            networkService.stopDiscovery();
        }
    }
}