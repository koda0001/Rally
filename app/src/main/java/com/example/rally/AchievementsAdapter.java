package com.example.rally;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.data.Achievement;

import java.util.ArrayList;
import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private List<Pair<Achievement, Number>> achievementsWithProgress = new ArrayList<>();
    private Context context;

    public void setAchievementsData(List<Pair<Achievement, Number>> achievementsWithProgress, Context context) {
        this.achievementsWithProgress = achievementsWithProgress;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_item, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = achievementsWithProgress.get(position).first;
        Number progress = achievementsWithProgress.get(position).second;
        holder.description.setText(achievement.description);
        holder.progress.setText(
                String.format(
                        context.getResources().getString(R.string.achievement_progress),
                        progress,
                        achievement.target
                )
        );
    }

    @Override
    public int getItemCount() {
        return achievementsWithProgress.size();
    }

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        TextView progress;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            progress = itemView.findViewById(R.id.progress);
        }
    }

}
