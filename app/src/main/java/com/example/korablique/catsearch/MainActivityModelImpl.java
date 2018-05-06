package com.example.korablique.catsearch;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.korablique.catsearch.imagesearch.ImageInfo;
import com.example.korablique.catsearch.imagesearch.JSONResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.korablique.catsearch.imagesearch.BingSearchConstants.SEARCH_QUERY;

public class MainActivityModelImpl implements MainActivityModel {
    private static final String PREFERENCE_FILE_KEY = "MAIN_ACTIVITY_MODEL_IMPL";
    private static final String CACHED_CONTENT_URLS = "CACHED_CONTENT_URLS";
    private static final String CACHED_THUMBNAIL_URLS = "CACHED_THUMBNAIL_URLS";
    private Context context;

    public MainActivityModelImpl(Context context) {
        this.context = context;
    }

    @Override
    public void requestImages(ImagesCallback callback) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        if (!sharedPref.contains(CACHED_CONTENT_URLS) || !sharedPref.contains(CACHED_THUMBNAIL_URLS)) {
            CatSearchApplication.getApi().getData(SEARCH_QUERY).enqueue(new Callback<JSONResponse>() {
                @Override
                public void onResponse(@NonNull Call<JSONResponse> call, @NonNull Response<JSONResponse> response) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    Set<String> contentURLs = new HashSet<>();
                    Set<String> thumbnailUrls = new HashSet<>();
                    if (response.body() == null) {
                        callback.onFailure(new IllegalStateException("Response has no body"));
                        return;
                    }
                    List<ImageInfo> imageInfoList = response.body().getImageInfoList();
                    if (imageInfoList == null) {
                        callback.onFailure(new IllegalStateException("Response body has no images"));
                        return;
                    }
                    callback.onResponse(imageInfoList);

                    for (int index = 0; index < imageInfoList.size(); index++) {
                        String positionedContentURL = index + ":" + imageInfoList.get(index).getContentUrl();
                        contentURLs.add(positionedContentURL);
                        String positionedThumbnailURL = index + ":" + imageInfoList.get(index).getThumbnailUrl();
                        thumbnailUrls.add(positionedThumbnailURL);
                    }
                    editor.putStringSet(CACHED_CONTENT_URLS, contentURLs);
                    editor.putStringSet(CACHED_THUMBNAIL_URLS, thumbnailUrls);
                    editor.apply();
                }

                @Override
                public void onFailure(@NonNull Call<JSONResponse> call, @NonNull Throwable t) {
                    callback.onFailure(t);
                }
            });
        } else {
            Set<String> contentURLsSet = sharedPref.getStringSet(CACHED_CONTENT_URLS, null);
            List<String> contentURLsList = orderedSetToList(contentURLsSet);

            Set<String> thumbnailURLsSet = sharedPref.getStringSet(CACHED_THUMBNAIL_URLS, null);
            List<String> thumbnailURLsList = orderedSetToList(thumbnailURLsSet);

            List<ImageInfo> imageInfoList = new ArrayList<>();
            for (int index = 0; index < contentURLsList.size(); index++) {
                ImageInfo imageInfo = new ImageInfo(contentURLsList.get(index), thumbnailURLsList.get(index));
                imageInfoList.add(imageInfo);
            }
            callback.onResponse(imageInfoList);
        }
    }

    private List<String> orderedSetToList(Set<String> stringSet) {
        List<String> stringList = new ArrayList<>();
        // fill contentURLsList with nulls
        stringList.addAll(Arrays.asList(new String[stringSet.size()]));
        for (String s : stringSet) {
            int index = Integer.parseInt(s.substring(0, s.indexOf(":")));
            String URL = s.substring(s.indexOf(":") + 1);
            stringList.set(index, URL);
        }
        return stringList;
    }
}
