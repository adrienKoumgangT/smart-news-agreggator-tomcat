package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.ApiConfiguration;

import java.util.Calendar;

public final class MineLog {

    public enum ColorPrint {
        //Color end string, color reset
        RESET("\033[0m"),

        // Regular Colors. Normal color, no bold, background color etc.
        BLACK("\033[0;30m"),    // BLACK
        RED("\033[0;31m"),      // RED
        GREEN("\033[0;32m"),    // GREEN
        YELLOW("\033[0;33m"),   // YELLOW
        BLUE("\033[0;34m"),     // BLUE
        MAGENTA("\033[0;35m"),  // MAGENTA
        CYAN("\033[0;36m"),     // CYAN
        WHITE("\033[0;37m"),    // WHITE

        // Bold
        BLACK_BOLD("\033[1;30m"),   // BLACK
        RED_BOLD("\033[1;31m"),     // RED
        GREEN_BOLD("\033[1;32m"),   // GREEN
        YELLOW_BOLD("\033[1;33m"),  // YELLOW
        BLUE_BOLD("\033[1;34m"),    // BLUE
        MAGENTA_BOLD("\033[1;35m"), // MAGENTA
        CYAN_BOLD("\033[1;36m"),    // CYAN
        WHITE_BOLD("\033[1;37m"),   // WHITE

        // Underline
        BLACK_UNDERLINED("\033[4;30m"),     // BLACK
        RED_UNDERLINED("\033[4;31m"),       // RED
        GREEN_UNDERLINED("\033[4;32m"),     // GREEN
        YELLOW_UNDERLINED("\033[4;33m"),    // YELLOW
        BLUE_UNDERLINED("\033[4;34m"),      // BLUE
        MAGENTA_UNDERLINED("\033[4;35m"),   // MAGENTA
        CYAN_UNDERLINED("\033[4;36m"),      // CYAN
        WHITE_UNDERLINED("\033[4;37m"),     // WHITE

        // Background
        BLACK_BACKGROUND("\033[40m"),   // BLACK
        RED_BACKGROUND("\033[41m"),     // RED
        GREEN_BACKGROUND("\033[42m"),   // GREEN
        YELLOW_BACKGROUND("\033[43m"),  // YELLOW
        BLUE_BACKGROUND("\033[44m"),    // BLUE
        MAGENTA_BACKGROUND("\033[45m"), // MAGENTA
        CYAN_BACKGROUND("\033[46m"),    // CYAN
        WHITE_BACKGROUND("\033[47m"),   // WHITE

        // High Intensity
        BLACK_BRIGHT("\033[0;90m"),     // BLACK
        RED_BRIGHT("\033[0;91m"),       // RED
        GREEN_BRIGHT("\033[0;92m"),     // GREEN
        YELLOW_BRIGHT("\033[0;93m"),    // YELLOW
        BLUE_BRIGHT("\033[0;94m"),      // BLUE
        MAGENTA_BRIGHT("\033[0;95m"),   // MAGENTA
        CYAN_BRIGHT("\033[0;96m"),      // CYAN
        WHITE_BRIGHT("\033[0;97m"),     // WHITE

        // Bold High Intensity
        BLACK_BOLD_BRIGHT("\033[1;90m"),    // BLACK
        RED_BOLD_BRIGHT("\033[1;91m"),      // RED
        GREEN_BOLD_BRIGHT("\033[1;92m"),    // GREEN
        YELLOW_BOLD_BRIGHT("\033[1;93m"),   // YELLOW
        BLUE_BOLD_BRIGHT("\033[1;94m"),     // BLUE
        MAGENTA_BOLD_BRIGHT("\033[1;95m"),  // MAGENTA
        CYAN_BOLD_BRIGHT("\033[1;96m"),     // CYAN
        WHITE_BOLD_BRIGHT("\033[1;97m"),    // WHITE

