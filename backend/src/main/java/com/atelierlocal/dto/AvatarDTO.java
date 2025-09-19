package com.atelierlocal.dto;

import com.atelierlocal.model.Avatar;


public class AvatarDTO {
    private String url;
    private String extension;

    public AvatarDTO(Avatar avatar) {
        this.url = avatar.getAvatarUrl();
        this.extension = avatar.getExtension();
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
}
