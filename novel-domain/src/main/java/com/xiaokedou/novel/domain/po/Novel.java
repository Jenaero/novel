package com.xiaokedou.novel.domain.po;

import java.util.Date;

public class Novel {

    private String id;
    //书名
    private String name;
    //作者
    private String author;
    //封面的链接
    private String img;
    //收藏数
    private Integer collection;
    //小说长度
    private Integer length;
    //总点击数
    private Integer totalClick;
    //本月点击
    private Integer monthClick;
    //本周点击
    private Integer weekClick;
    //总推荐数
    private Integer totalRecommend;
    //本月推荐
    private Integer monthRecommend;
    //本周推荐
    private Integer weekRecommend;
    //小说简介
    private String introduction;
    //小说评论
    private String comment;
    //小说详情链接
    private String novelUrl;
    //小说章节链接
    private String chapterUrl;
    //小说的类别：如武侠修真、都市言情
    private String type;
    //最后一章的章节名
    private String lastUpdateChapter;
    //最后一章的url
    private String lastUpdateChapterUrl;
    //小说最后的更新时间
    private Date lastUpdateTime;
    //小说的状态：1-连载 2-完结
    private Integer status;
    //书名的首字母
    private String firstLetter;
    //小说平台的id
    private Integer platformId;
    //入库时间
    private Date addTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img == null ? null : img.trim();
    }

    public Integer getCollection() {
        return collection;
    }

    public void setCollection(Integer collection) {
        this.collection = collection;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getTotalClick() {
        return totalClick;
    }

    public void setTotalClick(Integer totalClick) {
        this.totalClick = totalClick;
    }

    public Integer getMonthClick() {
        return monthClick;
    }

    public void setMonthClick(Integer monthClick) {
        this.monthClick = monthClick;
    }

    public Integer getWeekClick() {
        return weekClick;
    }

    public void setWeekClick(Integer weekClick) {
        this.weekClick = weekClick;
    }

    public Integer getTotalRecommend() {
        return totalRecommend;
    }

    public void setTotalRecommend(Integer totalRecommend) {
        this.totalRecommend = totalRecommend;
    }

    public Integer getMonthRecommend() {
        return monthRecommend;
    }

    public void setMonthRecommend(Integer monthRecommend) {
        this.monthRecommend = monthRecommend;
    }

    public Integer getWeekRecommend() {
        return weekRecommend;
    }

    public void setWeekRecommend(Integer weekRecommend) {
        this.weekRecommend = weekRecommend;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public String getNovelUrl() {
        return novelUrl;
    }

    public void setNovelUrl(String novelUrl) {
        this.novelUrl = novelUrl == null ? null : novelUrl.trim();
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl == null ? null : chapterUrl.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getLastUpdateChapter() {
        return lastUpdateChapter;
    }

    public void setLastUpdateChapter(String lastUpdateChapter) {
        this.lastUpdateChapter = lastUpdateChapter == null ? null : lastUpdateChapter.trim();
    }

    public String getLastUpdateChapterUrl() {
        return lastUpdateChapterUrl;
    }

    public void setLastUpdateChapterUrl(String lastUpdateChapterUrl) {
        this.lastUpdateChapterUrl = lastUpdateChapterUrl == null ? null : lastUpdateChapterUrl.trim();
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter == null ? null : firstLetter.trim();
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
    
    public void setNovelInfo(NovelInfo novelInfo){
		this.name                =novelInfo.getName()                 ;
		this.author              =novelInfo.getAuthor()               ;
		this.img                 =novelInfo.getImg()                  ;
		this. collection         =novelInfo. getCollection()          ;
		this. length             =novelInfo. getLength()              ;
		this. totalClick         =novelInfo. getTotalClick()          ;
		this. monthClick         =novelInfo. getMonthClick()          ;
		this. weekClick          =novelInfo. getWeekClick()           ;
		this. totalRecommend     =novelInfo. getTotalRecommend()      ;
		this. monthRecommend     =novelInfo. getMonthRecommend()      ;
		this. weekRecommend      =novelInfo. getWeekRecommend()       ;
		this.introduction        =novelInfo.getIntroduction()         ;
		this.comment             =novelInfo.getComment()              ;
		this.novelUrl            =novelInfo.getNovelUrl()             ;
		this.chapterUrl          =novelInfo.getChapterUrl()           ;
		this.type                =novelInfo.getType()                 ;
		this.lastUpdateChapter   =novelInfo.getLastUpdateChapter()    ;
		this.lastUpdateTime      =novelInfo.getLastUpdateTime()       ;
		this.status              =novelInfo.getStatus()               ;
    }

	@Override
	public String toString() {
		return "Novel [id=" + id + ", name=" + name + ", author=" + author + ", img=" + img + ", collection="
				+ collection + ", length=" + length + ", totalClick=" + totalClick + ", monthClick=" + monthClick
				+ ", weekClick=" + weekClick + ", totalRecommend=" + totalRecommend + ", monthRecommend="
				+ monthRecommend + ", weekRecommend=" + weekRecommend + ", introduction=" + introduction + ", comment="
				+ comment + ", novelUrl=" + novelUrl + ", chapterUrl=" + chapterUrl + ", type=" + type
				+ ", lastUpdateChapter=" + lastUpdateChapter + ", lastUpdateChapterUrl=" + lastUpdateChapterUrl
				+ ", lastUpdateTime=" + lastUpdateTime + ", status=" + status + ", firstLetter=" + firstLetter
				+ ", platformId=" + platformId + ", addTime=" + addTime + "]";
	}
    
}