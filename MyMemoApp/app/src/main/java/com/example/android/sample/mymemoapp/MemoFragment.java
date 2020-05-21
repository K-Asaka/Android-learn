package com.example.android.sample.mymemoapp;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MemoFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private MemoEditText mMemoEditText;

    private Uri mMemoUri;

    private static final int LOADER_SAVE_MEMO = 1;
    private static final int LOADER_LOAD_MEMO = 2;

    private static final String BUNDLE_KEY_URI = "uri";
    private static final String BUNDLE_KEY_TEXT = "text";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // レイアウトXMLからViewを生成
        View view = inflater.inflate(R.layout.fragment_memo, container, false);

        mMemoEditText = (MemoEditText)view.findViewById(R.id.Memo);

        // 設定を反映する
        reflectSettings();

        return view;
    }

    // 設定を反映する
    public void reflectSettings() {
        Context context = getActivity();
        if (context != null) {
            setFontSize(SettingPrefUtil.getFontSize(context));
            setTypeface(SettingPrefUtil.getTypeface(context));
            setMemoColor(SettingPrefUtil.isScreenReverse(context));
        }
    }

    // 文字サイズの設定を反映する
    private void setFontSize(float fontSizePx) {
        mMemoEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizePx);
    }

    // 文字装飾の設定を反映する
    private void setTypeface(int typeface) {
        mMemoEditText.setTypeface(Typeface.DEFAULT, typeface);
    }

    // 色の反転の設定を反映する
    private void setMemoColor(boolean reverse) {
        int backgroundColor = reverse ? Color.BLACK : Color.WHITE;
        int textColor = reverse ? Color.WHITE : Color.BLACK;

        mMemoEditText.setBackgroundColor(backgroundColor);
        mMemoEditText.setTextColor(textColor);
    }

    // 保存する
    public void save() {
        // URIとメモ内容をBundleに詰める
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_URI, mMemoUri);
        bundle.putString(BUNDLE_KEY_TEXT, mMemoEditText.getText().toString());

        // 保存用のLoaderを要求する
        getLoaderManager().restartLoader(LOADER_SAVE_MEMO, bundle, this).forceLoad();
    }

    // 読み込む
    public void load(Uri uri) {
        // 「現在のURI」を変更する
        mMemoUri = uri;

        if (uri != null) {
            // URIをBundleに詰める
            Bundle bundle = new Bundle();
            bundle.putParcelable(BUNDLE_KEY_URI, uri);

            // 読み取り用のLoaderを要求する
            getLoaderManager().restartLoader(LOADER_LOAD_MEMO, bundle, this).forceLoad();

        } else {
            // URIがnullの場合には、メモをクリアするだけ
            mMemoEditText.setText(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Loaderを廃棄
        getLoaderManager().destroyLoader(LOADER_LOAD_MEMO);
        getLoaderManager().destroyLoader(LOADER_SAVE_MEMO);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Context context = getActivity();

        if (context == null) {
            return null;

        } else if (id == LOADER_SAVE_MEMO) {
            // 保存用のLoaderを生成して返す
            Uri uri = args.getParcelable(BUNDLE_KEY_URI);
            String text = args.getString(BUNDLE_KEY_TEXT);
            return new MemoSaveLoader(getActivity(), uri, text);

        } else if (id == LOADER_LOAD_MEMO) {
            // 読み込み用のLoaderを生成して返す
            Uri uri = args.getParcelable(BUNDLE_KEY_URI);
            return new MemoLoadLoader(getActivity(), uri);

        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();

        if (id == LOADER_SAVE_MEMO && data != null) {
            // 保存に成功した場合
            mMemoUri = (Uri)data;

            if (getActivity() != null) {
                Toast.makeText(getActivity(), "保存しました", Toast.LENGTH_SHORT).show();
            }

        } else if (id == LOADER_LOAD_MEMO) {
            // 読み込みに成功した場合

            mMemoEditText.setText((String)data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private static class MemoSaveLoader extends AsyncTaskLoader<Uri> {
        private Uri mUri;

        private String mMemo;

        public MemoSaveLoader(Context context, @Nullable Uri uri, String memo) {
            super(context);
            mUri = uri;
            mMemo = memo;
        }

        @Override
        public Uri loadInBackground() {
            if (mUri != null) {
                // 更新する
                MemoRepository.update(getContext(), mUri, mMemo);
                return null;
            } else {
                return MemoRepository.create(getContext(), mMemo);
            }
        }
    }

    private static class MemoLoadLoader extends AsyncTaskLoader<String> {
        private Uri mUri;

        public MemoLoadLoader(Context context, @Nullable Uri uri) {
            super(context);
            mUri = uri;
        }

        @Override
        public String loadInBackground() {
            return MemoRepository.findMemoByUri(getContext(), mUri);
        }
    }
}
