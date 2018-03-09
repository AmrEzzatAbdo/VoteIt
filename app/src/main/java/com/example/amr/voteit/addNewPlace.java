package com.example.amr.voteit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.net.Network;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class addNewPlace extends AppCompatActivity {
    ImageView UImage;
    RatingBar ratingBar;
    EditText CommentReview, locationName;
    Button Submit;
    ImageButton Ulocation;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final int GALLARY_REQUIST = 1;
    int PLACE_PICKER_REQUEST = 1;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);
        //Progress Dialog
        mProgress = new ProgressDialog(this);
        //for uploading Image for Firebase
        mStorage = FirebaseStorage.getInstance().getReference();
        UImage = (ImageView) findViewById(R.id.UImage);
        UImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, GALLARY_REQUIST);
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Location");
        //Location Button
        Ulocation = (ImageButton) findViewById(R.id.Ulocation);
        //ratingBar
        ratingBar = (RatingBar) findViewById(R.id.UratingBar);
        locationName = (EditText) findViewById(R.id.locationName);
        //submit Button
        Submit = (Button) findViewById(R.id.Submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

        //for comemnt EditText
        CommentReview = (EditText) findViewById(R.id.CommentReview);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY_REQUIST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            UImage.setImageURI(mImageUri);
        }

        //map picker
        else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                if (place.getName()!=null){
                String toastMsg = String.format(place.getName().toString());
                    locationName.setText(toastMsg);
                }

            }
        }
    }

    private void startPosting() {
        mProgress.setMessage("Posting Location/Review...");
        mProgress.show();
        final String Review = CommentReview.getText().toString().trim();
        if (!TextUtils.isEmpty(Review) && mImageUri != null) {
            StorageReference filepath = mStorage.child("Location").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    DatabaseReference newLocation = mDatabase.push();
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser User = mAuth.getCurrentUser();
                    String Email = User.getEmail();
                    newLocation.child("email").setValue(Email);
                    newLocation.child("comment").setValue(Review);
                    newLocation.child("ratingnum").setValue(ratingBar.getRating());
                    newLocation.child("image").setValue(downloadUri.toString());
                    newLocation.child("lname").setValue(locationName.getText().toString());
                    mProgress.dismiss();
                    startActivity(new Intent(addNewPlace.this, MainActivity.class));

                }
            });
        } else
            //Toast.makeText(this, "Sorry Can't add this Location...", Toast.LENGTH_SHORT).show();
            Snackbar.make(null, "Sorry Can't add this Location...", Snackbar.LENGTH_LONG).show();
        mProgress.dismiss();

    }

    public void GoToPlacePicker(View view){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

}
