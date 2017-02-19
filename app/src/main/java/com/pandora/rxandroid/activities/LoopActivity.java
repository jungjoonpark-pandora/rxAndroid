package com.pandora.rxandroid.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pandora.rxandroid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;


public class LoopActivity extends AppCompatActivity {
    public static final String TAG = LoopActivity.class.getSimpleName();

    @BindView(R.id.lv_log)
    ListView mLogView;

    @BindView(R.id.tv_title)
    TextView mTitle;


    private Unbinder mUnbinder;
    private LogAdapter mLogAdapter;
    private List<String> mLogs;
    static private List<String> samples;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop);
        mUnbinder = ButterKnife.bind(this);


        setupLogger();
        setSampleTitle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        mUnbinder = null;
    }

    static {
        List<String> m = new ArrayList<>();
        m.add("banana");
        m.add("orange");
        m.add("apple");
        m.add("apple mango");
        m.add("melon");
        m.add("watermelon");
        samples = Collections.unmodifiableList(m);
    }

    private void setSampleTitle() {
        mTitle.append(
                // rxJava 1.x
//                rx.Observable.from(samples)
//                        .reduce((r, s) -> (r + "\n") + s)
//                        .toBlocking().single()

                // rxJava 2.x
                Observable.fromIterable(samples)
                        .reduce((r, s) -> (r + "\n") + s).blockingGet()
        );
    }




    @OnClick(R.id.btn_loop)
    void loop() {
        log(">>>>> getFirst :: apple");
        for (String s : samples) {
            if (s.contains("apple")) {
                log(s);
                break;
            }
        }
    }

    @OnClick(R.id.btn_loop2)
    void loop2() {
        log(">>>>> getFirst :: apple");

        // rxJava 1.x
//        rx.Observable.from(samples)
//                .filter(s -> s.contains("android"))
//                .firstOrDefault("Not found")
//                .subscribe(this::log);

        // rxJava 2.x
        Observable.fromIterable(samples)
                .skipWhile(s -> !s.contains("android"))
//                .filter(s -> s.contains("apple"))
                .first("Not found")
                .subscribe(this::log);
    }


//    public static <T, R> List<R> map(Function<T, R> f, List<T> list) throws Exception {
//        List<R> result = new ArrayList<>();
//        for (T t : list) {
//            result.add(f.apply(t));
//        }
//        return result;
//    }
//
//    Function<String, String> toLowerCase = String::toLowerCase;
//


    @OnClick(R.id.btn_loop3)
    void loop3() {



    }


    private void log(String log) {
        mLogs.add(log);
        mLogAdapter.clear();
        mLogAdapter.addAll(mLogs);
    }

    private void setupLogger() {
        mLogs = new ArrayList<>();
        mLogAdapter = new LogAdapter(this, new ArrayList<>());
        mLogView.setAdapter(mLogAdapter);
    }

    private class LogAdapter extends ArrayAdapter<String> {
        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.textview_log, R.id.tv_log, logs);
        }
    }


}
