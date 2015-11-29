package com.noktiz.ui.rest.services.response;

import com.noktiz.domain.entity.Friendship;
import com.noktiz.domain.entity.User;
import com.noktiz.domain.model.image.ImageManagement;

/**
 * Created by hassan on 03/11/2015.
 */
public class UserInfo {
    String userId;
    String firstName;
    String lastName;
    String gender;
    Boolean isMyTrustedFriend;
    Boolean amHisTruesteFriend;
    Long mutualTruestedFriendCount;
    String imageUrl_small;
    String imageUrl_medium;
    String imageUrl_large;
    String email;


    public UserInfo() {
    }


    public UserInfo(User user, User reference) {
        userId=user.getId().toString();
        firstName=user.getFirstName();
        lastName= user.getLastName();
        gender = user.getGender().toString();
        imageUrl_small = ImageManagement.getUserImageUrl(user, ImageManagement.ImageSize.small);
        imageUrl_medium = ImageManagement.getUserImageUrl(user, ImageManagement.ImageSize.medium);
        imageUrl_large = ImageManagement.getUserImageUrl(user, ImageManagement.ImageSize.large);
        if(!user.equals(reference)) {
            mutualTruestedFriendCount = Friendship.getMutualTrustedFriendsCount(user, reference);
            isMyTrustedFriend = Friendship.exists(user, reference) != null;
            amHisTruesteFriend = Friendship.exists(reference, user) != null;
        }
        else{
            email = user.getEmail();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getMyTrustedFriend() {
        return isMyTrustedFriend;
    }

    public void setMyTrustedFriend(Boolean myTrustedFriend) {
        isMyTrustedFriend = myTrustedFriend;
    }

    public Boolean getAmHisTruesteFriend() {
        return amHisTruesteFriend;
    }

    public void setAmHisTruesteFriend(Boolean amHisTruesteFriend) {
        this.amHisTruesteFriend = amHisTruesteFriend;
    }

    public Long getMutualTruestedFriendCount() {
        return mutualTruestedFriendCount;
    }

    public void setMutualTruestedFriendCount(Long mutualTruestedFriendCount) {
        this.mutualTruestedFriendCount = mutualTruestedFriendCount;
    }

    public String getImageUrl_small() {
        return imageUrl_small;
    }

    public void setImageUrl_small(String imageUrl_small) {
        this.imageUrl_small = imageUrl_small;
    }

    public String getImageUrl_medium() {
        return imageUrl_medium;
    }

    public void setImageUrl_medium(String imageUrl_medium) {
        this.imageUrl_medium = imageUrl_medium;
    }

    public String getImageUrl_large() {
        return imageUrl_large;
    }

    public void setImageUrl_large(String imageUrl_large) {
        this.imageUrl_large = imageUrl_large;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
