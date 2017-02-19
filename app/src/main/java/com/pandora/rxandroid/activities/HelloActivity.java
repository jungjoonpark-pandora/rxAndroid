package com.pandora.rxandroid.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.pandora.rxandroid.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;

public class HelloActivity extends AppCompatActivity {
    public static final String TAG = HelloActivity.class.getSimpleName();

    @BindView(R.id.tv_hello) TextView textView;



    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        mUnbinder = ButterKnife.bind(this);

        // case 1 : original
        Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    e.onNext("hello world!");
                    e.onComplete();
                }
            }).subscribe(new DisposableObserver<String>() {
                @Override
                public void onNext(String s) {
                    textView.setText(s);
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "error : " + e.getMessage());
                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "complete.");
                }
            });


        // case 2 : lambda
//        Observable.<String>create(s -> {
//            s.onNext("Hello, world!");
//            s.onComplete();
//        }).subscribe(o -> textView.setText(o));


        // case 3 : other Observable creator and reference method.
//        Observable.just("Hello, world!")
//                .subscribe(textView::setText);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
