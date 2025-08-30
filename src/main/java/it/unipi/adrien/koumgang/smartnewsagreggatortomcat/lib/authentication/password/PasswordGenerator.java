package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password;

import java.security.SecureRandom;
import java.util.*;

public final class PasswordGenerator {

    private static final SecureRandom RNG = new SecureRandom();

    public static final class Config {
        // Length
        private final int length;

        // Character groups
        private final boolean includeLower;
        private final boolean includeUpper;
        private final boolean includeDigits;
        private final boolean includeSpecial;

        // Options
        private final boolean avoidAmbiguous;          // avoid 0 O o l I 1 5 S B 8 etc.
        private final boolean requireEachSelected;     // ensure at least one from each selected group
        private final boolean noAdjacentDuplicates;    // forbid same char twice in a row
        private final String customSpecials;           // override default specials (null = default)
        private final Set<Character> excludeChars;     // remove these characters from the pool

        private Config(Builder b) {
            this.length = b.length;
            this.includeLower = b.includeLower;
            this.includeUpper = b.includeUpper;
            this.includeDigits = b.includeDigits;
            this.includeSpecial = b.includeSpecial;
            this.avoidAmbiguous = b.avoidAmbiguous;
            this.requireEachSelected = b.requireEachSelected;
            this.noAdjacentDuplicates = b.noAdjacentDuplicates;
            this.customSpecials = b.customSpecials;
            this.excludeChars = Collections.unmodifiableSet(new HashSet<>(b.excludeChars));
            sanityCheck();
        }

        private void sanityCheck() {
            if (length < 1) throw new IllegalArgumentException("length must be >= 1");
            if (!(includeLower || includeUpper || includeDigits || includeSpecial)) {
                throw new IllegalArgumentException("At least one character group must be enabled.");
            }
        }

        public static class Builder {
            private int length = 16;
            private boolean includeLower = true;
            private boolean includeUpper = true;
            private boolean includeDigits = true;
            private boolean includeSpecial = false;

            private boolean avoidAmbiguous = false;
            private boolean requireEachSelected = true;
            private boolean noAdjacentDuplicates = false;
            private String customSpecials = null;
            private final Set<Character> excludeChars = new HashSet<>();

            public Builder length(int n) { this.length = n; return this; }
            public Builder lower(boolean v) { this.includeLower = v; return this; }
            public Builder upper(boolean v) { this.includeUpper = v; return this; }
            public Builder digits(boolean v) { this.includeDigits = v; return this; }
            public Builder special(boolean v) { this.includeSpecial = v; return this; }
            public Builder avoidAmbiguous(boolean v) { this.avoidAmbiguous = v; return this; }
            public Builder requireEachSelected(boolean v) { this.requireEachSelected = v; return this; }
            public Builder noAdjacentDuplicates(boolean v) { this.noAdjacentDuplicates = v; return this; }
            /** Set custom specials (e.g. "!@#$%") or null to use default. */
            public Builder customSpecials(String s) { this.customSpecials = s; return this; }
            /** Exclude specific characters from the pool. */
            public Builder excludeChars(String s) { if (s != null) for (char c : s.toCharArray()) excludeChars.add(c); return this; }

            public Config build() { return new Config(this); }
        }
    }

    // Defaults
    private static final char[] LOWER = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final char[] DIGIT = "0123456789".toCharArray();
    private static final char[] SPECIAL_DEFAULT = "!@#$%^&*()-_=+[]{};:,.?/\\|~`'\"<>".toCharArray();

    private static final Set<Character> AMBIGUOUS = setOf("0OolI1|5S$B8gq9Z2z");

    /**
     * Generate a password with default settings:
     * length=16, lower+upper+digits+special=true, avoidAmbiguous=true,
     * requireEachSelected=true, noAdjacentDuplicates=false.
     */
    public static String generate() {
        Config cfg = new Config.Builder()
                .length(16)
                .lower(true)
                .upper(true)
                .digits(true)
                .special(true)
                .avoidAmbiguous(true)
                .requireEachSelected(true)
                .noAdjacentDuplicates(false)
                .build();
        return generate(cfg);
    }

