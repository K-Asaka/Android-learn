package com.example.android.sample.myrssreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * URL入力用のFragment
 */
public class DeleteSiteDialogFragment extends DialogFragment {

    private static final String RESULT_KEY_SITE_ID = "site_id";

    // このダイアログフラグメントのインスタンスを生成して返す
    public static DeleteSiteDialogFragment newInstance(
            Fragment target, int requestCode, long siteId) {

        // このダイアログフラグメントのインスタンスを生成する
        DeleteSiteDialogFragment fragment = new DeleteSiteDialogFragment();

        // 削除対象のサイトのIDをBundleに詰める
        Bundle args = new Bundle();
        args.putLong(RESULT_KEY_SITE_ID, siteId);
        fragment.setArguments(args);

        // Fragment#setTargetFragment()で、結果を返す対象のフラグメントを指定できる
        fragment.setTargetFragment(target, requestCode);

        return fragment;
    }

    // ダイアログを表示する
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // AlertDialogを作るためのビルダー
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // 表示するダイアログを設定する
        builder.setTitle(R.string.dialog_delete_title) // タイトル
                .setMessage(R.string.dialog_delete_message) // メッセージ
                .setPositiveButton(R.string.dialog_button_positive, // 「はい」ボタン
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 「はい」ボタンが押された時の処理
                                // 結果を返す先のフラグメント
                                Fragment fragment = getTargetFragment();

                                if (fragment != null) {
                                    // 受け取っていた「削除対象のID」を、そのまま返す
                                    Bundle args = getArguments();
                                    Intent data = new Intent();
                                    data.putExtras(args);

                                    // 結果を返す先のフラグメントの、onActivityResult()を呼ぶ
                                    fragment.onActivityResult(getTargetRequestCode(),
                                            Activity.RESULT_OK, data);
                                }
                            }
                        })
                .setNegativeButton(R.string.dialog_button_cancel, // 「キャンセル」ボタン
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 結果を返す先のフラグメントに、キャンセルされたという
                                // 結果コードを返す
                                Fragment fragment = getTargetFragment();

                                if (fragment != null) {
                                    fragment.onActivityResult(getTargetRequestCode(),
                                            Activity.RESULT_CANCELED, null);
                                }
                            }
                        });
        // ビルダーからダイアログを生成して返す
        return builder.create();
    }
}
