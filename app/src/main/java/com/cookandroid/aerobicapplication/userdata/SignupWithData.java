package com.cookandroid.aerobicapplication.userdata;

import android.widget.EditText;

import androidx.annotation.NonNull;

import com.cookandroid.aerobicapplication.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class SignupWithData {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore mStore;

    public SignupWithData() {
        mAuth = FirebaseAuth.getInstance();         // FirebaseAuth, Firestore 객체 초기화
        mStore = FirebaseFirestore.getInstance();
    }

    public String strName, strId, strPw, strBirthYear, strBirthMonth, strBirthDate, strHeight, strWeight, strDisease, strExperience;

    public void PushToString(EditText name, EditText id, EditText pw, EditText year, EditText month, EditText date,
                             EditText height, EditText weight, String disease, String experience){
        strName = name.getText().toString().trim();
        strId = id.getText().toString().trim();
        strPw = pw.getText().toString().trim();
        strBirthYear = year.getText().toString().trim();
        strBirthMonth = month.getText().toString().trim();
        strBirthDate = date.getText().toString().trim();
        strHeight = height.getText().toString().trim();
        strWeight = weight.getText().toString().trim();
        strDisease = disease;
        strExperience = experience;
    }

    public void SignupToFirebase(CompleteListener listener){
        mAuth.createUserWithEmailAndPassword(strId,strPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    listener.onSuccess();
                    SetDatatoFirebase();
                }
                else
                    listener.onFailure();
            }
        });
    }

    private void SetDatatoFirebase(){
        //uid 불러오기
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid().toString();

        //map 생성
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(FirebaseData.user, strId);
        userMap.put(FirebaseData.name, strName);
        userMap.put(FirebaseData.birthday, strBirthYear + "-" + strBirthMonth + "-" + strBirthDate);
        userMap.put(FirebaseData.height, strHeight);
        userMap.put(FirebaseData.weight, strWeight);
        userMap.put(FirebaseData.disease, strDisease);
        userMap.put(FirebaseData.experience, strExperience);

        //map 컨테이너에 merge -> 유저/해당 유저 uid 디렉토리에 저장
        mStore.collection(FirebaseData.user).document(uid).set(userMap, SetOptions.merge());
    }
}
