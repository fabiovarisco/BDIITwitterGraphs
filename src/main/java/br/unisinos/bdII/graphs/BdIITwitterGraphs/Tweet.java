package br.unisinos.bdII.graphs.BdIITwitterGraphs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.social.twitter.api.HashTagEntity;
import org.springframework.social.twitter.api.MentionEntity;
import org.springframework.social.twitter.api.UrlEntity;

@NodeEntity
public class Tweet  {

	@GraphId private Long id;
	private String twitterId;
	private String text, source, inReplyToScreenName;
	private Date createdDate;
	private int retweetCount, favoritesCount;
	private boolean retweeted;
	
	private List<String> urls;
	private List<String> hashtags;
	private List<String> userMentions;
	
	@Relationship(type="TWEETED_BY", direction=Relationship.OUTGOING)
	private Profile user;
	
	@Relationship(type="RETWEETED", direction=Relationship.OUTGOING)
	private List<Tweet> retweets;
	
	public Tweet(){
		this.hashtags = new ArrayList<>();
		this.urls = new ArrayList<>();
		this.userMentions = new ArrayList<>();
		this.retweets = new ArrayList<>();
	}
	
	public Tweet(org.springframework.social.twitter.api.Tweet t) {
		this.twitterId = t.getIdStr();
		System.out.println(t.getIdStr());
		System.out.println(this.twitterId);
		this.text = t.getText();
		this.source = t.getSource();
		this.inReplyToScreenName = t.getInReplyToScreenName();
		this.createdDate = t.getCreatedAt();
		this.retweetCount = t.getRetweetCount();
		this.retweeted = t.isRetweeted();
		this.favoritesCount = t.getFavoriteCount();
	//	this.user = new Profile(t.getUser());
		this.hashtags = new ArrayList<>();
		for (HashTagEntity ht : t.getEntities().getHashTags()) 
			this.hashtags.add(ht.getText());
		this.urls = new ArrayList<>();
		for (UrlEntity e : t.getEntities().getUrls())
			this.urls.add(e.getUrl());
		this.userMentions = new ArrayList<>();
		for (MentionEntity m : t.getEntities().getMentions())
			this.userMentions.add(m.getScreenName());
		this.hashtags = new ArrayList<>();
		this.urls = new ArrayList<>();
		this.userMentions = new ArrayList<>();
		this.retweets = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(int retweetCount) {
		this.retweetCount = retweetCount;
	}

	public int getFavoritesCount() {
		return favoritesCount;
	}

	public void setFavoritesCount(int favoritesCount) {
		this.favoritesCount = favoritesCount;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public List<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}

	public List<String> getUserMentions() {
		return userMentions;
	}

	public void setUserMentions(List<String> userMentions) {
		this.userMentions = userMentions;
	}

	public Profile getUser() {
		return user;
	}

	public void setUser(Profile user) {
		this.user = user;
	}

	public List<Tweet> getRetweets() {
		return retweets;
	}

	public void setRetweets(List<Tweet> retweets) {
		this.retweets = retweets;
	}
}