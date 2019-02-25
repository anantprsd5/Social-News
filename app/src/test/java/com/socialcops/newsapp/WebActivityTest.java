package com.socialcops.newsapp;

import com.socialcops.newsapp.View.WebViewInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class WebActivityTest {

    @Mock
    WebViewInterface webViewInterface;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadUrlTest(){
        webViewInterface.onLoadFinished();
        webViewInterface.onErrorReceived("error");
    }
}
