package br.unisinos.bdII.graphs.BdIITwitterGraphs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/twitter/")
public class TwitterApi {
	
	private final int LIMIT_TWEETS_PER_USER = 30,
			LIMIT_FOLLOWERS=200;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private TweetRepository tweetRepository;
	
	@Autowired
	private ProfileRepository profileRepository;
	
	private Twitter twitter;

	@PostConstruct
	public void init() {
		twitter = new TwitterTemplate(
						environment.getProperty("spring.social.twitter.appId"),
						environment.getProperty("spring.social.twitter.appSecret"));
	}

	@RequestMapping("search")
    public List<Tweet> getTweets(@RequestParam String query) {
		SearchResults searchResults = twitter.searchOperations().search(query);
    	List<Tweet> tweets = new ArrayList<>();
    	for (org.springframework.social.twitter.api.Tweet t : searchResults.getTweets())
    		tweets.add(new Tweet(t));
    	return tweets;
    }
	
	@RequestMapping("search/save")
    public List<Tweet> getTweetsAndSave(@RequestParam String query) {
		List<Tweet> tweets = getTweets(query);
		tweetRepository.save(tweets);
    	return tweets;
    }
	
	@RequestMapping("tweets")
	public Iterable<Tweet> getSavedTweets(){
		return tweetRepository.findAll();
	}
	
	@RequestMapping("users/user")
	public Profile getUser(@RequestParam String username) {
		TwitterProfile tp = twitter.userOperations().getUserProfile(username);
		Profile p = new Profile(tp);
		return p;
	}
	
	@RequestMapping("users/user/friends")
	public List<Profile> getUserFriends(@RequestParam String username) {
		CursoredList<TwitterProfile> tpFriends = twitter.friendOperations().getFriends();
		List<Profile> friends = new ArrayList<>();
		for (TwitterProfile tp: tpFriends) 
			friends.add(new Profile(tp));
		return friends;
	}
	
	@RequestMapping("users/user/followers")
	public List<Profile> getUserFollowers(@RequestParam String username) {
		CursoredList<TwitterProfile> tpFollowers = twitter.friendOperations().getFollowers(username);
		List<Profile> followers = new ArrayList<>();
		for (TwitterProfile tp: tpFollowers)
			followers.add(new Profile(tp));
		return followers;
	}
	
	@RequestMapping("user/followers/save")
	public Profile getFollowersAndSave(@RequestParam String username) {
		Profile p = profileRepository.findByScreenName(username);
		if (p==null) {
			p = getUser(username);
		}	
		p.setFollowers(getUserFollowers(username));
		profileRepository.save(p);
		return p;
	}
	
	@RequestMapping("user/tweets")
	public Profile getRecentTweets(@RequestParam String username) {
		Profile p = profileRepository.findByScreenName(username);
		if (p==null) {
			p = getUser(username);
		}	
		if (p.getTweets()==null) 
			p.setTweets(new ArrayList<>());
		for (org.springframework.social.twitter.api.Tweet t : 
			twitter.timelineOperations().getUserTimeline(username)) {
			p.getTweets().add(new Tweet(t));
		}
	    profileRepository.save(p);
		return p;
	}
	
	@RequestMapping("populate/user")
	public Profile getPopulateUser(@RequestParam String username) {
		Profile p = profileRepository.findByScreenName(username);
		if (p==null) {
			p = getUser(username);
		}	
		populateUserRetweets(p);
		return p;
	}	
	
	@RequestMapping("populate/user/followers")
	public Profile getPopulateUserFollowers(@RequestParam String username) {
		Profile p = profileRepository.findByScreenName(username);
		if (p==null) {
			p = getUser(username);
		}	
		CursoredList<TwitterProfile> tpFollowers = twitter.friendOperations().getFollowers(username);
		if (p.getFollowers()==null)
			p.setFollowers(new ArrayList<>());
		processFollowersCursoredList(tpFollowers, p);
		while (tpFollowers.hasNext()) {
			try {
				tpFollowers = twitter.friendOperations().getFollowersInCursor(username, tpFollowers.getNextCursor());
				processFollowersCursoredList(tpFollowers, p);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				break;
			}
		} 
		profileRepository.save(p);
		return p;
	}	
	
