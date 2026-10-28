package com.twitter.util;

import java.util.Locale;

/**
 * Normalizes user search input and builds a literal LIKE pattern.
 * Wildcards in the query are escaped with '!' so searching for "%" or "_"
 * does not turn into a full-table match.
 */
public final class SearchQuery {
    private SearchQuery() {
    }

    public static String normalize(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }

    public static String likePattern(String normalized) {
        String escaped = normalized
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escaped + "%";
    }
}
