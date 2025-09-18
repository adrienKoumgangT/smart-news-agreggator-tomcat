package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.RequiredValidator;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseArticleModel {

    @CsvColumn(order = 1001, name = "error")
    private String error;


    public BaseArticleModel() {}

    public boolean checkIfValid() {
        List<String> missing = RequiredValidator.validateRequiredFields(this);

        return missing.isEmpty();
    }

    public boolean checkIfInvalid() {
        return !checkIfValid();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }



    /** Convert *this instance* to a CSV line using annotated fields & ordering. */
    public String toCsvLine() {
        List<ColumnMeta> cols = resolveColumns(getClass());
        List<String> values = new ArrayList<>(cols.size());
        for (ColumnMeta c : cols) {
            values.add(escapeCsv(valueToString(c.field, this, c.dateFormat)));
        }
        return String.join(",", values);
    }

    /** Generate CSV header from annotated fields on the given class. */
    public static String csvHeader(Class<? extends BaseArticleModel> type) {
        List<ColumnMeta> cols = resolveColumns(type);
        List<String> headers = cols.stream()
                .map(c -> escapeCsv(c.header))
                .collect(Collectors.toList());
        return String.join(",", headers);
    }

    /** Turn a list into a full CSV string (header + rows). Returns empty string for empty list. */
    public static String toCsv(List<? extends BaseArticleModel> rows) {
        if (rows == null || rows.isEmpty()) return "";
        Class<? extends BaseArticleModel> type = rows.get(0).getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(csvHeader(type)).append("\n");
        for (BaseArticleModel row : rows) {
            sb.append(row.toCsvLine()).append("\n");
        }
        return sb.toString();
    }

    // ====== helpers ======

    private static final class ColumnMeta {
        final Field field;
        final String header;
        final int order;
        final String dateFormat;

        ColumnMeta(Field f, String header, int order, String dateFormat) {
            this.field = f;
            this.header = header;
            this.order = order;
            this.dateFormat = dateFormat;
        }
    }

    /** Collect annotated fields from class hierarchy, sort by order then by name. */
    private static List<ColumnMeta> resolveColumns(Class<?> type) {
        List<ColumnMeta> cols = new ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                CsvColumn ann = f.getAnnotation(CsvColumn.class);
                if (ann == null) continue;
                String header = ann.name().isBlank() ? f.getName() : ann.name();
                cols.add(new ColumnMeta(f, header, ann.order(), ann.dateFormat()));
            }
        }
        cols.sort(Comparator
                .comparingInt((ColumnMeta m) -> m.order)
                .thenComparing(m -> m.header));
        cols.forEach(m -> m.field.setAccessible(true));
        return cols;
    }

    /** Convert field value to string with optional date formatting; handle arrays/collections. */
    private static String valueToString(Field field, Object instance, String dateFormat) {
        Object val;
        try {
            val = field.get(instance);
        } catch (IllegalAccessException e) {
            return "";
        }
        if (val == null) return "";

        // Date formatting
        if (!dateFormat.isBlank() && val instanceof Date d) {
            try {
                return new SimpleDateFormat(dateFormat).format(d);
            } catch (Exception ignore) {
                // fall through to default
            }
        }

        Class<?> cls = val.getClass();

        // Arrays
        if (cls.isArray()) {
            if (val instanceof Object[] arr) {
                return joinArray(arr);
            } else if (val instanceof int[] a) {
                return joinPrimitive(a);
            } else if (val instanceof long[] a) {
                return joinPrimitive(a);
            } else if (val instanceof double[] a) {
                return joinPrimitive(a);
            } else if (val instanceof float[] a) {
                return joinPrimitive(a);
            } else if (val instanceof boolean[] a) {
                return joinPrimitive(a);
            } else if (val instanceof char[] a) {
                return joinPrimitive(a);
            } else if (val instanceof byte[] a) {
                // byte[] as hex
                StringBuilder hex = new StringBuilder(a.length * 2);
                for (byte b : a) hex.append(String.format("%02x", b));
                return hex.toString();
            }
        }

        // Collections
        if (val instanceof Collection<?> coll) {
            return coll.stream().map(String::valueOf).collect(Collectors.joining("|"));
        }

        // Everything else
        return String.valueOf(val);
    }

    private static String joinArray(Object[] arr) {
        return Arrays.stream(arr).map(String::valueOf).collect(Collectors.joining("|"));
    }

    private static String joinPrimitive(int[] a)     { return Arrays.stream(a).mapToObj(String::valueOf).collect(Collectors.joining("|")); }
    private static String joinPrimitive(long[] a)    { return Arrays.stream(a).mapToObj(String::valueOf).collect(Collectors.joining("|")); }
    private static String joinPrimitive(double[] a)  { return Arrays.stream(a).mapToObj(String::valueOf).collect(Collectors.joining("|")); }
    private static String joinPrimitive(float[] a)   { StringBuilder sb = new StringBuilder(); for (int i=0;i<a.length;i++){ if(i>0) sb.append("|"); sb.append(a[i]); } return sb.toString(); }
    private static String joinPrimitive(boolean[] a) { StringBuilder sb = new StringBuilder(); for (int i=0;i<a.length;i++){ if(i>0) sb.append("|"); sb.append(a[i]); } return sb.toString(); }
    private static String joinPrimitive(char[] a)    { return new String(a); }

    /** RFC4180-ish escaping: wrap in quotes if needed and double any quotes. */
    private static String escapeCsv(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (!needQuotes) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
    
}
