package ru.demjanov_av.githubviewer.injector.db;

import dagger.Component;
import ru.demjanov_av.githubviewer.presenters.MoreUsersPresenter;
import ru.demjanov_av.githubviewer.presenters.OneUsersPresenter;


@Component(modules = {RealmInit.class})
public interface InjectorRealm {
    void injectToMoreUsersPresenter(MoreUsersPresenter presenter);
    void injectToOneUsersPresenter(OneUsersPresenter presenter);
}
