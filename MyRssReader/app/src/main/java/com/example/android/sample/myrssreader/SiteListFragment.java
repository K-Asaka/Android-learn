package com.example.android.sample.myrssreader;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sample.myrssreader.data.Site;
import com.example.android.sample.myrssreader.adapter.SiteAdapter;
import com.example.android.sample.myrssreader.loader.AddSiteLoader;
import com.example.android.sample.myrssreader.loader.DeleteSiteLoader;
import com.example.android.sample.myrssreader.loader.SiteListLoader;

import java.util.List;

/**
 * RSS配信サイトの一覧を表示するFragment
 */
public class SiteListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks, AdapterView.OnItemClickListener {

    // 各LoaderのID
    private static final int LOADER_LOAD_SITES = 1;
    private static final int LOADER_ADD_SITE = 2;
    private static final int LOADER_DELELTE_SITE = 3;

    // 登録／削除フラグメントへのリクエストコード
    private static final int REQUEST_ADD_SITE = 1;
    private static final int REQUEST_DELETE_CONFIRM = 2;

    // ダイアログフラグメント用のタグ
    private static final String TAG_DIALOG_FRAGMENT = "dialog_fragment";

    // サイトの登録・削除をアクティビティに伝えるリスナー
    public interface SiteListFragmentListener {
        void onSiteDeleted(long siteId);
        void onSiteAdded();
    }

    private SiteAdapter mAdapter;

    // Activityがインターフェースを実装しているかチェックする
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof SiteListFragmentListener)) {
            // ActivityがFeedsFragmentListenerを実装していない場合
            throw new RuntimeException(context.getClass().getSimpleName()
                    + " does not implement SiteListFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loaderを初期化する
        getLoaderManager().initLoader(LOADER_LOAD_SITES, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Loaderを廃棄
        getLoaderManager().destroyLoader(LOADER_LOAD_SITES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Viewを生成する
        View v = inflater.inflate(R.layout.fragment_sites, container, false);

        Context context = inflater.getContext();

        ListView listView = (ListView)v.findViewById(R.id.SiteList);

        // ヘッダーを生成する
        View header = inflater.inflate(R.layout.header_add_site, null, false);
        // ヘッダーのクリックイベントを追加
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ダイアログフラグメントを生成して表示
                AddSiteDialogFragment dialog =
                        AddSiteDialogFragment.newInstance(
                        SiteListFragment.this, REQUEST_ADD_SITE);
                dialog.show(getFragmentManager(), TAG_DIALOG_FRAGMENT);
            }
        });
        // ヘッダーを追加
        listView.addHeaderView(header, null, false);

        mAdapter = new SiteAdapter(context);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // ヘッダーもpositionにカウントされるので、1減らす
        Site site = mAdapter.getItem(position - 1);

        // 削除を確認するダイアログを表示する
        DeleteSiteDialogFragment dialog = DeleteSiteDialogFragment.newInstance(
                SiteListFragment.this, REQUEST_DELETE_CONFIRM, site.getId());
        dialog.show(getFragmentManager(), TAG_DIALOG_FRAGMENT);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // キャンセルされた場合などは何も行わない
        if (resultCode != Activity.RESULT_OK) return;

        // RSS配信サイトを追加した場合
        if (requestCode == REQUEST_ADD_SITE) {
            // RSS配信サイトを追加するLoaderを呼ぶ
            String url = data.getStringExtra("url");
            Bundle args = new Bundle();
            args.putString("url", url);

            // すでに初期化済みの場合は再スタート、そうでない場合は初期化
            Loader loader = getLoaderManager().getLoader(LOADER_ADD_SITE);
            if (loader == null) {
                getLoaderManager().initLoader(LOADER_ADD_SITE, args, this);
            } else {
                getLoaderManager().restartLoader(LOADER_ADD_SITE, args, this);
            }

        } else if (requestCode == REQUEST_DELETE_CONFIRM) {
            // 配信サイトを登録削除するLoaderを呼ぶ
            long targetFeedId = data.getLongExtra("site_id", -1L);
            Bundle args = new Bundle();
            args.putLong("targetId", targetFeedId);

            Loader loader = getLoaderManager().getLoader(LOADER_DELELTE_SITE);
            if (loader == null) {
                getLoaderManager().initLoader(LOADER_DELELTE_SITE, args, this);
            } else {
                getLoaderManager().restartLoader(LOADER_DELELTE_SITE, args, this);
            }
        }

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == LOADER_LOAD_SITES) {
            // 登録済みのRSS配信サイトの一覧を取得する
            SiteListLoader loader = new SiteListLoader(getActivity());
            loader.forceLoad();
            return loader;
        } else if (id == LOADER_ADD_SITE) {
            // RSS配信サイトを登録する
            String url = args.getString("url");

            AddSiteLoader loader = new AddSiteLoader(getActivity(), url);
            loader.forceLoad();

            return loader;

        } else if (id == LOADER_DELELTE_SITE) {
            // 登録済みのRSS配信サイトを削除する
            long siteId = args.getLong("targetId");

            DeleteSiteLoader loader = new DeleteSiteLoader(getActivity(), siteId);
            loader.forceLoad();

            return loader;
        }

        return null;
    }

    // Loaderの処理が終わった時に呼ばれる
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();

        if (id == LOADER_LOAD_SITES
                && data != null) {
            // 登録済みのRSS配信サイトの一覧が得られた
            mAdapter.addAll((List<Site>)data);

        } else if (id == LOADER_ADD_SITE) {
            // RSS配信サイトを登録した場合、すぐに反映する
            if (data != null) {
                mAdapter.addItem((Site)data);

                SiteListFragmentListener listener = (SiteListFragmentListener)getActivity();
                listener.onSiteAdded();
            } else {
                Toast.makeText(getActivity(), "登録できませんでした", Toast.LENGTH_SHORT).show();
            }

        } else if (id == LOADER_DELELTE_SITE) {
            // RSS配信サイトを登録削除した場合、すぐに反映する
            int affected = (int)data;
            if (affected > 0) {
                DeleteSiteLoader deleteLoader = (DeleteSiteLoader)loader;
                long targetId = deleteLoader.getTargetId();

                mAdapter.removeItem(targetId);

                SiteListFragmentListener listener = (SiteListFragmentListener)getActivity();
                listener.onSiteDeleted(targetId);
            } else {
                Toast.makeText(getActivity(), "削除できませんでした", Toast.LENGTH_SHORT).show();;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
