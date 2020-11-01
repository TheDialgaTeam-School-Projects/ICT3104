package edu.singaporetech.ict3104.project.fragment;

public class feature {
     String feature_name;
     int feature_rating;
     int user_age;
     char user_gender;
     char user_method;

    public feature(String feature_name, int feature_rating,int user_age , char user_gender ,char user_method){
        this.feature_name = feature_name;
        this.feature_rating = feature_rating;
        this.user_age = user_age;
        this.user_gender = user_gender;
        this.user_method = user_method;
    }

    public String getFeature_name(){
        return this.feature_name;
    }

    public void setFeature_name(String feature_name){
        this.feature_name = feature_name;
    }
    public int getFeature_rating(){
        return this.feature_rating;
    }

    public void setFeature_rating(int feature_rating){
        this.feature_rating = feature_rating;
    }
    public int getUser_age(){
        return this.user_age;
    }

    public void setUser_age(int user_age){
        this.user_age = user_age;
    }
    public char getUser_gender(){
        return this.user_gender;
    }
    public void setUser_gender(char user_gender){
        this.user_gender = user_gender;
    }
    public char getUser_method(){
        return this.user_method;
    }
    public void setUser_method(char user_method){
        this.user_method = user_method;
    }

}
