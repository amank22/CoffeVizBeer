package com.qurux.coffeevizbeer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.events.ItemTapAdapterEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Aman Kapoor on 19-11-2016.
 */

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {

    private int[] avatars = {
            R.drawable.ic_avatar1, R.drawable.ic_avatar2, R.drawable.ic_avatar3, R.drawable.ic_avatar4,
            R.drawable.ic_avatar5, R.drawable.ic_avatar6, R.drawable.ic_avatar7, R.drawable.ic_avatar8,
            R.drawable.ic_avatar9, R.drawable.ic_avatar10, R.drawable.ic_avatar11, R.drawable.ic_avatar13,
            R.drawable.ic_avatar14, R.drawable.ic_avatar15, R.drawable.ic_avatar16, R.drawable.ic_avatar17
    };

    public AvatarAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avatar, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.avatar.setImageResource(avatars[position]);
    }

    @Override
    public int getItemCount() {
        return avatars.length;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView avatar;

        ViewHolder(View v) {
            super(v);
            avatar = (ImageView) v;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            EventBus.getDefault().post(new ItemTapAdapterEvent(-1, getAdapterPosition()));
        }
    }
}
