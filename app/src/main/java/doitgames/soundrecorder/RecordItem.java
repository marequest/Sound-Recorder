package doitgames.soundrecorder;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class RecordItem implements Serializable {

    private String mName; // file name
    private String mFilePath; // file path
    private int mId; // id in database
    private int mLength; // length of recording in seconds
    private long mTime; // date/time of recording

    public RecordItem(){

    }

    public RecordItem(String mName, String mFilePath, int mId, int mLength, long mTime) {
        this.mName = mName;
        this.mFilePath = mFilePath;
        this.mId = mId;
        this.mLength = mLength;
        this.mTime = mTime;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmFilePath() {
        return mFilePath;
    }

    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getmLength() {
        return mLength;
    }

    public void setmLength(int mLength) {
        this.mLength = mLength;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

}
