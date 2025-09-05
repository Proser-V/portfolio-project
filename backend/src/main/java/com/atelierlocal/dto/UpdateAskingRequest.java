package com.atelierlocal.dto;

import java.util.List;

import com.atelierlocal.model.ArtisanCategory;

public class UpdateAskingRequest {
    private String content;
    private List<ArtisanCategory> artisanCategoryList;


    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<ArtisanCategory> getArtisanCategoryList() { return artisanCategoryList; }
    public void setArtisanCategoryList(List<ArtisanCategory> artisanCategoryList) { this.artisanCategoryList = artisanCategoryList; }
}