    public static String generate(Config cfg) {
        // Build per-group character pools
        List<char[]> enabledGroups = new ArrayList<>();
        if (cfg.includeLower) enabledGroups.add(filter(LOWER, cfg));
        if (cfg.includeUpper) enabledGroups.add(filter(UPPER, cfg));
        if (cfg.includeDigits) enabledGroups.add(filter(DIGIT, cfg));
        if (cfg.includeSpecial) {
            char[] src = cfg.customSpecials != null ? cfg.customSpecials.toCharArray() : SPECIAL_DEFAULT;
            enabledGroups.add(filter(src, cfg));
        }

        // Flatten master pool
        List<Character> pool = new ArrayList<>();
        for (char[] g : enabledGroups) for (char c : g) pool.add(c);

        if (pool.isEmpty()) {
            throw new IllegalStateException("Character pool is empty after applying options/exclusions.");
        }

        char[] out = new char[cfg.length];

        // If we must ensure at least one from each selected group
        int pos = 0;
        if (cfg.requireEachSelected) {
            for (char[] group : enabledGroups) {
                if (group.length == 0) continue;
                char next = pick(group, pos > 0 ? out[pos - 1] : null, cfg.noAdjacentDuplicates);
                out[pos++] = next;
                if (pos == cfg.length) break; // if length smaller than groups (rare), stop
            }
        }

        // Fill remaining with the full pool
        while (pos < cfg.length) {
            char next = pick(pool, pos > 0 ? out[pos - 1] : null, cfg.noAdjacentDuplicates);
            out[pos++] = next;
        }

        // Shuffle to avoid predictable group ordering (Fisher–Yates)
        for (int i = out.length - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            char tmp = out[i]; out[i] = out[j]; out[j] = tmp;
        }

        return new String(out);
    }

    public static List<String> generateMany(Config cfg, int count) {
        if (count < 1) throw new IllegalArgumentException("count must be >= 1");
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) list.add(generate(cfg));
        return list;
    }

    // ---------- helpers ----------

    private static char[] filter(char[] src, Config cfg) {
        StringBuilder sb = new StringBuilder(src.length);
        for (char c : src) {
            if (cfg.excludeChars.contains(c)) continue;
            if (cfg.avoidAmbiguous && AMBIGUOUS.contains(c)) continue;
            sb.append(c);
        }
        return sb.toString().toCharArray();
    }

    private static char pick(char[] group, Character prev, boolean noAdjacentDuplicates) {
        if (!noAdjacentDuplicates || prev == null) {
            return group[RNG.nextInt(group.length)];
        }
        // try a few times to avoid repeating prev
        for (int tries = 0; tries < 8; tries++) {
            char c = group[RNG.nextInt(group.length)];
            if (c != prev) return c;
        }
        // fallback (group might be size 1)
        return group[RNG.nextInt(group.length)];
    }

    private static char pick(List<Character> pool, Character prev, boolean noAdjacentDuplicates) {
        if (!noAdjacentDuplicates || prev == null) {
            return pool.get(RNG.nextInt(pool.size()));
        }
        for (int tries = 0; tries < 8; tries++) {
            char c = pool.get(RNG.nextInt(pool.size()));
            if (c != prev) return c;
        }
        return pool.get(RNG.nextInt(pool.size()));
    }

    private static Set<Character> setOf(String s) {
        Set<Character> set = new HashSet<>();
        for (char c : s.toCharArray()) set.add(c);
        return set;
    }

    // Quick demo
    public static void main(String[] args) {
        Config cfg = new Config.Builder()
                .length(20)
                .lower(true)
                .upper(true)
                .digits(true)
                .special(true)
                .customSpecials("!@#_-+.")
                .avoidAmbiguous(true)
                .requireEachSelected(true)
                .noAdjacentDuplicates(true)
                .excludeChars("O0Il1") // optional blacklist
                .build();

        System.out.println(generate(cfg));
        System.out.println(generate(cfg));
        System.out.println(generate(cfg));
    }
}
