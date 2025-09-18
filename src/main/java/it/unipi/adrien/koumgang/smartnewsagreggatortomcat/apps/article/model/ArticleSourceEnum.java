package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model;

public enum ArticleSourceEnum {

    NYT("1", "NYT", "NewYork Time")

    ;


    private final String idArticleSource;
    private final String name;
    private final String description;


    ArticleSourceEnum(String idArticleSource, String name, String description) {
        this.idArticleSource = idArticleSource;
        this.name = name;
        this.description = description;
    }

    public String getIdArticleSource() {
        return idArticleSource;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static ArticleSourceEnum fromName(String name) {
        for (ArticleSourceEnum articleSourceEnum : ArticleSourceEnum.values()) {
            if (articleSourceEnum.name.equalsIgnoreCase(name)) {
                return articleSourceEnum;
            }
        }

        return null;
    }

    public static ArticleSourceEnum fromIdArticleSource(String idArticleSource) {
        for (ArticleSourceEnum articleSourceEnum : ArticleSourceEnum.values()) {
            if (articleSourceEnum.getIdArticleSource().equals(idArticleSource)) {
                return articleSourceEnum;
            }
        }

        return null;
    }
}
