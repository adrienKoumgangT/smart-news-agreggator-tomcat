package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils;

import java.util.Collections;
import java.util.List;

public class PaginateUtils {

    public static  <T> List<T> paginateList(List<T> list, int page, int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        int start = page * size;
        if (start >= list.size()) {
            return Collections.emptyList();
        }

        int end = Math.min(start + size, list.size());
        return list.subList(start, end);
    }

}
