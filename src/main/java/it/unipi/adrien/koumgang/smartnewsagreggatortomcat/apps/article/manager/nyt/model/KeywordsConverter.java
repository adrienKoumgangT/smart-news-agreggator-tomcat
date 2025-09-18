package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.model;

import com.opencsv.bean.AbstractBeanField;

import java.util.ArrayList;
import java.util.List;

public class KeywordsConverter extends AbstractBeanField<List<String>, String> {
    public static String normalizeQuotes(String value) {
        if (value == null) return null;
        String newValue = value
                .replace("\\u0027", "'")
                .replace("\\u0022", "\"")
                .replace("\u2018", "'")
                .replace("\u2019", "'")
                .replace("\u201C", "\"")
                .replace("\u201D", "\"")
                .replace("\u00A0", " "); // non-breaking space to normal space

        if(newValue.startsWith("'")) newValue = newValue.substring(1);
        if(newValue.endsWith("'")) newValue = newValue.substring(0, newValue.length()-1);

        return newValue;
    }

    @Override
    protected List<String> convert(String value) {
        if (value == null || value.isBlank()) return List.of();

        // Normalize possible Unicode escapes / smart quotes
        String s = normalizeQuotes(value.trim());

        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);
        }

        // split on comma
        String[] parts = s.split("\\s*,\\s*");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if ((t.startsWith("'") && t.endsWith("'")) || (t.startsWith("\"") && t.endsWith("\""))) {
                t = t.substring(1, t.length() - 1);
            }
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }
}
