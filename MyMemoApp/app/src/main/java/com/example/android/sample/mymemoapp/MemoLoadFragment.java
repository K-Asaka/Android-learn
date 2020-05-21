package com.example.android.sample.mymemoapp;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;

/**
 * 保存されたメモの一覧用Fragment
 */
public class MemoLoadFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // FragmentとActivity間はインターフェースを通してアクセスすることで、
    // FragmentがActivityの実装に依存することを防ぐ
    public interface MemoLoadFragmentListener {
        void onMemoSelected(@Nullable Uri uri);
    }

    private static final int CURSOR_LOADER = 1;

    private MemoAdapter mAdapter;

    // Activityがインターフェースを実装しているかチェックする
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof MemoLoadFragmentListener)) {
            // ActivityがMemoLoadFragmentListenerを実装していない場合
            throw new RuntimeException(context.getClass().getSimpleName()
                    + " does not implement MemoLoadFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Headerを追加する
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.memo_list_create, null);
        getListView().addHeaderView(header);

        // 空のAdapterをセットする
        mAdapter = new MemoAdapter(getActivity(), null, true);
        setListAdapter(mAdapter);

        // Loaderを初期化する
        getLoaderManager().restartLoader(CURSOR_LOADER, getArguments(), this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Loaderを廃棄
        getLoaderManager().destroyLoader(CURSOR_LOADER);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            // Headerの場合
            ((MemoLoadFragmentListener)getActivity()).onMemoSelected(null);

        } else {

            Uri selectedItem = ContentUris.withAppendedId(MemoProvider.CONTENT_URI, id);
            ((MemoLoadFragmentListener)getActivity()).onMemoSelected(selectedItem);
        }

    }

    @Override
    public CursorLoader onCreateLoader(int id, Bundle args) {
        if (id == CURSOR_LOADER) {
            return new CursorLoader(getActivity(), MemoProvider.CONTENT_URI,
                    null, null, null, MemoDBHelper.DATE_MODIFIED + " DESC");
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CURSOR_LOADER) {
            mAdapter.swapCursor(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CURSOR_LOADER) {
            mAdapter.swapCursor(null);
        }
    }
}
