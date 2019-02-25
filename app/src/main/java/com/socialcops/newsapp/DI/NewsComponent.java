package com.socialcops.newsapp.DI;

import com.socialcops.newsapp.Activity.MainActivity;

import dagger.Component;

@Component(modules = NewsModule.class)
public interface NewsComponent {
    void addActivity(MainActivity mainActivity);
}
