package com.example.korablique.catsearch;


import com.example.korablique.catsearch.imagesearch.ImageInfo;

import java.util.List;

public interface MainActivityModel {
    interface ImagesCallback {
        void onResponse(List<ImageInfo> imageInfoList);
        void onFailure(Throwable t);
    }
    void requestImages(ImagesCallback callback);
}
