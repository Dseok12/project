// backend/src/main/java/com/example/backend/util/MarkdownRenderer.java
package com.example.backend.util;

/**
 * 마크다운 → HTML 렌더러 (경량 버전).
 * 현재는 별도 파서를 쓰지 않고 텍스트를 안전한 HTML로 변환합니다.
 * 추후 commonmark/flexmark 등을 붙이면 여기서 실제 마크다운 파싱을 수행하세요.
 */
public final class MarkdownRenderer {

    private MarkdownRenderer() {}

    /**
     * 입력 마크다운을 HTML로 변환한 뒤 화이트리스트로 정화된 안전한 HTML을 반환합니다.
     * 지금은 간단히 텍스트를 escape + 개행을 <br/>로 유지하는 형태입니다.
     */
    public static String render(String markdown) {
        if (markdown == null) return "";
        // 1) 텍스트 → 안전한 HTML (개행 유지)
        String html = HtmlSanitizer.textToSafeHtml(markdown);
        // 2) (호환 유지를 위해) 한 번 더 화이트리스트 정화
        return HtmlSanitizer.safeHtml(html);  // ← 기존 sanitize(...)를 safeHtml(...)로 교체
        // 또는 HtmlSanitizer.sanitizeHtml(html) 사용 가능
    }
}
