package ru.demjanov_av.githubviewer.injector.db;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.demjanov_av.githubviewer.injector.ContextProvider;

@Module(includes = ContextProvider.class)
public class RealmInit {

    RealmConfiguration configuration;

    public RealmInit(){
        this.configuration = new RealmConfiguration.Builder().build();
    }


    @Provides
    public RealmConfiguration getConfiguration() {
        return configuration;
    }


    @Provides
    public Realm getRealm(Context context) {
        Realm.init(context);
//        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(this.configuration);

        return Realm.getDefaultInstance();
    }

}
