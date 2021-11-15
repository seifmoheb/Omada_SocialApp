package com.app.omada;

public class UsersInfo {
    private static String username;
    private static String phone;
    private static String gender;
    private static String skills;
    private static String image;
    private static int posts, totalposts;

    public static void clear(){

    }
    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UsersInfo.email = email;
    }

    private static String email;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UsersInfo.username = username;
    }

    public static String getPhone() {
        return phone;
    }

    public static void setPhone(String phone) {
        UsersInfo.phone = phone;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        UsersInfo.gender = gender;
    }

    public static String getSkills() {
        return skills;
    }

    public static void setSkills(String skills) {
        UsersInfo.skills = skills;
    }

    public static String getImage() {
        return image;
    }

    public static void setImage(String image) {
        UsersInfo.image = image;
    }

    public static int getPosts() {
        return posts;
    }

    public static void setPosts(int posts) {
        UsersInfo.posts = posts;
    }

    public static int getTotalposts() {
        return totalposts;
    }

    public static void setTotalposts(int totalposts) {
        UsersInfo.totalposts = totalposts;
    }



}
