package com.orhotechnologies.barman;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.orhotechnologies.barman.dashboard.Dashboard;
import com.orhotechnologies.barman.databinding.ActivityVerifyphoneBinding;
import com.orhotechnologies.barman.di.FireStoreModule;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class VerifyPhoneActivity extends AppCompatActivity {

    private static final String TAG = VerifyPhoneActivity.class.getSimpleName();

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    @Inject
    FireStoreModule fireStoreModule;

    //firebase auth object
    private FirebaseAuth mAuth;

    private ActivityVerifyphoneBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyphoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initializing objects
        mAuth = fireStoreModule.getFirebaseAuth();

        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("mobile");
        sendVerificationCode(phoneNumber);

        binding.btnSubmit.setOnClickListener(v->{
            String code = Objects.requireNonNull(binding.etvCode.getText()).toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                binding.etlayCode.setError("Enter valid code");
                binding.etvCode.requestFocus();
                return;
            }

            //creating the credential
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            //signing the user
            signInWithPhoneAuthCredential(credential);
        });
    }

    //the method is sending verification code
    //the country id is concatenated
    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //the callback to detect the verification status
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:" + credential);

            //Getting the code sent by SMS
            String code = credential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                binding.etvCode.setText(code);
            }

            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);

            String message = "Varification Failed";

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                message = "Invalid request";
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                message = "quota for the project has been exceeded";
            }

            // Show a message and update the UI
            Toast.makeText(VerifyPhoneActivity.this,message,Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                       // FirebaseUser user = task.getResult().getUser();
                        // Update UI
                        Intent intent = new Intent(VerifyPhoneActivity.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());

                        String message = "Somthing is wrong, we will fix it soon...";

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            message = "Invalid code entered...";
                        }

                        Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                        snackbar.setAction("Dismiss", v -> { });
                        snackbar.show();
                    }
                });
    }
}