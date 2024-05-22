package com.example.rally;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.data.Route;

import java.util.ArrayList;
import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ButtonViewHolder> {

    private List<Route> routes = new ArrayList<>();

    public void setRoutesData(List<Route> routes) {
        this.routes = routes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_button, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        Route route = routes.get(position);
        String buttonText = route.name;
        holder.buttonItem.setText(buttonText);
        holder.buttonItem.setOnClickListener(v -> {
            Context context = holder.buttonItem.getContext();
            Intent intent = new Intent(context, RouteActivity.class);
            intent.putExtra(RouteActivity.MARKERS_LNG, route.markersLng);
            intent.putExtra(RouteActivity.MARKERS_LOG, route.markersLog);
            intent.putExtra(RouteActivity.WARNINGS_LNG, route.warningLng);
            intent.putExtra(RouteActivity.WARNINGS_LOG, route.warningsLog);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button buttonItem;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonItem = itemView.findViewById(R.id.trasa_button);
        }
    }
}
