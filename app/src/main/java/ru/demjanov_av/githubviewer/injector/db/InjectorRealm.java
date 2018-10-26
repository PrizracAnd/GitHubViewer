package ru.demjanov_av.githubviewer.injector.db;

import dagger.Component;
import ru.demjanov_av.githubviewer.presenters.MoreUsersPresenter;



@Component(modules = {RealmInit.class})
public interface InjectorRealm {
    void injectToMoreUsersPresenter(MoreUsersPresenter presenter);
}