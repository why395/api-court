package sch.xmut.wu.apicourt.http.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CourtRequest {
    @JsonProperty("arena_id")
    private Integer arenaId;
    @JsonProperty("rent_work")
    private Double rentWork;
    @JsonProperty("rent_weekend")
    private Double rentWeekend;
    @JsonProperty("court_name")
    private String courtName;
    @JsonProperty("cover_image")
    private String coverImage;

    public Integer getArenaId() {
        return arenaId;
    }

    public void setArenaId(Integer arenaId) {
        this.arenaId = arenaId;
    }

    public Double getRentWork() {
        return rentWork;
    }

    public void setRentWork(Double rentWork) {
        this.rentWork = rentWork;
    }

    public Double getRentWeekend() {
        return rentWeekend;
    }

    public void setRentWeekend(Double rentWeekend) {
        this.rentWeekend = rentWeekend;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
