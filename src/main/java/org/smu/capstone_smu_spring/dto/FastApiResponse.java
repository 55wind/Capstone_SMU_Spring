package org.smu.capstone_smu_spring.dto;

public class FastApiResponse {
    private String category;
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
}