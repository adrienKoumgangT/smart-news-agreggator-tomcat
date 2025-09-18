package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.model;

import com.opencsv.bean.CsvBindByName;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.base.model.BaseArticleModel;

import java.util.Date;

public class NYTComment extends BaseArticleModel {

    @CsvBindByName(column = "approveDate")
    private Long approveDate;

    @CsvBindByName(column = "commentBody")
    private String commentBody;

    @CsvBindByName(column = "commentID")
    private Double commentId;

    @CsvBindByName(column = "commentSequence")
    private Double commentSequence;

    @CsvBindByName(column = "commentTitle")
    private String commentTitle;

    @CsvBindByName(column = "commentType")
    private String commentType;

    @CsvBindByName(column = "createDate")
    private Double createDate;

    @CsvBindByName
    private Integer depth;

    @CsvBindByName(column = "editorsSelection")
    private Boolean editorsSelection;

    @CsvBindByName(column = "parentID")
    private Double parentId;

    @CsvBindByName(column = "parentUserDisplayName")
    private String parentUserDisplayName;

    @CsvBindByName(column = "permID")
    private Double permId;

    @CsvBindByName(column = "picURL")
    private String picUrl;

    @CsvBindByName
    private Double recommendations;

    @CsvBindByName(column = "recommendedFlag")
    private String recommendedFlag;

    @CsvBindByName(column = "replyCount")
    private Double replyCount;

    @CsvBindByName(column = "reportAbuseFlag")
    private String reportAbuseFlag;

    @CsvBindByName
    private Integer sharing;

    @CsvBindByName
    private String status;

    @CsvBindByName
    private Double timespeople;

    @CsvBindByName
    private Double trusted;

    @CsvBindByName(column = "updateDate")
    private Double updateDate;

    @CsvBindByName(column = "userDisplayName")
    private String userDisplayName;

    @CsvBindByName(column = "userID")
    private Double userId;

    @CsvBindByName(column = "userLocation")
    private String userLocation;

    @CsvBindByName(column = "userTitle")
    private String userTitle;

    @CsvBindByName(column = "userURL")
    private String userUrl;

    @CsvBindByName(column = "inReplyTo")
    private Integer inReplyTo;

    @CsvBindByName(column = "articleID")
    private String articleId;

    @CsvBindByName(column = "sectionName")
    private String sectionName;

    @CsvBindByName(column = "newDesk")
    private String newsDesk;

    @CsvBindByName(column = "articleWordCount")
    private Double articleWordCount;

    @CsvBindByName(column = "printPage")
    private Double printPage;

    @CsvBindByName(column = "typeOfMaterial")
    private String typeOfMaterial;

    // Constructors
    public NYTComment() {}

    // Utility methods for date conversion
    public Date getApproveDateAsDate() {
        return approveDate != null ? new Date(approveDate * 1000) : null;
    }

    public Date getCreateDateAsDate() {
        return createDate != null ? new Date(createDate.longValue() * 1000) : null;
    }

    public Date getUpdateDateAsDate() {
        return updateDate != null ? new Date(updateDate.longValue() * 1000) : null;
    }

    // Getters and Setters
    public Long getApproveDate() { return approveDate; }
    public void setApproveDate(Long approveDate) { this.approveDate = approveDate; }

    public String getCommentBody() { return commentBody; }
    public void setCommentBody(String commentBody) { this.commentBody = commentBody; }

    public Double getCommentId() { return commentId; }
    public void setCommentId(Double commentId) { this.commentId = commentId; }

    public Double getCommentSequence() { return commentSequence; }
    public void setCommentSequence(Double commentSequence) { this.commentSequence = commentSequence; }

    public String getCommentTitle() { return commentTitle; }
    public void setCommentTitle(String commentTitle) { this.commentTitle = commentTitle; }

    public String getCommentType() { return commentType; }
    public void setCommentType(String commentType) { this.commentType = commentType; }

    public Double getCreateDate() { return createDate; }
    public void setCreateDate(Double createDate) { this.createDate = createDate; }

    public Integer getDepth() { return depth; }
    public void setDepth(Integer depth) { this.depth = depth; }

    public Boolean getEditorsSelection() { return editorsSelection; }
    public void setEditorsSelection(Boolean editorsSelection) { this.editorsSelection = editorsSelection; }

    public Double getParentId() { return parentId; }
    public void setParentId(Double parentId) { this.parentId = parentId; }

    public String getParentUserDisplayName() { return parentUserDisplayName; }
    public void setParentUserDisplayName(String parentUserDisplayName) { this.parentUserDisplayName = parentUserDisplayName; }

    public Double getPermId() { return permId; }
    public void setPermId(Double permId) { this.permId = permId; }

    public String getPicUrl() { return picUrl; }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }

    public Double getRecommendations() { return recommendations; }
    public void setRecommendations(Double recommendations) { this.recommendations = recommendations; }

    public String getRecommendedFlag() { return recommendedFlag; }
    public void setRecommendedFlag(String recommendedFlag) { this.recommendedFlag = recommendedFlag; }

    public Double getReplyCount() { return replyCount; }
    public void setReplyCount(Double replyCount) { this.replyCount = replyCount; }

    public String getReportAbuseFlag() { return reportAbuseFlag; }
    public void setReportAbuseFlag(String reportAbuseFlag) { this.reportAbuseFlag = reportAbuseFlag; }

    public Integer getSharing() { return sharing; }
    public void setSharing(Integer sharing) { this.sharing = sharing; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTimespeople() { return timespeople; }
    public void setTimespeople(Double timespeople) { this.timespeople = timespeople; }

    public Double getTrusted() { return trusted; }
    public void setTrusted(Double trusted) { this.trusted = trusted; }

    public Double getUpdateDate() { return updateDate; }
    public void setUpdateDate(Double updateDate) { this.updateDate = updateDate; }

    public String getUserDisplayName() { return userDisplayName; }
    public void setUserDisplayName(String userDisplayName) { this.userDisplayName = userDisplayName; }

    public Double getUserId() { return userId; }
    public void setUserId(Double userId) { this.userId = userId; }

    public String getUserLocation() { return userLocation; }
    public void setUserLocation(String userLocation) { this.userLocation = userLocation; }

    public String getUserTitle() { return userTitle; }
    public void setUserTitle(String userTitle) { this.userTitle = userTitle; }

    public String getUserUrl() { return userUrl; }
    public void setUserUrl(String userUrl) { this.userUrl = userUrl; }

    public Integer getInReplyTo() { return inReplyTo; }
    public void setInReplyTo(Integer inReplyTo) { this.inReplyTo = inReplyTo; }

    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getNewsDesk() { return newsDesk; }
    public void setNewsDesk(String newsDesk) { this.newsDesk = newsDesk; }

    public Double getArticleWordCount() { return articleWordCount; }
    public void setArticleWordCount(Double articleWordCount) { this.articleWordCount = articleWordCount; }

    public Double getPrintPage() { return printPage; }
    public void setPrintPage(Double printPage) { this.printPage = printPage; }

    public String getTypeOfMaterial() { return typeOfMaterial; }
    public void setTypeOfMaterial(String typeOfMaterial) { this.typeOfMaterial = typeOfMaterial; }

    @Override
    public String toString() {
        return "NYTComment{" +
                "user='" + userDisplayName + '\'' +
                ", location='" + userLocation + '\'' +
                ", recommendations=" + recommendations +
                ", comment='" + (commentBody != null ?
                commentBody.substring(0, Math.min(50, commentBody.length())) + "..." : "null") +
                '}';
    }
}
