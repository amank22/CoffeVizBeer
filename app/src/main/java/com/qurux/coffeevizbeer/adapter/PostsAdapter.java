package com.qurux.coffeevizbeer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qurux.coffeevizbeer.R;
import com.qurux.coffeevizbeer.events.ItemTapAdapterEvent;
import com.qurux.coffeevizbeer.events.ItemTapEvent;
import com.qurux.coffeevizbeer.helper.ThisThatView;
import com.qurux.coffeevizbeer.local.CvBContract;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Aman Kapoor on 19-11-2016.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Cursor dataCursor;
    private Context context;

    public PostsAdapter(Context context, Cursor dataCursor) {
        this.dataCursor = dataCursor;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);
        String title = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_TITLE));
        String author = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_AUTHOR));
        String summary = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_SUMMARY));
        String linkThis = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LINK_THIS));
        String linkThat = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LINK_THAT));
        String color = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_COLOR));
        int liked = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LIKED));
        int bookmarked = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_BOOKMARKED));
        String[] titles = title.split(" ");
        SpannableString spanTitle = new SpannableString(title);
        spanTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent)),
                titles[0].length() + 1, titles[0].length() + 4, Spanned.SPAN_COMPOSING);
        holder.title.setText(spanTitle);
        holder.author.setText(author);
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_vector_user_black);
        holder.author.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        holder.summary.setText(summary);
        holder.thisThatView.setBackgroundColor(Color.parseColor(color));
        holder.title.setBackgroundColor(Color.parseColor(color));
        try {
            holder.thisThatView.setImageToAllImages(new String[]{linkThis, linkThat});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bookmarked == 0)
            holder.bookmark.setImageResource(R.drawable.ic_vector_bookmark_black);
        else if (bookmarked == 1)
            holder.bookmark.setImageResource(R.drawable.ic_vector_bookmark_red);
        if (liked == 0)
            holder.like.setImageResource(R.drawable.ic_vector_like);
        else if (liked == 1)
            holder.like.setImageResource(R.drawable.ic_vector_like_red);
    }

    @Override
    public int getItemCount() {
        return dataCursor != null ? dataCursor.getCount() : 0;
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public Cursor getCursorAtPosition(int pos) {
        dataCursor.moveToPosition(pos);
        return dataCursor;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, author, summary, readmore;
        ImageButton like, bookmark, share;
        ThisThatView thisThatView;

        ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.post_item_title);
            author = (TextView) v.findViewById(R.id.post_item_author);
            summary = (TextView) v.findViewById(R.id.post_item_summary);
            readmore = (TextView) v.findViewById(R.id.post_item_read_more);
            like = (ImageButton) v.findViewById(R.id.button_like);
            bookmark = (ImageButton) v.findViewById(R.id.button_bookmark);
            share = (ImageButton) v.findViewById(R.id.button_share);
            thisThatView = (ThisThatView) v.findViewById(R.id.this_that_view_item);
            like.setOnClickListener(this);
            bookmark.setOnClickListener(this);
            share.setOnClickListener(this);
            readmore.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (view.getId() == like.getId()) {
                EventBus.getDefault().post(new ItemTapAdapterEvent(ItemTapEvent.TAP_LIKED, getAdapterPosition()));
            } else if (view.getId() == bookmark.getId()) {
                EventBus.getDefault().post(new ItemTapAdapterEvent(ItemTapEvent.TAP_BOOKMARKED, getAdapterPosition()));
            } else if (view.getId() == readmore.getId()) {
                EventBus.getDefault().post(new ItemTapAdapterEvent(ItemTapEvent.TAP_READMORE, getAdapterPosition()));
            }
        }
    }
}
