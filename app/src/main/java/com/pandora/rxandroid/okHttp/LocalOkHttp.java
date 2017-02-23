package com.pandora.rxandroid.okHttp;


import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class LocalOkHttp {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/r")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    interface GitHubService {
        @GET("repos/{owner}/{repo}/contributors")
        Call<List<Contributor>> repoContributors(
            @Path("owner") String owner,
            @Path("repo") String repo
        );
    }
}

