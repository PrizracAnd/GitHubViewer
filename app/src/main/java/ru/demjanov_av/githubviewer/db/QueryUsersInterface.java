package ru.demjanov_av.githubviewer.db;

import android.support.annotation.Nullable;

import java.util.List;


import ru.demjanov_av.githubviewer.models.RealmModelUser;

public interface QueryUsersInterface {
    void onCompleteQueryUsers(int codeOperation, @Nullable List<RealmModelUser> realmModelUsers);
    void onErrorQueryUsers(String message);
}
