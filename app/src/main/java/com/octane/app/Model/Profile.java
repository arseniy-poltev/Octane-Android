package com.octane.app.Model;

public class Profile {
    private String profileName;
    private String profileCode;

    public Profile(){
        profileName = "";
        profileCode = "";
    }
    public Profile(String profileCode, String profileName) {
        this.profileName = profileName;
        this.profileCode = profileCode;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }

    @Override
    public boolean equals(Object obj) {
        return profileCode.equals(obj);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Integer.parseInt(profileCode);
        return result;
    }
}
