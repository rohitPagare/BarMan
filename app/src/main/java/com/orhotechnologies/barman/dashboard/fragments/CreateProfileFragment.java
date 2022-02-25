package com.orhotechnologies.barman.dashboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Transaction;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentCreateprofileBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.models.Member;
import com.orhotechnologies.barman.models.User;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class CreateProfileFragment extends Fragment {

    private FragmentCreateprofileBinding binding;

    @Inject
    FireStoreModule fireStoreModule;


    public CreateProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateprofileBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCreate.setOnClickListener(v->{
            //validate all fields
            if(Objects.requireNonNull(binding.etvHotelname.getText()).toString().trim().isEmpty()){
                binding.etlayHotelname.setError("Enter Valide HotelName");
            }else if(Objects.requireNonNull(binding.etvSaname.getText()).toString().trim().isEmpty()){
                binding.etlaySaname.setError("Enter Valide Name");
            }else if(Objects.requireNonNull(binding.etvSaphone.getText()).toString().trim().isEmpty() || binding.etvSaphone.getText().toString().trim().length()!=10){
                binding.etlaySaphone.setError("Enter Valide Phone");
            }else if(Objects.requireNonNull(binding.etvSapassword.getText()).toString().trim().isEmpty()){
                binding.etlaySapassword.setError("Enter Valide Password");
            }else {
                
                User user = new User(fireStoreModule.getFirebaseAuth().getUid(),
                        fireStoreModule.getFirebaseUser().getPhoneNumber(),
                        binding.etvHotelname.getText().toString().trim(),1,1);

                Member member = new Member(binding.etvSaname.getText().toString().trim(),
                        binding.etvSaphone.getText().toString().trim(),
                        binding.etvSapassword.getText().toString().trim(),
                        "SUPERADMIN",false);

                DocumentReference userDocRef = fireStoreModule.getUserDocRef();
                DocumentReference memberDocRef = userDocRef.collection(Utility.DB_MEMBER).document(member.getPhone());

                //insert user and member
                fireStoreModule.getFirebaseFirestore().runTransaction((Transaction.Function<Void>) transaction -> {
                    transaction.set(userDocRef,user);
                    transaction.set(memberDocRef,member);
                    return null;
                }).addOnSuccessListener(requireActivity(), aVoid -> {
                    //goto main fragment
                    Navigation.findNavController(view).navigate(CreateProfileFragmentDirections.actionCreateProfileFragmentToMainFragment());

                }).addOnFailureListener(requireActivity(), e -> {
                    //show Error
                    Utility.showSnakeBar(requireActivity(),e.getMessage());
                });
            }
        });
    }
}