package ru.demjanov_av.githubviewer.network;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.demjanov_av.githubviewer.models.RetrofitModel;

public interface RestAPI {
    @GET("users")
    Call<List<RetrofitModel>> loadUsers();

    @GET("users/{user}")
    Call<RetrofitModel> loadUser(@Path("user") String user);
}
