package com.sionnavisualizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class WorkspaceMemberResponse {
    @NotNull
    @Min(0)
    private Long userId;
    @NotBlank
    private String name;
    @NotBlank
    private String initials;
    @NotBlank
    private String role;
    @NotNull
    @Min(0)
    private Long simulationCount;
    @NotBlank
    private String lastActiveAt;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getSimulationCount() { return simulationCount; }
    public void setSimulationCount(Long simulationCount) { this.simulationCount = simulationCount; }

    public String getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(String lastActiveAt) { this.lastActiveAt = lastActiveAt; }
}
