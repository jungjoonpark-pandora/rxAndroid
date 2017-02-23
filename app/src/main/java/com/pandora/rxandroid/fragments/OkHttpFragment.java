package com.pandora.rxandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pandora.rxandroid.R;
import com.pandora.rxandroid.logs.LogAdapter;
import com.pandora.rxandroid.okHttp.Contributor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;


public class OkHttpFragment extends Fragment {

    @BindView(R.id.vf_lv_log) ListView mLogView;

    private Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_rest, container, false);

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
        if(mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

//    https://api.github.com/repos/jungjoonpark-pandora/rxAndroid/contributors
    @OnClick(R.id.vf_btn_get)
    void start() { startOkHttp(); }

    private void startOkHttp() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        GitHubService service = retrofit.create(GitHubService.class);
        Call<List<Contributor>> call = service.repoContributors("jungjoonpark-pandora", "rxAndroid");
        call.enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {

                if (response.isSuccessful()) {
                    log("isSuccessful : " + response.isSuccessful());

                    List<Contributor> contributors = response.body();
                    for (Contributor c : contributors) {
                        log(c.toString());
                    }
                } else {
                    log("not successful");
                }

            }

            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }





    interface GitHubService {
        @GET("repos/{owner}/{repo}/contributors")
        Call<List<Contributor>> repoContributors(
                @Path("owner") String owner,
                @Path("repo") String rep
        );
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
