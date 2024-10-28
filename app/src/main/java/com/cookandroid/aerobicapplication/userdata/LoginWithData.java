package com.cookandroid.aerobicapplication.userdata;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cookandroid.aerobicapplication.LoginActivity;
import com.cookandroid.aerobicapplication.MainActivity;
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
                    GetToFirebase(new CompleteListener() {
                        @Override
                        public void onSuccess() {
                            listener.onSuccess();
                        }
                        @Override
                        public void onFailure() {
                            listener.onFailure();
                        }
                    });
                }
                else
                    listener.onFailure();
            }
        });
    }

    private void GetToFirebase(CompleteListener listener){
        String id, name;
        mStore.collection(FirebaseData.user).document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    String userName = document.getData().get(FirebaseData.name).toString();
                    String userBirth = document.getData().get(FirebaseData.birthday).toString();
                    String userHeight = document.getData().get(FirebaseData.height).toString();
                    String userWeight = document.getData().get(FirebaseData.weight).toString();
                    String userDisease = document.getData().get(FirebaseData.disease).toString();
                    String userExperience = document.getData().get(FirebaseData.experience).toString();

                    AddToManager(strId, userName, userBirth, userHeight, userWeight, userDisease, userExperience);
                    listener.onSuccess();
                }
                else
                    listener.onFailure();
            }
        });
    }


    private void AddToManager(String id, String name, String birthday,
                              String height, String weight, String disease, String experience){

        UserdataManager.getInstance().setUserData(id, name, birthday, height, weight, disease, experience);

        Log.d("!Get Data!", "Age: " + UserdataManager.getInstance().getUserAge() +
                                    " / Bmi : " + UserdataManager.getInstance().getUserBmi()+
                                    " / Score : " + UserdataManager.getInstance().getUserScore());
    }
}
