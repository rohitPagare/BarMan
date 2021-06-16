package com.orhotechnologies.barman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhotechnologies.barman.models.User;

public class Welcome extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        mAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.google_signin);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    @Override
    protected void onStart() {
        super.onStart();
        openNext();
    }

    private void openNext() {
        //delay to open next activity 2sec
        new Handler(Looper.getMainLooper()).postDelayed(this::updateUI, 250);
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Utilities.showSnakeBar(Welcome.this,"Authentication Failed.");
                        }

                        // ...
                    }
                });
    }

    private void updateUI(){
        //get current singed user instance
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if user is signed in (non-null) and update UI accordingly.
        if (currentUser == null) {
            signInButton.setVisibility(View.VISIBLE);
            signInButton.setOnClickListener(v -> {
                signIn();
            });
            //else show dashboard
        } else {
            if(currentUser.getEmail()==null){
                Toast.makeText(this,"Email of this user is empty",Toast.LENGTH_LONG).show();
                return;
            }
            //check if userid valide
            FirebaseFirestore.getInstance().collection(Utilities.DB_USER)
                    .document(currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Welcome.this,"Somthing went wrong,Try Later!",Toast.LENGTH_LONG).show();
                            }else if(task.isSuccessful() && task.getResult() != null && task.getResult().exists()){
                                User user = task.getResult().toObject(User.class);
                                Intent intent = new Intent(Welcome.this, Dashboard.class);
                                intent.putExtra(Utilities.KEY_USER,user);
                                startActivity(intent);
                            }else {
                                startActivity(new Intent(Welcome.this, CreateProfile.class));
                            }

                            //animate
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            //finish this activity
                            Welcome.this.finish();

                        }
                    });
        }
    }

}