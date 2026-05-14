package com.club.dto;

public record ClubMemberUpdateRequest(
        String role,
        String status
) {
}
