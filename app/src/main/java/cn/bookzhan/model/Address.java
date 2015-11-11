package cn.bookzhan.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {

    private String ship_id;
    private String ship_name;
    private String ship_area;
    private String ship_addr;
    private String ship_mobile;
    private Boolean is_default;

    public Address() {
    }

    protected Address(Parcel in) {
        ship_id = in.readString();
        ship_name = in.readString();
        ship_area = in.readString();
        ship_addr = in.readString();
        ship_mobile = in.readString();
        is_default = in.readByte() != 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public Boolean getIsDefault() {
        return is_default;
    }

    public void setIsDefault(Boolean is_default) {
        this.is_default = is_default;
    }

    public String getShip_id() {
        return ship_id;
    }

    public void setShip_id(String ship_id) {
        this.ship_id = ship_id;
    }

    public String getShip_name() {
        return ship_name;
    }

    public void setShip_name(String ship_name) {
        this.ship_name = ship_name;
    }

    public String getShip_area() {
        return ship_area;
    }

    public void setShip_area(String ship_area) {
        this.ship_area = ship_area;
    }

    public String getShip_addr() {
        return ship_addr;
    }

    public void setShip_addr(String ship_addr) {
        this.ship_addr = ship_addr;
    }

    public String getShip_mobile() {
        return ship_mobile;
    }

    public String getShow_mobile() {
        if (ship_mobile.length() > 8) {
            return ship_mobile.substring(0, ship_mobile.length() - 8) + "****" + ship_mobile.substring(ship_mobile.length() - 4, ship_mobile.length());
        } else {
            return ship_mobile;
        }
    }

    public void setShip_mobile(String ship_mobile) {
        this.ship_mobile = ship_mobile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ship_id);
        dest.writeString(ship_name);
        dest.writeString(ship_area);
        dest.writeString(ship_addr);
        dest.writeString(ship_mobile);
        dest.writeByte((byte) (is_default ? 1 : 0));
    }
}
