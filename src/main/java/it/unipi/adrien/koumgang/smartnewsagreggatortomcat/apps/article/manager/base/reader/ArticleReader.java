package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.reader;

import java.io.InputStream;
import java.util.List;

public interface ArticleReader<T> {

    public List<T> readArticles(InputStream fileStream);

}
