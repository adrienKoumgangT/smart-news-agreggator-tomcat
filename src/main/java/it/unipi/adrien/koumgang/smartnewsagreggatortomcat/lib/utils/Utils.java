package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils;

public class Utils {

    public static int countString(String str, String substr) {
        // Initialise le compteur à zero
        int count = 0;

        // Initialise l'index de départ à zero
        int startIndex = 0;

        // Tant qu'on trouve une occurrence de la chaîne recherchée...
        while (str.indexOf(substr, startIndex) != -1) {
            // Incrémente le compteur
            count++;

            // Définit l'index de départ pour la recherche suivante
            startIndex = str.indexOf(substr, startIndex) + substr.length();
        }

        return count;
    }

}
