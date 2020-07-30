package com.hcmus.easywork.ui.chat.mention;

import android.annotation.SuppressLint;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.hcmus.easywork.models.IComparableModel;
import com.linkedin.android.spyglass.mentions.Mentionable;

@SuppressLint("ParcelCreator")
public class SuggestedPeople implements Mentionable, IComparableModel<SuggestedPeople> {
    @SerializedName("userID")
    private int userId;
    private String name;

    public SuggestedPeople() {

    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isTheSame(@NonNull SuggestedPeople item) {
        return getUserId() == item.getUserId();
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(@NonNull MentionDisplayMode mode) {
        switch (mode) {
            case FULL: {
                return getName();
            }
            case PARTIAL: {
                String[] words = getName().split(" ");
                return (words.length > 1) ? words[0] : "";
            }
            case NONE:
            default: {
                return "";
            }
        }
    }

    @NonNull
    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.FULL_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return getName().hashCode();
    }

    @NonNull
    @Override
    public String getSuggestiblePrimaryText() {
        return getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(userId);
    }
}
