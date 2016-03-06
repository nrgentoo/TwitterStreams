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

    // --------------------------------------------------------------------------------------------
    //      FIELDS
    // --------------------------------------------------------------------------------------------

    private List items = new ArrayList<>();

    // --------------------------------------------------------------------------------------------
    //      CONSTRUCTOR
    // --------------------------------------------------------------------------------------------

    public TimelineAdapter(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // add divider
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext()));
    }

    // --------------------------------------------------------------------------------------------
    //      PUBLIC METHODS
    // --------------------------------------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View tweetView = layoutInflater.inflate(R.layout.item_tweet, parent, false);
        return new TweetVH(tweetView);
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
}
