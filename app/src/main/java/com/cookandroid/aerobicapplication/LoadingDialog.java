package com.cookandroid.aerobicapplication;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class LoadingDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCanceledOnTouchOutside(false);                                        // 로딩 화면 터치 안되게
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 배경 투명하게
        return dialog;
    }
}

//LoadingDialog loadingDialog = new LoadingDialog();
//loadingDialog.show(getSupportFragmentManager(), "loading"); // 로딩화면 실행
//loadingDialog.dismiss(); // 로딩화면 종료
//으로 사용
