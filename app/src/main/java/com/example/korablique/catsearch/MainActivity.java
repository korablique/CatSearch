package com.example.korablique.catsearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    MainActivityPresenterImpl presenter = new MainActivityPresenterImpl(
            new MainActivityModelImpl(this),
            new MainActivityViewImpl(this),
            this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter.onActivityCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onActivitySaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onActivityDestroy();
    }
}
