package com.indexyear.jd.dispatch.data.user;

import com.indexyear.jd.dispatch.models.User;

// 11/28/17 JD: created to handle single event listener retrieval of an individual user

public interface IGetUserListener {
    void onGetSingleUser(User retrievedUser);
    void onFailedSingleUser();
}
