package com.pandora.rxandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.pandora.rxandroid.R;
import com.pandora.rxandroid.logs.LogAdapter;
import com.pandora.rxandroid.volley.LocalVolley;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;


public class VolleyFragment extends Fragment {

    @BindView(R.id.vf_lv_log) ListView mLogView;

    private Unbinder mUnbinder;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_volley, container, false);
        mUnbinder = ButterKnife.bind(this, layout);
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mCompositeDisposable.clear();
    }

    @OnClick(R.id.vf_btn_volley)
    void start() { startVolley(); }

    private void startVolley() {

        DisposableObserver<JSONObject> observer = getObserver();

        mCompositeDisposable.add(getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer));
    }

    private Observable<JSONObject> getObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends JSONObject>>() {
            @Override
            public ObservableSource<? extends JSONObject> call() throws Exception {
                try {
                    return Observable.just(getRouteData());
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        });
    }

    private JSONObject getRouteData() throws ExecutionException, InterruptedException {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String url = "http://www.weather.com.cn/adat/sk/101010100.html";
//        String url = "http://www.google.com";
        JsonObjectRequest req = new JsonObjectRequest(url, null, future, future);
        LocalVolley.getRequestQueue().add(req);
        return future.get();
    }


    private DisposableObserver<JSONObject> getObserver() {
        return new DisposableObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                log(" >> " + jsonObject.toString());
            }

            @Override
            public void onError(Throwable t) {

                log(t.toString());
            }

            @Override
            public void onComplete() {
                log("complete");
            }
        };
    }

    private DisposableSubscriber<JSONObject> getSubscriber() {
        return new DisposableSubscriber<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                log(" >> " + jsonObject.toString());
            }

            @Override
            public void onError(Throwable t) {
                log("error");
            }

            @Override
            public void onComplete() {
                log("complete");
            }
        };
    }

    // Log
    private LogAdapter mLogAdapter;
    private List<String> mLogs;

    private void log(String log) {
        mLogs.add(log);
        mLogAdapter.clear();
        mLogAdapter.addAll(mLogs);
    }

    private void setupLogger() {
        mLogs = new ArrayList<>();
        mLogAdapter = new LogAdapter(getActivity(), new ArrayList<>());
        mLogView.setAdapter(mLogAdapter);
    }
}
