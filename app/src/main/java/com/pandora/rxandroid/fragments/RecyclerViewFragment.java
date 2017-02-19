package com.pandora.rxandroid.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pandora.rxandroid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecyclerViewFragment extends Fragment {
    public static final String TAG = RecyclerViewFragment.class.getSimpleName();

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Unbinder mUnbinder;

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
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mRecyclerViewAdapter == null) {
            return;
        }

        getItemObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    mRecyclerViewAdapter.updateItems(item);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                });
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


    private Observable<RecyclerItem> getItemObservable() {

        return Observable.create(e -> {
                final PackageManager pm = getActivity().getPackageManager();

                Intent i = new Intent(Intent.ACTION_MAIN, null);
                i.addCategory(Intent.CATEGORY_LAUNCHER);

                Observable.fromIterable(pm.queryIntentActivities(i, 0))
                        .sorted(new ResolveInfo.DisplayNameComparator(pm))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(Schedulers.computation())
                        .forEach(ri -> e.onNext(new RecyclerItem(ri.activityInfo.loadIcon(pm), ri.activityInfo.loadLabel(pm).toString())));

                // Java
//                final List<ResolveInfo> apps = new ArrayList<>();
//                Intent i = new Intent(Intent.ACTION_MAIN, null);
//                i.addCategory(Intent.CATEGORY_LAUNCHER);
//                apps.addAll(pm.queryIntentActivities(i, 0));
//                Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    apps.stream().forEach(ri -> e.onNext(new RecyclerItem(ri.activityInfo.loadIcon(pm), ri.activityInfo.loadLabel(pm).toString())));
//                    ;
//                } else {
//                    for (ResolveInfo ri : apps) {
//                        e.onNext(new RecyclerItem(ri.activityInfo.loadIcon(pm), ri.activityInfo.loadLabel(pm).toString()));
//                    }
//                }
            }
        );
    }


    private void toast(String title) {
        Toast.makeText(getActivity().getApplicationContext(), title, Toast.LENGTH_SHORT).show();
    }
}
