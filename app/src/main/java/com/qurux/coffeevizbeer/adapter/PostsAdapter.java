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
import com.qurux.coffeevizbeer.helper.CvBUtil;
import com.qurux.coffeevizbeer.local.CvBContract;
import com.qurux.coffeevizbeer.views.ThisThatView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Aman Kapoor on 19-11-2016.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private static final String regex = "Local:avatar[0-9]{1,2}";
    private Cursor dataCursor;
    private Context context;
    private ForegroundColorSpan colorSpan;
    private Drawable authorDrawable;

    public PostsAdapter(Context context, Cursor dataCursor) {
        this.dataCursor = dataCursor;
        this.context = context;
        colorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent));
        authorDrawable = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_vector_user_black);
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
        String desc = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_DESCRIPTION));
        String color = dataCursor.getString(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_COLOR));
        int liked = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_LIKED));
        int bookmarked = dataCursor.getInt(dataCursor.getColumnIndex(CvBContract.PostsEntry.COLUMN_BOOKMARKED));
        String[] titles = title.split(" ");
        SpannableString spanTitle = new SpannableString(title);
        spanTitle.setSpan(colorSpan,
                titles[0].length() + 1, titles[0].length() + 4, Spanned.SPAN_COMPOSING);
        holder.title.setText(spanTitle);
        holder.author.setText(author);
        holder.author.setCompoundDrawablesWithIntrinsicBounds(authorDrawable, null, null, null);
        holder.summary.setText(summary);
        holder.thisThatView.setBackgroundColor(Color.parseColor(color));
        holder.title.setBackgroundColor(Color.parseColor(color));
        setImageToThisThatView(holder, linkThis, linkThat);
        if (bookmarked == 0)
            holder.bookmark.setImageResource(R.drawable.ic_vector_bookmark_black);
        else if (bookmarked == 1)
            holder.bookmark.setImageResource(R.drawable.ic_vector_bookmark_red);
        if (liked == 0)
            holder.like.setImageResource(R.drawable.ic_vector_like);
        else if (liked == 1)
            holder.like.setImageResource(R.drawable.ic_vector_like_red);

        if (desc == null || desc.length() == 0) {
            holder.readmore.setVisibility(View.INVISIBLE);
        } else {
            holder.readmore.setVisibility(View.VISIBLE);
        }
    }

    private void setImageToThisThatView(ViewHolder holder, String linkThis, String linkThat) {
        if (linkThis.matches(regex)) {
            holder.thisThatView.removeImage(0);
            int localPos = Character.getNumericValue(linkThis.charAt(linkThis.length() - 2)) * 10 +
                    Character.getNumericValue(linkThis.charAt(linkThis.length() - 1));
            CvBUtil.log("local position OF " + linkThis + ":" + String.valueOf(localPos));
            holder.thisThatView.getHolder().get(0).getHierarchy().setPlaceholderImage(CvBUtil.getAvatarResId(localPos));
        } else {
            holder.thisThatView.getHolder().get(0).getHierarchy().setPlaceholderImage(R.drawable.circle_blue);
            holder.thisThatView.setImage(0, linkThis);
        }
        if (linkThat.matches(regex)) {
            int localPos = Character.getNumericValue(linkThat.charAt(linkThat.length() - 2)) * 10 +
                    Character.getNumericValue(linkThat.charAt(linkThat.length() - 1));
            CvBUtil.log("local position OF THAT  " + linkThat + ":" + String.valueOf(localPos));
            holder.thisThatView.removeImage(1);
            holder.thisThatView.getHolder().get(1).getHierarchy().setPlaceholderImage(CvBUtil.getAvatarResId(localPos));
        } else {
            holder.thisThatView.getHolder().get(1).getHierarchy().setPlaceholderImage(R.drawable.circle_green);
            holder.thisThatView.setImage(1, linkThat);
        }
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            final int adapterPosition = getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            dataCursor.moveToPosition(adapterPosition);
            if (view.getId() == like.getId()) {
                EventBus.getDefault().post(new ItemTapAdapterEvent(ItemTapEvent.TAP_LIKED, dataCursor));
            } else if (view.getId() == bookmark.getId()) {
                EventBus.getDefault().post(new ItemTapAdapterEvent(ItemTapEvent.TAP_BOOKMARKED, dataCursor));
            } else if (view.getId() == readmore.getId()) {
                EventBus.getDefault().post(new ItemTapAdapterEvent(ItemTapEvent.TAP_READMORE, dataCursor));
            }
        }
    }
}
