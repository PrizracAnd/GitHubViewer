package ru.demjanov_av.githubviewer.injector.db;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.demjanov_av.githubviewer.injector.ContextProvider;

@Module(includes = ContextProvider.class)
public class RealmInit {

    //-----Class variables begin-------------------------
    private RealmConfiguration configuration;
    private Realm realm;
    private boolean isInit = false;
    //-----Class variables end---------------------------


    /////////////////////////////////////////////////////
    // Initialize Method initRealm      !!!
    ////////////////////////////////////////////////////
    private void initRealm(Context context){
        Realm.init(context);
        this.configuration = new RealmConfiguration.Builder()
                .schemaVersion(3)
                .migration(new MigratorForRealm())
                .build();
        Realm.setDefaultConfiguration(this.configuration);
        this.realm = Realm.getDefaultInstance();

        this.isInit = true;
    }


    /////////////////////////////////////////////////////
    // Provides Methods
    ////////////////////////////////////////////////////
    //-----Begin-----------------------------------------
    @Provides
    public Realm getRealm(Context context) {
        if(!this.isInit){
            initRealm(context);
        }
        return this.realm;
    }


    @Provides
    public RealmConfiguration getConfiguration(Context context) {
        if(!this.isInit){
            initRealm(context);
        }
        return this.configuration;
    }
    //-----End-------------------------------------------

}
