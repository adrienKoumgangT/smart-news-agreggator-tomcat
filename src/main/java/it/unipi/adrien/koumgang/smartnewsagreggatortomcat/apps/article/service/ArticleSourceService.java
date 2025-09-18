package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.ArticleSourceView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.service.MetaDataService;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.view.MetaDataView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.service.BaseService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArticleSourceService extends BaseService {

    @Contract(" -> new")
    public static @NotNull ArticleSourceService getInstance() {
        return new ArticleSourceService(MetaDataService.getInstance());
    }



    private final MetaDataService metaDataService;

    public ArticleSourceService(MetaDataService metaDataService) {
        this.metaDataService = metaDataService;
    }


    private static final String METADATA_TYPE = "article-source";


    public List<ArticleSourceView> listArticleSources(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [SOURCE] [LIST] ");

        List<MetaDataView> metaDataViews = metaDataService.listMetaDataByType(userToken, METADATA_TYPE);

        List<ArticleSourceView> articleSourceViews = metaDataViews.stream().map(ArticleSourceView::new).toList();

        timePrinter.log();

        return articleSourceViews;
    }

    public ArticleSourceView getArticleSourceById(UserToken userToken, String id) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [SOURCE] [GET] id: " + id);


        MetaDataView metaDataView = metaDataService.getMetaDataById(userToken, id);

        if(metaDataView == null) {
            timePrinter.missing();
            return null;
        }

        ArticleSourceView articleSourceView = new ArticleSourceView(metaDataView);

        timePrinter.log();

        return articleSourceView;
    }

    public ArticleSourceView createArticleSource(UserToken userToken, ArticleSourceView articleSourceDetail) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [SOURCE] [CREATE] user status: " + gson.toJson(articleSourceDetail)
        );

        MetaDataView metaData = new MetaDataView(articleSourceDetail, METADATA_TYPE);

        MetaDataView metaDataView = metaDataService.saveMetaData(userToken, metaData);

        if(metaDataView == null) {
            timePrinter.error("Error during saving article source");
            return null;
        }

        ArticleSourceView articleSourceView = new ArticleSourceView(metaDataView);

        timePrinter.log();

        return articleSourceView;
    }

    public boolean updateArticleSource(UserToken userToken, String id, ArticleSourceView articleSourceDetail) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [ARTICLE] [SOURCE] [UPDATE] id: " + id
                        + " , article source: " + gson.toJson(articleSourceDetail)
        );


        MetaDataView metaData = new MetaDataView(articleSourceDetail, METADATA_TYPE);

        boolean updated = metaDataService.updateMetadata(userToken, id, metaData);

        timePrinter.log();

        return updated;
    }

    public boolean deleteArticleSource(UserToken userToken, String id) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [ARTICLE] [SOURCE] [DELETE] id: " + id);

        boolean deleted = metaDataService.deleteMetadata(userToken, id);

        timePrinter.log();

        return deleted;
    }

}