package com.learn.mycamera;

/**
 * Created by dongjiangpeng on 2016/8/23 0023.
 */
public class RateItem {
    private int imageId;
    private String rateName;

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public RateItem(int imageId, String rateName) {
        this.imageId = imageId;
        this.rateName = rateName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
