package com.example.amr.voteit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class comments extends Activity {
    DatabaseReference mdatabase;
    RecyclerView mPositionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

       mdatabase = FirebaseDatabase.getInstance().getReference().child("Location");
        mdatabase.keepSynced(true);
        mPositionList = (RecyclerView) findViewById(R.id.commentList);
        mPositionList.setHasFixedSize(true);
        mPositionList.setLayoutManager(new LinearLayoutManager(this));
    }

    public static class LocationviewHolder extends RecyclerView.ViewHolder {
        View mView;
        public LocationviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setRatingnum(long ratingnum) {
            RatingBar locRate = (RatingBar) mView.findViewById(R.id.Prate);
            locRate.setRating(ratingnum);
        }
        public void setEmail(String email) {
            TextView Email= (TextView) mView.findViewById(R.id.Pname);
            Email.setText(email);
        }
        public void setComment(String comment) {
            TextView Comment= (TextView) mView.findViewById(R.id.Pcomment);
            Comment.setText(comment);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<location, LocationviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<location, LocationviewHolder>(location.class, R.layout.comments_list, LocationviewHolder.class, mdatabase) {
            @Override
            protected void populateViewHolder(LocationviewHolder viewHolder, location model, int position) {
                viewHolder.setComment(model.getComment());
                viewHolder.setRatingnum(model.getRatingnum());
                viewHolder.setEmail(model.getEmail());
            }
        };
        mPositionList.setAdapter(firebaseRecyclerAdapter);
    }
}
