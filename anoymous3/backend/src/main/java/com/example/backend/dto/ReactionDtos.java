package com.example.backend.dto;

public class ReactionDtos {
    public enum Type { LIKE, DISLIKE }
    public record ToggleReq(Type type) {}
    public record SummaryRes(long likes, long dislikes, String my) {} // my: "LIKE"|"DISLIKE"|null
}
