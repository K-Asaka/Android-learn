package com.example.android.sample.mymemoapp;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

public class MemoLoadFragment extends ListFragment {

    // ユーザーが一覧をタップしたイベントのコールバック
    public interface MemoLoadFragmentListener {
        void onMemoSelected(@Nullable Uri uri);
    }

    // アクティビティがインターフェースを実装しているかチェックする
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof MemoLoadFragmentListener)) {
            // アクティビティがMemoLoadFragmentListenerを実装していない場合
            throw new RuntimeException(context.getClass().getSimpleName() + " does not implements MemoLoadFragmentlistener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // ヘッダーを追加する
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.memo_list_create, null);
        getListView().addHeaderView(header);

        // データベースを検索する
        Cursor cursor = MemoRepository.query(getActivity());

        // アダプターをセットする
        MemoAdapter adapter = new MemoAdapter(getActivity(), cursor, true);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            // ヘッダーの場合
            ((MemoLoadFragmentListener)getActivity()).onMemoSelected(null);
        } else {
            // リストの項目の場合
            Uri selectedItem = ContentUris.withAppendedId(MemoProvider.CONTENT_URI, id);
            ((MemoLoadFragmentListener)getActivity()).onMemoSelected(selectedItem);
        }
    }
}
