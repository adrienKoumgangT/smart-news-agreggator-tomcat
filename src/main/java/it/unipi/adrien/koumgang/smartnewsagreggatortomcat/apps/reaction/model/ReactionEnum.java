package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model;

public enum ReactionEnum {

    Like("like"),
    Dislike("dislike"),
    Love("love"),
    Laugh("laugh"),
    Wow("wow"),
    Sad("sad"),
    Angry("angry"),

    ;

    private final String name;

    ReactionEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static ReactionEnum fromName(String name) {
        for (ReactionEnum reactionEnum : ReactionEnum.values()) {
            if (reactionEnum.name.equalsIgnoreCase(name)) {
                return reactionEnum;
            }
        }

        return null;
    }
}
