package com.example.amr.voteit;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.logging.Handler;

import bolts.Task;

import static android.content.ContentValues.TAG;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    RecyclerView mPositionList;
    TextView  UserName;
    ImageButton fab, UserSignOut;
    ImageView UserPicture;
    ProgressDialog progress;
    //for google signin
    GoogleApiClient mGoogleApiClient;
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private DatabaseReference mdatabase;
    private StorageReference mStorage;
    private AdView mAdView;
    ArrayAdapter x;
    //chick network Connection
    ConnectivityManager connectivity;
    NetworkInfo mobileNetworkInfo, WifiNetwork;
    RelativeLayout chickConnection, header, UserData;
    FirebaseRecyclerAdapter<location, LocationviewHolder> firebaseRecyclerAdapter;
    //for test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ads
         mAdView = (AdView) findViewById(R.id.adView);
        MobileAds.initialize(this,"ca-app-pub-1623299335056519~7563250082");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdRequest request = new AdRequest.Builder()
                .addTestDevice("ca-app-pub-1623299335056519/5732606742")  // An example device ID
                .build();
        //recycle View
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Location");
        mStorage= FirebaseStorage.getInstance().getReference();
        mdatabase.keepSynced(true);
        mPositionList = (RecyclerView) findViewById(R.id.Positionlist);
        mPositionList.setHasFixedSize(true);
        mPositionList.setLayoutManager(new LinearLayoutManager(this));
        //headers and user data
        header = (RelativeLayout) findViewById(R.id.header);
        UserData = (RelativeLayout) findViewById(R.id.userData);
        UserData.setVisibility(View.GONE);
        UserName = (TextView) findViewById(R.id.UserName);
        UserPicture = (ImageView) findViewById(R.id.UserImage);
        UserSignOut = (ImageButton) findViewById(R.id.SignOutUser);
        progress = new ProgressDialog(this);
        UserSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNetworkInfo.isConnected() || WifiNetwork.isConnected()) {
                    progress.setTitle("Signing Out...");
                    progress.show();
                    try {
                        progress.show();
                        mAuth.signOut();
                        UserData.setVisibility(View.GONE);
                        header.setVisibility(View.VISIBLE);
                        progress.dismiss();

                    } catch (Exception e) {
                        progress.setTitle("No Network Connection..");
                    }
                    progress.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Please Connect Network", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //chick network Connection
        chickConnection = (RelativeLayout) findViewById(R.id.chickConnection);
        connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiNetwork = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileNetworkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetworkInfo.isConnected() || WifiNetwork.isConnected()) {
            chickConnection.setVisibility(View.GONE);
        }

        fab = (ImageButton) findViewById(R.id.Floatingbutton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNetworkInfo.isConnected() || WifiNetwork.isConnected()) {
                    fab.setVisibility(View.VISIBLE);
                    if (mAuth.getCurrentUser() != null) {
                        startActivity(new Intent(MainActivity.this, addNewPlace.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Plz Sign In to push new Location Review..", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    fab.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "plz connect networkand", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Google SignIn
        mAuth = FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //whats happend in connected
                    header.setVisibility(View.GONE);
                    UserData.setVisibility(View.VISIBLE);
                    UserName.setText(mAuth.getCurrentUser().getDisplayName());
                    UserPicture.setImageURI(mAuth.getCurrentUser().getPhotoUrl());
                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(MainActivity.this, "Sorry can't sign in", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

//search view
        SearchView search= (SearchView) findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mdatabase.orderByChild("_searchLastName")
                        .startAt(query)
                        .endAt(query+"\uf8ff");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

    }

    //google signin button
    public void GoogleSignIn(View view) {
        progress.show();
        signIn();
        progress.dismiss();
    }


    //for google signIn


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);


         firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<location, LocationviewHolder>(location.class, R.layout.list_item, LocationviewHolder.class, mdatabase) {
            @Override
            protected void populateViewHolder(LocationviewHolder viewHolder, location model, int position) {
                viewHolder.setLname(model.getLname());
                viewHolder.setRatingnum(model.getRatingnum());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setlocation(model.getLocation());
               // Toast.makeText(MainActivity.this, "Plz Sign In to push new Location Review..", Toast.LENGTH_SHORT).show();
            }
        };
        mPositionList.setAdapter(firebaseRecyclerAdapter);
    }

    public void CommentClick(View view) {
        startActivity(new Intent(MainActivity.this,comments.class));
    }

    public static class LocationviewHolder extends RecyclerView.ViewHolder {
        View mView;
        public LocationviewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ImageButton locBtn= (ImageButton) mView.findViewById(R.id.direction);
            locBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(i);*/
                }
            });
            /*final RatingBar ratingBar=(RatingBar) mView.findViewById(R.id.ratingBar);
            ratingBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    mdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    return true;
                }
            });*/
        }
        public void setLname(String lname){
            final TextView locName = (TextView) mView.findViewById(R.id.VName);
            locName.setText(lname);
        }

        public void setRatingnum(long ratingnum) {
            TextView locRate = (TextView) mView.findViewById(R.id.RatingValue);
            RatingBar r= (RatingBar) mView.findViewById(R.id.ratingBar);
            r.setRating(ratingnum);
            ratingnum/=3;
            String Xz=String.valueOf(ratingnum);
            locRate.setText(Xz);
        }
        public String setlocation(String location){
            return location;
        }

        public void setImage(final Context ctx, final String image) {
            final ImageView LocationImage = (ImageView) mView.findViewById(R.id.VImage);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(LocationImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(LocationImage);
                }
            });

        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        } else {
            Toast.makeText(this, "Error in Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                Log.d(TAG, "onComplete:signInWithCredential " + task.isSuccessful());


                if (!task.isSuccessful()) {

                    Log.w("Main Activity", "onComplete:signInWithCredential ", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication faild", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }



}
