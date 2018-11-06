package ru.demjanov_av.githubviewer.network;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.demjanov_av.githubviewer.models.RetrofitModel;
import ru.demjanov_av.githubviewer.models.RetrofitModelRep;

public interface RestAPI {
    @GET("users")
    Call<List<RetrofitModel>> loadUsers(@Query("since") String previous_id);

    @GET("users/{user}")
    Call<RetrofitModel> loadUser(@Path("user") String user);

    @GET("users/{user}/repos")
    Call<List<RetrofitModelRep>> loadRepos(@Path("user") String user);
}
