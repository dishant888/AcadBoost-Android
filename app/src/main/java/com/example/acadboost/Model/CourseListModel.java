package com.example.acadboost.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class CourseListModel implements Parcelable {

    private String title,courseBy,imageURL;
    private Double ratings;
    private String description,lang,validity;

    public CourseListModel(String title, String courseBy, String imageURL, String description, String lang, String validity, Double ratings) {
        this.title = title;
        this.description = description;
        this.lang = lang;
        this.validity = validity;
        this.courseBy = courseBy;
        this.imageURL = imageURL;
        this.ratings = ratings;
    }

    protected CourseListModel(Parcel in) {
        title = in.readString();
        courseBy = in.readString();
        imageURL = in.readString();
        if (in.readByte() == 0) {
            ratings = null;
        } else {
            ratings = in.readDouble();
        }
        description = in.readString();
        lang = in.readString();
        validity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(courseBy);
        dest.writeString(imageURL);
        if (ratings == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(ratings);
        }
        dest.writeString(description);
        dest.writeString(lang);
        dest.writeString(validity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CourseListModel> CREATOR = new Creator<CourseListModel>() {
        @Override
        public CourseListModel createFromParcel(Parcel in) {
            return new CourseListModel(in);
        }

        @Override
        public CourseListModel[] newArray(int size) {
            return new CourseListModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseBy() {
        return courseBy;
    }

    public void setCourseBy(String courseBy) {
        this.courseBy = courseBy;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Double getRatings() {
        return ratings;
    }

    public void setRatings(Double ratings) {
        this.ratings = ratings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
