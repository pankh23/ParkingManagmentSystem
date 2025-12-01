package com.apc.parking.dto.analytics;

public class VehicleTypeDistributionResponse {
    private Long twoWheelerCount;
    private Long fourWheelerCount;
    private Double twoWheelerPercentage;
    private Double fourWheelerPercentage;

    public VehicleTypeDistributionResponse() {}

    public VehicleTypeDistributionResponse(Long twoWheelerCount, Long fourWheelerCount) {
        this.twoWheelerCount = twoWheelerCount;
        this.fourWheelerCount = fourWheelerCount;
        long total = twoWheelerCount + fourWheelerCount;
        if (total > 0) {
            this.twoWheelerPercentage = (twoWheelerCount * 100.0) / total;
            this.fourWheelerPercentage = (fourWheelerCount * 100.0) / total;
        } else {
            this.twoWheelerPercentage = 0.0;
            this.fourWheelerPercentage = 0.0;
        }
    }

    public Long getTwoWheelerCount() {
        return twoWheelerCount;
    }

    public void setTwoWheelerCount(Long twoWheelerCount) {
        this.twoWheelerCount = twoWheelerCount;
    }

    public Long getFourWheelerCount() {
        return fourWheelerCount;
    }

    public void setFourWheelerCount(Long fourWheelerCount) {
        this.fourWheelerCount = fourWheelerCount;
    }

    public Double getTwoWheelerPercentage() {
        return twoWheelerPercentage;
    }

    public void setTwoWheelerPercentage(Double twoWheelerPercentage) {
        this.twoWheelerPercentage = twoWheelerPercentage;
    }

    public Double getFourWheelerPercentage() {
        return fourWheelerPercentage;
    }

    public void setFourWheelerPercentage(Double fourWheelerPercentage) {
        this.fourWheelerPercentage = fourWheelerPercentage;
    }
}

