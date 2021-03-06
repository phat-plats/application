package com.phat_plats.scanitfortheplanet.search.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.phat_plats.scanitfortheplanet.R;


/**
 * Represents the set of information contained in one result row
 */
public class ResultItem implements Parcelable {

    public QueryItem query;
    private Integer leftIcon;
    private Integer rightIcon;

    // Constructors ________________________________________________________________________________
    public ResultItem () {
        this.query = null;
        this.leftIcon = R.drawable.ic_action_search;
        this.rightIcon = R.drawable.arrow_left_up_icon;
    }

    public ResultItem (QueryItem query, Integer leftIcon, Integer rightIcon) {
        this.query = query;
        this.setLeftIcon(leftIcon);
        this.setRightIcon(rightIcon);
    }

    // Getters and Setters__________________________________________________________________________
    public Integer getLeftIcon() {
        return leftIcon;
    }

    public void setLeftIcon(Integer leftIcon) {
        if (leftIcon != null && leftIcon != 0 && leftIcon != -1) {
            this.leftIcon = leftIcon;
        } else {
            this.leftIcon = R.drawable.ic_action_search;
        }
    }

    public Integer getRightIcon() {
        return rightIcon;
    }

    public void setRightIcon(Integer rightIcon) {
        if (rightIcon != null && rightIcon != 0 && rightIcon != -1) {
            this.rightIcon = rightIcon;
        } else {
            this.rightIcon = R.drawable.arrow_left_up_icon;
        }
    }

    public String getHeader() {
        return query.name;
    }

    public void setHeader(String header) {
        this.query.name = header;
    }

    public String getSubHeader() {
        return query.upc;
    }

    public void setSubHeader(String subHeader) {
        if (subHeader != null && !"".equals(subHeader)) {
            this.query.upc = subHeader;
        } else {
            this.query.upc = "";
        }
    }

    // Parcelable contract implementation __________________________________________________________
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(query);
        dest.writeInt(leftIcon);
        dest.writeInt(rightIcon);
    }

    // Parcelable Creator Implementation ___________________________________________________________
    public static final Creator<ResultItem> CREATOR = new Creator<ResultItem>() {

        public ResultItem createFromParcel(Parcel in) {
            ResultItem resultItem = new ResultItem();

            resultItem.setHeader(in.readString());
            resultItem.setSubHeader(in.readString());
            resultItem.setLeftIcon(in.readInt());
            resultItem.setRightIcon(in.readInt());

            return resultItem;
        }

        public ResultItem[] newArray(int size) {
            return new ResultItem[size];
        }
    };

    @Override
    public String toString() {
        return this.getHeader() + " | " + this.getSubHeader();
    }
}
