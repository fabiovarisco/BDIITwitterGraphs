package br.unisinos.bdII.graphs.BdIITwitterGraphs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.social.twitter.api.TwitterProfile;

@NodeEntity
public class Profile {
	
	@GraphId private Long nodeId;
	private long twitterId;
	private String name, screenName, profileImageURL, location, description;
	private Date createdDate;
	private int followersCount, statusesCount, friendsCount, favouritesCount;
	
	@Relationship(type="TWEETED", direction=Relationship.OUTGOING)
	private List<Tweet> tweets;
	
	@Relationship(type="FOLLOWED_BY", direction=Relationship.OUTGOING)
	private List<Profile> followers;
	
	public Profile() {
		tweets = new ArrayList<Tweet>();
		followers = new ArrayList<>();
	}
	
	public Profile (TwitterProfile tp) {
		this.twitterId = tp.getId();
		this.name = tp.getName();
		this.screenName = tp.getScreenName();
		this.profileImageURL = tp.getBackgroundImageUrl();
		this.location = tp.getLocation();
		this.description = tp.getDescription();
		this.createdDate = tp.getCreatedDate();
		this.followersCount = tp.getFollowersCount();
		this.statusesCount = tp.getStatusesCount();
		this.friendsCount = tp.getFriendsCount();
		this.favouritesCount = tp.getFavoritesCount();
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public long getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(long twitterId) {
		this.twitterId = twitterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getProfileImageURL() {
		return profileImageURL;
	}

	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public int getStatusesCount() {
		return statusesCount;
	}

	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public List<Profile> getFollowers() {
		return followers;
	}

	public void setFollowers(List<Profile> followers) {
		this.followers = followers;
	}
}