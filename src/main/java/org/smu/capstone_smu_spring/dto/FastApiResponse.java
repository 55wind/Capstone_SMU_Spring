package org.smu.capstone_smu_spring.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FastApiResponse {

    @JsonProperty("category")
    private String category;

    @JsonProperty("guide")
    private String guide;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    @Override
    public String toString() {
        return "FastApiResponse{" +
                "category='" + category + '\'' +
                ", guide='" + guide + '\'' +
                '}';
    }
}