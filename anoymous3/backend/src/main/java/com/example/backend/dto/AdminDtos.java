package com.example.backend.dto;

import com.example.backend.domain.UserStatus;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class AdminDtos {
    public record NoticeToggleReq(boolean notice) {}
    public record SetUserStatusReq(@NotNull UserStatus status, Instant suspendedUntil) {}
}
