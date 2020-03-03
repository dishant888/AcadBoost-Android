package com.example.acadboost.Model;

public class CourseListModel {

    private String title,courseBy,imageURL;
    private Double ratings;
    private String description,lang,validity;

    public CourseListModel(String title, String courseBy, String imageURL,String description,String lang,String validity, Double ratings) {
        this.title = title;
        this.description = description;
        this.lang = lang;
        this.validity = validity;
        this.courseBy = courseBy;
        this.imageURL = imageURL;
        this.ratings = ratings;
    }

    public CourseListModel(String title, String courseBy, String imageURL, Double ratings) {
        this.title = title;
        this.courseBy = courseBy;
        this.imageURL = imageURL;
        this.ratings = ratings;
    }

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
