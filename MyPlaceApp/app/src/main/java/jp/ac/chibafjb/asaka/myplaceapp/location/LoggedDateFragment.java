package jp.ac.chibafjb.asaka.myplaceapp.location;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.ac.chibafjb.asaka.myplaceapp.R;

public class LoggedDateFragment extends ListFragment {
    // フラグメントとアクティビティ間はインターフェースを通してアクセスすることで
    // フラグメントがアクティビティの実装に依存することを防ぐ
    public interface LoggedDateFragmentListener {
        void onDateSelected(String date);
    }

    // 日付を読み込むLoader
    private static final int DATE_LOADER = 1;
    // リストのアダプター
    private DateAdapter mAdapter;

    // アクティビティがインターフェースを実装しているかチェックする
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof LoggedDateFragmentListener)) {
            // アクティビティがLoggedDateFragmentListenerを実装していない場合
            throw new RuntimeException(context.getClass().getSimpleName()
                    + " does not implement LoggedDateFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 空のアダプターをセットする
        mAdapter = new DateAdapter(getActivity());
        setListAdapter(mAdapter);

        // Loaderを初期化する
        getLoaderManager().restartLoader(DATE_LOADER, getArguments(),
                mLoaderCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Loaderを破棄
        getLoaderManager().destroyLoader(DATE_LOADER);
    }

    private LoaderManager.LoaderCallbacks<List<String>>
        mLoaderCallback = new LoaderManager.LoaderCallbacks<List<String>>() {

        @Override
        public LoggedDateLoader onCreateLoader(int id, Bundle args) {
            if (id == DATE_LOADER) {
                LoggedDateLoader loader = new LoggedDateLoader(getActivity());
                loader.forceLoad();
                return loader;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            if (loader.getId() == DATE_LOADER) {
                mAdapter.addItems(data);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            if (loader.getId() == DATE_LOADER) {
                mAdapter.clearAll();
            }
        }
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String dateString = mAdapter.getItem(position);
        ((LoggedDateFragmentListener)getActivity()).onDateSelected(dateString);
    }

    private static class LoggedDateLoader extends AsyncTaskLoader<List<String>> {

        public LoggedDateLoader(Context context) {
            super(context);
        }

        @Override
        public List<String> loadInBackground() {
            return PlaceRepository.getAllDateString(getContext());
        }
    }

    private static class DateAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private List<String> mLoggedDates;

        DateAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mLoggedDates = new ArrayList<>();
        }

        public void addItems(List<String> items) {
            mLoggedDates.addAll(items);
            notifyDataSetChanged();
        }

        public void clearAll() {
            mLoggedDates.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mLoggedDates.size();
        }

        @Override
        public String getItem(int position) {
            return mLoggedDates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.list_item_date, parent, false);
                view.setTag(new ViewHolder(view));
            } else {
                view = convertView;
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            String dateString = mLoggedDates.get(position);

            holder.loggedDate.setText(dateString);

            return view;
        }

        private static class ViewHolder {
            private TextView loggedDate;

            ViewHolder(View view) {
                loggedDate = (TextView)view.findViewById(R.id.LoggedDate);
            }
        }
    }
}
