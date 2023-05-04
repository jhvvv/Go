package com.jh.go;

import android.graphics.Bitmap;

public class Go3B_GroupMemberListItem {

    /* 아이템의 정보를 담기 위한 클래스 */
    String name, hp;
    Bitmap profile;

    public Go3B_GroupMemberListItem(Bitmap profile, String name, String hp) {
        this.profile = profile;
        this.name = name;
        this.hp = hp;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getHp() {
        return hp;
    }
    public void setHp(String hp) {
        this.hp = hp;
    }

    public Bitmap getProfile() {
        return profile;
    }
    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }

}