package ssu.deslab.hexapod.history.common.model;

/**
 * Created by critic on 2017. 6. 28..
 */

public class HistoryData {
    private String mSavedDate;
    private String mLocation;
    private String mSavedImageURL;

    public HistoryData() {
        this.mSavedDate = "2017년 7월 4일 화요일";
        this.mLocation = "숭실대학교 형남공학관";
        this.mSavedImageURL = "https://www.naver.com";
    }

    public HistoryData(String mSavedDate, String mLocation, String mSavedImageURL) {
        this.mSavedDate = mSavedDate;
        this.mLocation = mLocation;
        this.mSavedImageURL = mSavedImageURL;
    }

    public String getmSavedDate() {
        return mSavedDate;
    }

    public void setmSavedDate(String mSavedDate) {
        this.mSavedDate = mSavedDate;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmSavedImage() {
        return mSavedImageURL;
    }

    public void setmSavedImage(String mSavedImageURL) {
        this.mSavedImageURL = mSavedImageURL;
    }
}
