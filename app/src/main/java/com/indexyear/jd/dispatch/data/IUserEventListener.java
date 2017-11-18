package com.indexyear.jd.dispatch.data;

import com.indexyear.jd.dispatch.models.User;

public interface IUserEventListener {
    void onUserCreated(User newUser);
    void onUserRemoved(User removedUser);
    void onUserUpdated(User updatedUser);
}
