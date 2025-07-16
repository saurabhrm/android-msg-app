package com.msgapp.localchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<Group> groups;
    private OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }

    public GroupAdapter(List<Group> groups, OnGroupClickListener listener) {
        this.groups = groups;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.bind(group, listener);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGroupName, tvMemberCount, tvHostAddress;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
            tvHostAddress = itemView.findViewById(R.id.tvHostAddress);
        }

        public void bind(Group group, OnGroupClickListener listener) {
            tvGroupName.setText(group.getName());
            tvMemberCount.setText(group.getMemberCount() + " members");
            tvHostAddress.setText(group.getHostAddress());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGroupClick(group);
                }
            });
        }
    }
}