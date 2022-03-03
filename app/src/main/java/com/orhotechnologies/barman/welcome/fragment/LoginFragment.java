package com.orhotechnologies.barman.welcome.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.narify.netdetect.NetDetect;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.dashboard.Dashboard;
import com.orhotechnologies.barman.databinding.FragmentLoginBinding;
import com.orhotechnologies.barman.di.FireStoreModule;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.aabhasjindal.otptextview.OTPListener;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    @Inject
    FireStoreModule fireStoreModule;

    private FirebaseAuth mAuth;

    private FragmentLoginBinding binding;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing objects
        mAuth = fireStoreModule.getFirebaseAuth();
        //login button
        binding.btnSingin.setOnClickListener(v -> login());
        //set otp call listner
        setOTPListner();
        //set privacy text
       setPrivacyText();
    }

    private void setPrivacyText(){
        SpannableString spannable = new SpannableString(getString(R.string.privacy));
        spannable.setSpan(new ForegroundColorSpan(requireContext().getColor(R.color.purple_500)),32,46, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(requireContext().getColor(R.color.purple_500)),51,spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new UnderlineSpan(),32,46, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new UnderlineSpan(),51,spannable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        binding.tvPrivacy.setText(spannable);
    }

    private void setOTPListner(){
        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                //creating the credential
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                //signing the user
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void login() {
        //stop btn click
        binding.btnSingin.setClickable(false);

        if (!validate()) {
            binding.etlayPhone.setError("Enter Valide number");
            binding.etvPhone.requestFocus();
            binding.btnSingin.setClickable(true);
            return;
        }


        //check internet
        NetDetect.check(isConnected -> {
            if(!isConnected)noNetwork();
            else {
                //hide group login
                binding.groupLogin.setVisibility(View.GONE);
                //show progress
                binding.progress.setVisibility(View.VISIBLE);

                //send to verification
                sendVerificationCode(phoneNumber);
            }
        });


    }

    private void noNetwork(){
        Utility.showSnakeBar(requireActivity(),"No Internet Connection, Please Try Later");
        //start btn click
        binding.btnSingin.setClickable(true);
    }

    private String phoneNumber;

    private boolean validate() {
        if (binding.etvPhone.getText() == null) return false;
        phoneNumber = binding.etvPhone.getText().toString().trim();
        return phoneNumber.length() == 10 && !Utility.notDigitsString(phoneNumber);
    }

    private void sendVerificationCode(String phoneNumber) {
        binding.tvVerifyphone.setText(getString(R.string.verifyphone).concat("+91XXXXXXXX").concat(phoneNumber.substring(8)));
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(requireActivity())                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");

                        // FirebaseUser user = task.getResult().getUser();
                        // Update UI
                        Intent intent = new Intent(requireActivity(), Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w("TAG", "signInWithCredential:failure", task.getException());

                        String message = "Somthing is wrong, we will fix it soon...";

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            message = "Invalid code entered...";
                        }


                        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
                        snackbar.setAction("Dismiss", v -> { });
                        snackbar.show();

                    }
                });
    }

    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d("TAG", "onVerificationCompleted:" + credential);

            //Getting the code sent by SMS
            String code = credential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                binding.otpView.setOTP(code);
            }

            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("TAG", "onVerificationFailed", e);

            String message = "Varification Failed";

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                message = "Invalid request";
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                message = "quota for the project has been exceeded";
            }
            //start button click
            binding.btnSingin.setClickable(true);
            //hide progress
            binding.progress.setVisibility(View.GONE);
            //show group login
            binding.groupLogin.setVisibility(View.VISIBLE);

            // Show a message and update the UI
            Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
            snackbar.setAction("Dismiss", v -> { });
            snackbar.show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("TAG", "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            //hide progress
            binding.progress.setVisibility(View.GONE);
            //show verify
            binding.groupVerify.setVisibility(View.VISIBLE);
        }
    };

}