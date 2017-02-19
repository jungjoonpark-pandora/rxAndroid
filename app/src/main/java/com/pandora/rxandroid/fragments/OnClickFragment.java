package com.pandora.rxandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.jakewharton.rxbinding.view.RxView;
import com.pandora.rxandroid.R;
import com.pandora.rxandroid.logs.LogAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;


public class OnClickFragment extends Fragment {
    public static final String TAG = OnClickFragment.class.getSimpleName();

    @BindView(R.id.lv_log)
    ListView mLogView;
    @BindView(R.id.btn_click_observer)
    Button mButton;
    @BindView(R.id.btn_click_observer_lambda)
    Button mButtonLambda;
    @BindView(R.id.btn_click_observer_binding)
    Button mButtonBinding;

    private Unbinder mUnbinder;
    private LogAdapter mLogAdapter;
    private List<String> mLogs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_on_click, container, false);
        mUnbinder = ButterKnife.bind(this, layout);
        setupLogger();
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getClickEventObservable().subscribe(getObserver());
        getClickEventObservableWithLambda().subscribe(aVoid -> log("Clicked lambda"));
        getClickEventObservableWithRxBinding().subscribe(aVoid -> log("Clicked rxBinding"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }


    private Observable<View> getClickEventObservable() {
        return Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(ObservableEmitter<View> e) throws Exception {
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        e.onNext(view);
                    }
                });
            }
        });
    }

    private Observable<View> getClickEventObservableWithLambda() {
        return Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(ObservableEmitter<View> e) throws Exception {
                mButtonLambda.setOnClickListener(e::onNext);
            }
        });
    }

    private Observable<Void> getClickEventObservableWithRxBinding() {
        return RxJavaInterop.toV2Observable(RxView.clicks(mButtonBinding));
    }


    private DisposableObserver<? super View> getObserver() {
        return new DisposableObserver<View>() {
            @Override
            public void onNext(View view) {
                log("Clicked normal");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
    //        getClickEventObservable().subscribe();
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
