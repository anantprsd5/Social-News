package com.socialcops.newsapp.DI;

import com.socialcops.newsapp.Activity.WebActivity;

import dagger.Component;

@Component(modules = WebModule.class)
public interface WebComponent {
    void addActivity(WebActivity webActivity);
}
