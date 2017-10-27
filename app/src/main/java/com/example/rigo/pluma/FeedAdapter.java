package com.example.rigo.pluma;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

/**
 * Created by rigo on 10/24/17.
 */

public class FeedAdapter extends FirebaseRecyclerAdapter<Blog, FeedAdapter.BlogViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FeedAdapter(FirebaseRecyclerOptions<Blog> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(BlogViewHolder holder, int position, Blog model) {

    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder {
        public BlogViewHolder(View itemView) {
            super(itemView);
        }
    }
}
