package com.example.backend.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Safelist;

/**
 * 서버 측 XSS 방지 유틸.
 * - safeHtml(html): 허용 태그만 남기고 모두 정화
 * - textToSafeHtml(text): 순수 텍스트를 안전한 HTML로(개행은 <br/>로 유지)
 * - markdownToSafeHtml(text): (간단버전) 마크다운 미파싱 -> 텍스트로 escape
 */
public final class HtmlSanitizer {

    private HtmlSanitizer() {}

    // 화이트리스트(기본 relaxed + 코드/프리태그, a 속성 강화)
    private static final Safelist SAFE_LIST = Safelist.relaxed()
            .addTags("pre", "code", "hr")
            .addAttributes("a", "target", "rel")
            .addProtocols("a", "href", "http", "https", "mailto");

    static {
        // 링크 안전 속성 강제
        SAFE_LIST.addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer");
        SAFE_LIST.addEnforcedAttribute("a", "target", "_blank");
        SAFE_LIST.preserveRelativeLinks(false);
    }

    private static final Document.OutputSettings OUT =
            new Document.OutputSettings()
                    .escapeMode(Entities.EscapeMode.xhtml)
                    .prettyPrint(false);

    /** HTML 입력을 화이트리스트 기준으로 정화 */
    public static String safeHtml(String html) {
        if (html == null) return "";
        return Jsoup.clean(html, "", SAFE_LIST, OUT);
    }

    /** alias */
    public static String sanitizeHtml(String html) { return safeHtml(html); }

    /** 순수 텍스트를 안전한 HTML로(개행 유지) */
    public static String textToSafeHtml(String text) {
        if (text == null) return "";
        // 텍스트를 안전하게 escape
        Document shell = Document.createShell("");
        shell.body().appendText(text);
        // 개행을 <br/>로 보존
        return shell.body().html().replace("\n", "<br/>");
    }

    /** (간단 대체) 마크다운 텍스트를 안전한 HTML로 – 별도 파서 없이 escape 처리 */
    public static String markdownToSafeHtml(String markdown) {
        return textToSafeHtml(markdown);
    }
}
