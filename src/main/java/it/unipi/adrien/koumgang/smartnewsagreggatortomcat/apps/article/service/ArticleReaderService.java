package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.model.BaseArticleModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.model.NYTArticle;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.reader.NYTArticleReader;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.ArticleSourceEnum;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.exception.safe.InvalidParamException;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ArticleReaderService {



    public static List<Article> readArticle(UserToken userToken, String source, InputStream fileStream) throws Exception {

        if(source == null || source.isBlank() || fileStream == null) return new ArrayList<>();

        ArticleSourceEnum articleSourceEnum = ArticleSourceEnum.fromName(source);

        if(articleSourceEnum == null) throw new InvalidParamException("invalid articleSourceEnum (" + source + ")");

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [READER] source: " + source);

        List<Article> articles;
        String errorsCSV;
        switch (articleSourceEnum) {
            case NYT: {
                List<NYTArticle> nytArticles = NYTArticleReader.getInstance().readArticles(fileStream);
                errorsCSV = BaseArticleModel.toCsv(nytArticles.stream().filter(BaseArticleModel::checkIfInvalid).toList());
                articles = nytArticles.stream().filter(BaseArticleModel::checkIfValid).map(Article::new).toList();
            } break;
            default: {
                articles = new ArrayList<>();
                errorsCSV = "";
            }
        }

        if(!errorsCSV.isBlank()) saveErrorsCSV(errorsCSV);

        timePrinter.log();

        return articles;
    }

    private static void saveErrorsCSV(String errorsCSV) {
        // TODO: save the errors result in a file

        System.out.println(errorsCSV);
    }


}
