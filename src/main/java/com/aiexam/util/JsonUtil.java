package com.aiexam.util;

/**
 * Minimal JSON-building helper for AJAX endpoint responses. Avoids pulling
 * in a full JSON library for what is, in every case in this project, a
 * small flat object of primitives - keeps the servlet layer dependency-free
 * and easy to verify.
 */
public class JsonUtil {

    private final StringBuilder sb = new StringBuilder("{");
    private boolean first = true;

    public static JsonUtil object() {
        return new JsonUtil();
    }

    public JsonUtil put(String key, String value) {
        appendKey(key);
        if (value == null) {
            sb.append("null");
        } else {
            sb.append('"').append(escape(value)).append('"');
        }
        return this;
    }

    public JsonUtil put(String key, int value) {
        appendKey(key);
        sb.append(value);
        return this;
    }

    public JsonUtil put(String key, long value) {
        appendKey(key);
        sb.append(value);
        return this;
    }

    public JsonUtil put(String key, boolean value) {
        appendKey(key);
        sb.append(value);
        return this;
    }

    /** Embeds a pre-built JSON fragment (object/array/raw value) verbatim. */
    public JsonUtil putRaw(String key, String rawJson) {
        appendKey(key);
        sb.append(rawJson == null ? "null" : rawJson);
        return this;
    }

    private void appendKey(String key) {
        if (!first) {
            sb.append(',');
        }
        first = false;
        sb.append('"').append(escape(key)).append('"').append(':');
    }

    @Override
    public String toString() {
        return sb.toString() + "}";
    }

    public static String escape(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }
}
