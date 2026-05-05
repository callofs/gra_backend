package com.graProject.graBackend.common.utils;

/**
 * 富文本处理工具类。
 *
 * <p>
 * 用于将前端编辑器生成的 HTML 富文本进行基础处理（例如：提取纯文本、生成摘要）。
 * </p>
 */
public class RichTextUtil {

    /**
     * 将 HTML 富文本转换为纯文本。
     *
     * <p>
     * 会移除 script/style 标签内容与所有 HTML 标签，并对常见 HTML 实体进行简单反转义。
     * 该方法仅用于生成列表摘要/搜索字段等场景，不用于安全防护（XSS 清洗需使用更严格的白名单方案）。
     * </p>
     *
     * @param html HTML 富文本
     * @return 纯文本；入参为空时返回空字符串
     */
    public static String toPlainText(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return html
                .replaceAll("<script[\\s\\S]*?</script>", " ")
                .replaceAll("<style[\\s\\S]*?</style>", " ")
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * 将 HTML 富文本转换为摘要文本。
     *
     * @param html   HTML 富文本
     * @param maxLen 最大长度（小于等于 0 表示不截断）
     * @return 摘要文本
     */
    public static String toSummary(String html, int maxLen) {
        String text = toPlainText(html);
        if (maxLen <= 0 || text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, Math.min(maxLen, text.length())) + "...";
    }

    private RichTextUtil() {
    }
}
