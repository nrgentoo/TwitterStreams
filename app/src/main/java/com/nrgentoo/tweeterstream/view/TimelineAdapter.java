package com.nrgentoo.tweeterstream.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nrgentoo.tweeterstream.R;
import com.nrgentoo.tweeterstream.view.components.DividerItemDecoration;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Timeline adapter
 */
public class TimelineAdapter extends RecyclerView.Adapter {

    private static final int VISIBLE_THRESHOLD = 5;

    private static final int VIEW_TWEET = 0;
    private static final int VIEW_PROGRESS = 1;

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    private List items = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;

    private int lastVisibleCount, totalItemCount;
    private boolean loading;

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public TimelineAdapter(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // add divider
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext()));

        // add onScroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleCount = layoutManager.findLastVisibleItemPosition();

                if (!TimelineAdapter.this.items.isEmpty() && !loading &&
                        totalItemCount <= (lastVisibleCount + VISIBLE_THRESHOLD)) {
                    // End has been reached. Do something
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.loadMore();
                    }

                    showLoading();
                }
            }
        });
    }

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);

        if (item instanceof Tweet) {
            return VIEW_TWEET;
        } else if (item instanceof Progress) {
            return VIEW_PROGRESS;
        } else {
            throw new IllegalArgumentException("Unsupported view type class: " + item.getClass()
                    .getCanonicalName());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TWEET:
                View tweetView = layoutInflater.inflate(R.layout.item_tweet, parent, false);
                return new TweetVH(tweetView);
            case VIEW_PROGRESS:
                View progressView = layoutInflater.inflate(R.layout.layout_progress_small, parent,
                        false);
                return new ProgressVH(progressView);
            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TweetVH) {
            Tweet tweet = (Tweet) items.get(position);
            ((TweetVH) holder).bind(tweet);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setTweets(List<Tweet> tweets) {
        items.clear();
        items.addAll(tweets);
        notifyDataSetChanged();
    }

    public void addTweets(boolean atTop, List<Tweet> tweets) {
        if (atTop) {
            items.addAll(0, tweets);
            notifyItemRangeInserted(0, tweets.size());
        } else {
            int insertPos = items.size() - 1;
            items.addAll(insertPos, tweets);
            notifyItemRangeInserted(insertPos, tweets.size());

            hideProgress();
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void hideProgress() {
        if (loading) {
            loading = false;

            // hide progress
            Object progress = items.get(items.size() - 1);
            if (progress instanceof Progress) {
                int removePos = items.size() - 1;
                items.remove(progress);
                notifyItemRemoved(removePos);
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    //      PRIVATE METHODS
    // --------------------------------------------------------------------------------------------

    private void showLoading() {
        if (!loading) {
            loading = true;

            // show progress
            int insertPos = items.size();
            items.add(new Progress());
            notifyItemInserted(insertPos);
        }
    }

    // --------------------------------------------------------------------------------------------
    //      VIEW HOLDER
    // --------------------------------------------------------------------------------------------

    public class TweetVH extends RecyclerView.ViewHolder {

        // views
        ImageView iv_avatar;
        TextView tv_author;
        TextView tv_nickname;
        TextView tv_date;
        TextView tv_text;

        public TweetVH(View itemView) {
            super(itemView);

            // inflate views
            iv_avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tv_author = (TextView) itemView.findViewById(R.id.tv_author);
            tv_nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
        }

        public void bind(Tweet tweet) {
            Picasso.with(itemView.getContext())
                    .load(tweet.user.profileImageUrlHttps)
                    .into(iv_avatar);

            tv_author.setText(tweet.user.name);
            tv_nickname.setText("@" + tweet.user.screenName);
            tv_text.setText(tweet.text);

            // set date Sun Mar 06 09:37:37 +0000 2016
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss zz yyyy");
                Date date = sdf.parse(tweet.createdAt);
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(itemView.getContext(), date.getTime());
                tv_date.setText(timeAgo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ProgressVH extends RecyclerView.ViewHolder {

        public ProgressVH(View itemView) {
            super(itemView);
        }
    }

    // --------------------------------------------------------------------------------------------
    //      INTERFACES
    // --------------------------------------------------------------------------------------------

    public interface OnLoadMoreListener {

        void loadMore();
    }

    // --------------------------------------------------------------------------------------------
    //      CLASSES
    // --------------------------------------------------------------------------------------------

    private static class Progress { }
}
