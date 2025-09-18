package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionUtils {

    /**
     * Randomize the order of a List or Set of strings.
     *
     * @param input List or Set of strings
     * @return new List<String> with randomized order
     */
    public static List<String> shuffleStrings(Collection<String> input) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }

        List<String> list = new ArrayList<>(input); // copy into list
        Collections.shuffle(list);                  // shuffle in place
        return list;
    }

}
