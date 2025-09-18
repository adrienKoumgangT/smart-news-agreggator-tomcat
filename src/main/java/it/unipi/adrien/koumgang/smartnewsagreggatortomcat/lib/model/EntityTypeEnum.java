package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model;

public enum EntityTypeEnum {

    Article("article"),
    Comment("comment"),
    DeepComment("deep_comment"),
    ;

    private final String name;

    EntityTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EntityTypeEnum fromName(String name) {
        for (EntityTypeEnum entityTypeEnum : EntityTypeEnum.values()) {
            if (entityTypeEnum.name.equals(name)) {
                return entityTypeEnum;
            }
        }

        return null;
    }
}
