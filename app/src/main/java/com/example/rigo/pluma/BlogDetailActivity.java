package com.example.rigo.pluma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlogDetailActivity extends AppCompatActivity {

    private static String TAG = BlogDetailActivity.class.getSimpleName();

    @BindView(R.id.blog_title) TextView titleTv;
    @BindView(R.id.blog_body) TextView bodyTv;
    @BindView(R.id.blog_author) TextView authorTv;
    @BindView(R.id.blog_post_comment) EditText postCommentEt;
    @BindView(R.id.blog_recyclerview) RecyclerView commentRecyclerview;

    private String postKey = null;
    private String postUID = null;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceComment;
    private DatabaseReference databaseReferenceUser;
    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);
        ButterKnife.bind(this);

        commentRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerview.setHasFixedSize(false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Blog");
        databaseReferenceComment = FirebaseDatabase.getInstance().getReference("Comment");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
        postKey = getIntent().getExtras().getString("postKey");

        databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String postTitle = (String) dataSnapshot.child("title").getValue();
                String postBody = (String) dataSnapshot.child("body").getValue();
                String postAuthor = (String) dataSnapshot.child("name").getValue();
                postUID = (String) dataSnapshot.child("userID").getValue();

                authorTv.setText("by " + postAuthor);
                titleTv.setText(postTitle);
                bodyTv.setText(postBody);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postCommentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEND) {
                    onCommentPost();
                    handled = true;
                }
                return handled;
            }
        });

        Query query = FirebaseDatabase.getInstance().getReference("Comment").child(postKey);
        FirebaseRecyclerOptions<Comment> options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(CommentViewHolder holder, int position, Comment comment) {
                String commentKey = getRef(position).getKey();
                Log.d(TAG, "CommentBody: " + comment.getBody());
                holder.commentBodyTv.setText(comment.getBody());
                holder.commentAuthorTv.setText(comment.getAuthor());
                holder.commentTimeTv.setText(parseTimeStamp(comment.getDate()));
            }

            @Override
            public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_comment,parent,false);
                return new CommentViewHolder(view);
            }
        };

        commentRecyclerview.setAdapter(firebaseRecyclerAdapter);
    }

    private String parseTimeStamp(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d',' hh:mm a");
        return sdf.format(calendar.getTime());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.clear();
        if (user.getUid().equals(postUID)) {
            getMenuInflater().inflate(R.menu.detail_menu, menu);
        }
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_remove_post:
                databaseReference.child(postKey).removeValue();
                startActivity(new Intent(this, Feed.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }


    public void onCommentPost() {
        final String newCommentText = postCommentEt.getText().toString();

        if (!TextUtils.isEmpty(newCommentText)) {
//            DatabaseReference newComment = databaseReference.child(postKey).push();
            final DatabaseReference newComment = databaseReferenceComment.child(postKey).push();

            databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("User", "Name: " + dataSnapshot.child("name").getValue());
                    newComment.child("body").setValue(newCommentText);
                    newComment.child("author").setValue(dataSnapshot.child("name").getValue());
                    newComment.child("date").setValue(System.currentTimeMillis());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        postCommentEt.setText("");

    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.blog_comment_body) TextView commentBodyTv;
        @BindView(R.id.blog_comment_author) TextView commentAuthorTv;
        @BindView(R.id.blog_comment_timestamp) TextView commentTimeTv;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