        // High Intensity backgrounds
        BLACK_BACKGROUND_BRIGHT("\033[0;100m"),     // BLACK
        RED_BACKGROUND_BRIGHT("\033[0;101m"),       // RED
        GREEN_BACKGROUND_BRIGHT("\033[0;102m"),     // GREEN
        YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),    // YELLOW
        BLUE_BACKGROUND_BRIGHT("\033[0;104m"),      // BLUE
        MAGENTA_BACKGROUND_BRIGHT("\033[0;105m"),   // MAGENTA
        CYAN_BACKGROUND_BRIGHT("\033[0;106m"),      // CYAN
        WHITE_BACKGROUND_BRIGHT("\033[0;107m");     // WHITE

        private final String code;

        ColorPrint(String code)
        {
            this.code = code;
        }

        public String getCode()
        {
            return this.code;
        }

        @Override
        public String toString()
        {
            return this.code;
        }
    }

    private static final Boolean printInfo = getIfPrintInfo();

    private static Boolean getIfPrintInfo() {
        try {
            ApiConfiguration apiConfiguration = new ApiConfiguration();
            return !apiConfiguration.isProd();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class TimePrinter {
        private final Long current;
        private String message;

        public TimePrinter() {
            this.message = "";
            this.current = Calendar.getInstance().getTimeInMillis();
        }

        public TimePrinter(String message) {
            this.message = message;
            info2(message);
            this.current = Calendar.getInstance().getTimeInMillis();
        }


        private void print(String color) {
            if(printInfo)
                System.out.println(
                        color
                                + Calendar.getInstance().getTime() + " "
                                + this.message
                                + " millis: " + (Calendar.getInstance().getTimeInMillis() - this.current) + " "
                                + ColorPrint.RESET.getCode()
                );
        }

        public void print() {
            String color = ColorPrint.WHITE_BOLD.getCode();
            print(color);
        }

        public void log() {
            String color = ColorPrint.GREEN_BOLD.getCode();
            print(color);
        }

        public void info() {
            String color = ColorPrint.YELLOW_BOLD.getCode();
            print(color);
        }

        public void warning() {
            String color = ColorPrint.MAGENTA_BOLD.getCode();
            print(color);
        }

        public void missing() {
            String color = ColorPrint.CYAN_BOLD.getCode();
            print(color);
        }

        public void error() {
            String color = ColorPrint.RED_BOLD.getCode();
            print(color);
        }

        public void print(String color, String message) {
            if(printInfo)
                System.out.println(
                        color
                                + Calendar.getInstance().getTime() + " "
                                + this.message + " --> " + message
                                + " millis: " + (Calendar.getInstance().getTimeInMillis() - this.current) + " "
                                + ColorPrint.RESET.getCode()
                );
        }

        public void log(String message) {
            String color = ColorPrint.GREEN_BOLD.getCode();
            print(color, message);
        }

        public void info(String message) {
            String color = ColorPrint.YELLOW_BOLD.getCode();
            print(color, message);
        }

        public void warning(String message) {
            String color = ColorPrint.MAGENTA_BOLD.getCode();
            print(color, message);
        }

        public void missing(String message) {
            String color = ColorPrint.CYAN_BOLD.getCode();
            print(color, message);
        }

        public void error(String message) {
            String color = ColorPrint.RED_BOLD.getCode();
            print(color, message);
        }

    }

    public static void formatPrint(String color, String message) {
        if(printInfo) System.out.println(color + Calendar.getInstance().getTime() + " " + message + ColorPrint.RESET.getCode());
    }

    public static void info(String message) {
        formatPrint(ColorPrint.WHITE_BOLD.getCode(), message);
    }

    public static void info2(String message) {
        formatPrint(ColorPrint.YELLOW_BOLD.getCode(), message);
    }

    public static void warning(String message) {
        formatPrint(ColorPrint.MAGENTA_BOLD.getCode(), message);
    }

    public static void missing(String message) {
        formatPrint(ColorPrint.CYAN_BOLD.getCode(), message);
    }

    public static void error(String message) {
        formatPrint(ColorPrint.RED_BOLD.getCode(), message);
    }

    public static void blue(String message) {
        formatPrint(ColorPrint.BLUE_BOLD.getCode(), message);
    }

}
