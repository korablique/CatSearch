package com.example.korablique.catsearch.imagesearch;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageInfo implements Parcelable {
    @SerializedName("contentUrl")
    @Expose
    private String contentUrl;

    @SerializedName("thumbnailUrl")
    @Expose
    private String thumbnailUrl;

    public ImageInfo(String contentUrl, String thumbnailUrl) {
        this.contentUrl = contentUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    protected ImageInfo(Parcel in) {
        contentUrl = in.readString();
        thumbnailUrl = in.readString();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public int describeContents() {
        return contentUrl.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contentUrl);
        dest.writeString(thumbnailUrl);
    }
}
