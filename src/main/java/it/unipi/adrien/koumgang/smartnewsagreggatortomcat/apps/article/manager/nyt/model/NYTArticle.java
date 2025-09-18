package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.model.BaseArticleModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.model.CsvColumn;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.utils.ToString;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

import java.util.Date;
import java.util.List;

public class NYTArticle extends BaseArticleModel {

    @CsvBindByName(column = "abstract")
    @CsvColumn(order = 1, name = "abstract")
    private String summary;

    @Required
    @CsvBindByName(column = "articleID")
    @CsvColumn(order = 2, name = "articleID")
    private String articleId;

    @CsvBindByName(column = "articleWordCount")
    @CsvColumn(order = 3, name = "articleWordCount")
    private Integer wordCount;

    @CsvBindByName
    @CsvColumn(order = 4, name = "byline")
    private String byline;

    @CsvBindByName(column = "documentType")
    @CsvColumn(order = 5, name = "documentType")
    private String documentType;

    @Required
    @CsvBindByName
    @CsvColumn(order = 6, name = "headline")
    private String headline;

    @CsvCustomBindByName(column = "keywords", converter = KeywordsConverter.class)
    @CsvColumn(order = 7, name = "keywords")
    private List<String> keywords;

    @CsvBindByName
    @CsvColumn(order = 8, name = "multimedia")
    private Integer multimedia;

    @CsvBindByName(column = "newDesk")
    @CsvColumn(order = 9, name = "newDesk")
    private String newsDesk;

    @CsvBindByName(column = "printPage")
    @CsvColumn(order = 10, name = "printPage")
    private Integer printPage;

    @Required
    @CsvBindByName(column = "pubDate")
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvColumn(order = 11, name = "pubDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date publicationDate;

    @CsvBindByName(column = "sectionName")
    @CsvColumn(order = 12, name = "sectionName")
    private String sectionName;

    @CsvBindByName
    @CsvColumn(order = 13, name = "snippet")
    private String snippet;

    @CsvBindByName
    @CsvColumn(order = 14, name = "source")
    private String source;

    @CsvBindByName(column = "typeOfMaterial")
    @CsvColumn(order = 15, name = "typeOfMaterial")
    private String materialType;

    @CsvBindByName(column = "webURL")
    @CsvColumn(order = 16, name = "webURL")
    private String webUrl;

    // Constructors
    public NYTArticle() {}

    // Getters and Setters
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }

    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }

    public String getByline() { return byline; }
    public void setByline(String byline) { this.byline = byline; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Integer getMultimedia() { return multimedia; }
    public void setMultimedia(Integer multimedia) { this.multimedia = multimedia; }

    public String getNewsDesk() { return newsDesk; }
    public void setNewsDesk(String newsDesk) { this.newsDesk = newsDesk; }

    public Integer getPrintPage() { return printPage; }
    public void setPrintPage(Integer printPage) { this.printPage = printPage; }

    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }

    public String getWebUrl() { return webUrl; }
    public void setWebUrl(String webUrl) { this.webUrl = webUrl; }

    @Override
    public String toString() {

        return ToString.builder("NYTArticle")
                .add("headline", headline)
                .add("byline", byline)
                .add("publicationDate", publicationDate)
                .add("sectionName", sectionName)
                .add("materialType", materialType)
                .add("wordCount", wordCount)
                .build();
    }
}
