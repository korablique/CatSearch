package com.example.korablique.catsearch;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.korablique.catsearch.imagesearch.ImageInfo;

import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainActivityPresenterImpl implements MainActivityPresenter {
    private MainActivityModel model;
    private MainActivityView view;
    private Context context;
    // needed to avoid multiple loading starts
    private boolean waitingResponse;

    public MainActivityPresenterImpl(MainActivityModel model, MainActivityView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        view.initActivity();

        IntentFilter intentFilter = new IntentFilter(CONNECTIVITY_ACTION);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (isConnected && !view.hasImages() && !waitingResponse) {
                    loadImages();
                }
            }
        }, intentFilter);

        if (savedInstanceState == null) {
            loadImages();
        } else {
            view.hideProgressBar();
            view.restoreState(savedInstanceState);
            if (!view.hasImages()) {
                loadImages();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState) {
        view.recordState(outState);
    }

    @Override
    public void onActivityDestroy() {
        view.destroy();
    }

    private void loadImages() {
        waitingResponse = true;
        view.showProgressBar();
        view.hideConnectivityError();
        model.requestImages(new MainActivityModel.ImagesCallback() {
            @Override
            public void onResponse(List<ImageInfo> imageInfoList) {
                view.showImages(imageInfoList);
                view.hideProgressBar();

                waitingResponse = false;
            }

            @Override
            public void onFailure(Throwable t) {
                view.hideProgressBar();
                view.displayConnectivityError();
                waitingResponse = false;
            }
        });
    }
}
