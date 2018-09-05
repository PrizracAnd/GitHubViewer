package ru.demjanov_av.githubviewer.injector.network;

import dagger.Component;
import ru.demjanov_av.githubviewer.network.Caller;

@Component(modules = {CreaterRestAPI.class, NetworkInfoProvider.class})
public interface InjectorToCaller {
    void injectToCaller(Caller caller);

}
