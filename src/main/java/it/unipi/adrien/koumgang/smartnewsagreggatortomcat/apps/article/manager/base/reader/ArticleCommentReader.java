package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.reader;

import java.io.InputStream;
import java.util.List;

public interface ArticleCommentReader<T> {

    public List<T> readComments(InputStream fileStream);

}
