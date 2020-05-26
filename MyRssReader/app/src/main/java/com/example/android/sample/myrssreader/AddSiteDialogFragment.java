package com.example.android.sample.myrssreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AliasActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddSiteDialogFragment extends DialogFragment {
    public static final String RESULT_KEY_URL = "url";
    private EditText mEditText;

    // このフラグメントのインスタンスを返す
    public static AddSiteDialogFragment newInstance(Fragment target, int requestCode) {
        AddSiteDialogFragment fragment = new AddSiteDialogFragment();

        // 結果を返す先のフラグメントを設定しておく
        fragment.setTargetFragment(target, requestCode);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        // 入力ダイアログ用のView
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.dialog_input_url, null);

        mEditText = (EditText)contentView.findViewById(R.id.URLEditText);

        // ダイアログ生成用のビルダー
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.add_site)
                .setView(contentView)
                .setPositiveButton(R.string.dialog_button_add,      // 「登録する」ボタン
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Fragment fragment = getTargetFragment();
                            if (fragment != null) {
                                // 結果を返す先のフラグメントが存在する場合には、
                                // ユーザーが入力したURLを詰めて返す
                                Intent data = new Intent();
                                data.putExtra(RESULT_KEY_URL, mEditText.getText().toString());

                                fragment.onActivityResult(
                                        getTargetRequestCode(),
                                        Activity.RESULT_OK,
                                        data);
                            }
                        }
                    })
                .setNegativeButton(R.string.dialog_button_cancel,   // 「キャンセル」ボタン
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Fragment fragment = getTargetFragment();
                                if (fragment != null) {
                                    fragment.onActivityResult(
                                            getTargetRequestCode(),
                                            Activity.RESULT_CANCELED,
                                            null);
                                }
                            }
                        });
        // ダイアログを生成して返す
        return builder.create();
    }
}
