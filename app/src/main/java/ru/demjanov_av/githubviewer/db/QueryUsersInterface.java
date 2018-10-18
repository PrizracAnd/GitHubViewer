package ru.demjanov_av.githubviewer.db;

import android.support.annotation.Nullable;

import io.realm.RealmResults;
import ru.demjanov_av.githubviewer.models.RealmModelUser;

public interface QueryUsersInterface {
    void onCompleteQueryUsers(int codeOperation, @Nullable RealmResults<RealmModelUser> realmModelUsers);
    void onErrorQueryUsers(String message);
}
