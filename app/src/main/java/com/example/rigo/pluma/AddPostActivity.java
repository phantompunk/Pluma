package com.example.rigo.pluma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPostActivity extends AppCompatActivity {

    @BindView(R.id.add_title) EditText titleEt;
    @BindView(R.id.add_body) EditText bodyEt;
    @BindView(R.id.add_publish) Button publishBt;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference database;
    private DatabaseReference userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Blog");
        userDb = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
    }

    @OnClick(R.id.add_publish)
    public void onPublish() {
        final String title = titleEt.getText().toString();
        final String body = bodyEt.getText().toString();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
            final DatabaseReference newPost = database.push();

            Log.d("User", userDb.child(user.getUid()).child("name").getKey());
            userDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newPost.child("title").setValue(title);
                    newPost.child("body").setValue(body);
                    newPost.child("userID").setValue(user.getUid());
                    newPost.child("name").setValue(dataSnapshot.child("name").getValue());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        startActivity(new Intent(this, Feed.class));
        finish();
    }
}
