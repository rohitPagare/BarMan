package com.orhotechnologies.barman.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class FireStoreModule {

    @Inject
    public FireStoreModule() {
    }

    @Provides
    public FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    @Provides
    public FirebaseFirestore getFirebaseFirestore(){
        return FirebaseFirestore.getInstance();
    }

    @Provides
    public FirebaseUser getFirebaseUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    @Provides
    public DocumentReference getUserDocRef(){
        return getFirebaseUser().getPhoneNumber()==null?null:
                getFirebaseFirestore().collection("users")
                        .document(getFirebaseUser().getPhoneNumber());
    }


}
