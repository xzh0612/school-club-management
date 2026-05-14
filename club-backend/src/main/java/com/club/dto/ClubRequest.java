package com.club.dto;

import com.club.entity.Club;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClubRequest(
        @NotBlank(message = "社团名称不能为空")
        @Size(max = 100, message = "社团名称不能超过100字")
        String clubName,
        @Size(max = 5000, message = "社团简介不能超过5000字")
        String description,
        @Size(max = 50, message = "社团类型不能超过50字")
        String clubType,
        Integer advisorId,
        String status
) {
    public Club toClub() {
        Club club = new Club();
        club.setClubName(clubName == null ? null : clubName.trim());
        club.setDescription(description);
        club.setClubType(clubType);
        club.setAdvisorId(advisorId);
        club.setStatus(status);
        return club;
    }
}
