package priv.liuxy.pushsmsdome.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import priv.liuxy.pushsmsdome.Config;

/**
 * Created by Liuxy on 2016/4/18.
 */
@DatabaseTable(tableName = "tb_sms")
public class SmsInfo implements Parcelable {
    /**
     * id
     */
    @DatabaseField(generatedId = true)
    private int id;
    /**
     * 短信内容
     */
    @DatabaseField(columnName = Config.FIELD_BODY)
    private String body;
    /**
     * 发送短信的电话号码
     */
    @DatabaseField(columnName = Config.FIELD_ADDRESS, canBeNull = false)
    private String phoneNumber;
    /**
     * 发送短信的日期和时间
     */
    @DatabaseField(columnName = Config.FIELD_TIME, canBeNull = false)
    private long date;
    /**
     * 短信是否已上传
     */
    @DatabaseField(columnName = Config.FIELD_UPLOAD, canBeNull = false)
    private boolean hasUpload;


    public SmsInfo() {
    }

    protected SmsInfo(Parcel in) {
        body = in.readString();
        phoneNumber = in.readString();
        date = in.readLong();
        hasUpload = in.readByte() != 0;
    }

    public static final Creator<SmsInfo> CREATOR = new Creator<SmsInfo>() {
        @Override
        public SmsInfo createFromParcel(Parcel in) {
            return new SmsInfo(in);
        }

        @Override
        public SmsInfo[] newArray(int size) {
            return new SmsInfo[size];
        }
    };

    public boolean isHasUpload() {
        return hasUpload;
    }

    public void setHasUpload(boolean hasUpload) {
        this.hasUpload = hasUpload;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeString(phoneNumber);
        dest.writeLong(date);
        dest.writeByte((byte) (hasUpload ? 1 : 0));
    }

    @Override
    public String toString() {
        return "SmsInfo{" +
                "body='" + body + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", date='" + date + '\'' +
                ", hasUpload=" + hasUpload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SmsInfo info = (SmsInfo) o;

        if (date != info.date) return false;
        if (hasUpload != info.hasUpload) return false;
        if (body != null ? !body.equals(info.body) : info.body != null) return false;
        return phoneNumber != null ? phoneNumber.equals(info.phoneNumber) : info.phoneNumber == null;

    }

    @Override
    public int hashCode() {
        int result = body != null ? body.hashCode() : 0;
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (hasUpload ? 1 : 0);
        return result;
    }
}
