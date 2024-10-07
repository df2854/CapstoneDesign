package com.cookandroid.aerobicapplication.userdata;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cookandroid.aerobicapplication.LoginActivity;
import com.cookandroid.aerobicapplication.Manager.UserdataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginWithData {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mStore;
    public String strId, strPw;

    public LoginWithData() {
        mAuth = FirebaseAuth.getInstance();         // FirebaseAuth,FirebaseStore 객체 초기화
        mStore = FirebaseFirestore.getInstance();
    }

    public void LogintoFirebase(EditText id, EditText pw, CompleteListener listener){
        strId = id.getText().toString().trim();
        strPw = pw.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(strId,strPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    listener.onSuccess();
                    GetToFirebase();
                }
                else
                    listener.onFailure();
            }
        });
    }

    private void GetToFirebase(){
        String id, name;
        mStore.collection(FirebaseData.user).document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();

                String userN = document.getData().get(FirebaseData.name).toString();
                String userB = document.getData().get(FirebaseData.birthday).toString();

                AddToManager(strId, userN, userB);
            }
        });
    }


    private void AddToManager(String id, String name, String birthday){
        UserdataManager.getInstance().setUserData(id, name, birthday);

        Log.d("Tag", UserdataManager.getInstance().getUserId() + " " + UserdataManager.getInstance().getUserBirthday());
    }

}
