package com.pandora.rxandroid.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pandora.rxandroid.R;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class RecyclerViewPagingFragment extends Fragment {
    public static final String TAG = RecyclerViewPagingFragment.class.getSimpleName();

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Unbinder mUnbinder;

    private PublishProcessor<Integer> mPublish;
    private List<ResolveInfo> mAppsList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_recycler_veiw, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.getItemPublishSubject().subscribe(s -> toast(s.getTitle()));

        mPublish = PublishProcessor.create();
    }

    @Override
    public void onStart() {
        super.onStart();



        mPublish.onBackpressureDrop()
                .concatMap(this::fromApps)
                .observeOn(AndroidSchedulers.mainThread())
                .map(items -> {
                    mRecyclerViewAdapter.updateItems(items);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    return items;
                })
                .subscribe();

        mPublish.onNext(0);
    }



    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mUnbinder = null;
    }


    private Flowable<List<RecyclerItem>> fromApps(int i) {
        final PackageManager pm = getActivity().getPackageManager();
        final List<ResolveInfo> apps = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps.addAll(pm.queryIntentActivities(intent, 0));
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        return Flowable
                .just(apps)
                .map(d -> {
                    List<RecyclerItem> items = new ArrayList<>();
                    for (ResolveInfo ri : d) {
                        items.add(new RecyclerItem(ri.activityInfo.loadIcon(pm), ri.activityInfo.loadLabel(pm).toString()));
                    }
                    return items;
                });
    }

    private void toast(String title) {
        Toast.makeText(getActivity().getApplicationContext(), title, Toast.LENGTH_SHORT).show();
    }
}
