package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class SaveOrganizationRequest {
    @NotBlank
    private String orgName;
    @NotBlank
    private String memberType;
    @NotBlank
    private String allianceTrack;
    @NotBlank
    private String memberId;

    public String getOrgName() { return orgName; }
    public void setOrgName(String orgName) { this.orgName = orgName; }

    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }

    public String getAllianceTrack() { return allianceTrack; }
    public void setAllianceTrack(String allianceTrack) { this.allianceTrack = allianceTrack; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
}
