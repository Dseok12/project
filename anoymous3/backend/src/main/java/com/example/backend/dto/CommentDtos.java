// backend/src/main/java/com/example/backend/dto/CommentDtos.java
package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class CommentDtos {

    public record CommentRes(
            Long id,
            Long postId,
            Long parentId,
            String authorActivityId,
            String contentRaw,
            String contentHtml,
            Instant createdAt
    ) {}

    public record CreateReq(
            @NotBlank @Size(max = 5000) String content,
            Long parentId // 대댓글이면 부모 ID
    ) {}

    public record UpdateReq(
            @NotBlank @Size(max = 5000) String content
    ) {}
}
