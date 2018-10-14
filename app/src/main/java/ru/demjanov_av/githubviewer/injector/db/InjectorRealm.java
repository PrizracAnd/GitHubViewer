package ru.demjanov_av.githubviewer.injector.db;

import dagger.Component;
import ru.demjanov_av.githubviewer.presenters.MoreUsersPresenter;
import ru.demjanov_av.githubviewer.presenters.MyPresenter;


@Component(modules = {RealmInit.class})
public interface InjectorRealm {
    void injectToMoreUsersPresenter(MoreUsersPresenter presenter);
}