	private void processFollowersCursoredList(CursoredList<TwitterProfile> followers, Profile p) {
		for (TwitterProfile tp : followers) {
			Profile fp = new Profile(tp);
			upsertProfile(fp);
			p.getFollowers().add(fp);
		}
	}
	
	@RequestMapping("populate/AllUserFollowers")
	public Iterable<Profile> populateAllUserFollowers(){
		Iterable<Profile> profiles = profileRepository.findAll();
		CursoredList<TwitterProfile> tpFollowers;
		for (Profile p : profiles) {
			try {
				tpFollowers = twitter.friendOperations().getFollowers(p.getScreenName());
				if (p.getFollowers() == null)
					p.setFollowers(new ArrayList<>());
				Profile followerProfile;
				for (TwitterProfile tp : tpFollowers) {
					followerProfile = new Profile(tp);
					upsertProfile(followerProfile);
					p.getFollowers().add(followerProfile);
				}
				profileRepository.save(p);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				break;
			}
		}
		return profiles;
	}
	
	@RequestMapping("populate/AllUsersTweets")
	public Iterable<Profile> populateTweetsFromAllUsers() {
		Iterable<Profile> profiles = profileRepository.findAll();
		for (Profile p : profiles) {
			try {
				populateUserRetweets(p);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				break;
			}
		}	
		return profiles;
	}
	
//	@RequestMapping("getRetweets")
//	public List<Tweet> getRetweets(@RequestParam long tweetId) {
//		System.out.println(tweetId);
//		List<Tweet> retweets = new ArrayList<>();
//		for (org.springframework.social.twitter.api.Tweet rt :
//			twitter.timelineOperations().getRetweets(tweetId)) {
//			retweets.add(new Tweet(rt));
//		}
//		return retweets;
//	}
	
	@RequestMapping("getRetweets")
	public List<org.springframework.social.twitter.api.Tweet> 
		getRetweets(@RequestParam long tweetId) {
		return twitter.timelineOperations().getRetweets(tweetId);
	}
	
	@RequestMapping("tweetsFromUser")
	public List<org.springframework.social.twitter.api.Tweet> 
		getUserTweets(@RequestParam String username) {
		return twitter.timelineOperations().getUserTimeline(username);
	}
	
	private Tweet upsertTweet(Tweet t) {
		Tweet bdT = tweetRepository.findByTwitterId(t.getTwitterId());
		if (bdT!=null) {
			t.setId(bdT.getId());
		} 
		tweetRepository.save(t);
		return t;		
	}
	
	private Profile upsertProfile(Profile p) {
		Profile bdP = profileRepository.findByTwitterId(p.getTwitterId());
		if (bdP!=null) 
			p.setNodeId(bdP.getNodeId());
		profileRepository.save(p);
		return p;	
	}

	private Profile populateUserRetweets(Profile p) {
		if (p.getTweets()==null) 
			p.setTweets(new ArrayList<>());
		for (org.springframework.social.twitter.api.Tweet t : twitter.timelineOperations().getUserTimeline(p.getScreenName(), LIMIT_TWEETS_PER_USER)) {
			Tweet nT = new Tweet(t);
			if (nT.getRetweetCount() > 0) {
				for (org.springframework.social.twitter.api.Tweet rt : twitter.timelineOperations()
						.getRetweets(Long.valueOf(nT.getTwitterId()))) {
					Tweet nRT = new Tweet(rt);
					Profile rtP = new Profile(rt.getUser());
					upsertProfile(rtP);
					nRT.setUser(rtP);
					upsertTweet(nRT);
					nT.getRetweets().add(nRT);
				}
			}
			upsertTweet(nT);
			p.getTweets().add(nT);
		}
	    profileRepository.save(p);
	    return p;
	}
}
