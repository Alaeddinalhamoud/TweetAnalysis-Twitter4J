
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

 

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class Main {

	static ConfigurationBuilder _ConfigurationBuilder;// Config builder to build the connection for twitter api
	static TwitterFactory _TwitterFactory;//Building the factory
	static twitter4j.Twitter twitter;// Twitter4j lib to get the data from twitter
	static long coursor = -1;//used to read multi page
	static PagableResponseList<User> _User;// number of follower of the number of friends of account
	static HashMap<String, Integer> ListofFollower;//List of follower to save all the follower to using them in comparing
	static HashMap<String, Integer> userstweets;
	static List<Long> datestatus;// to save number of retweets during date
	static String UserName;// the username, we got it from the soa servies link
	static int NumberOfReTweets;
	static int TotalNumberOfRetweet;
	static int TotalNumberOfFollwed;
	static String MostActiveFollowed;
	static int TotalNumberOfTweetsReceived;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Initional Twitter API
			Init();// Use Your Key
			TweetAnalysis();
	}
	
	public static void GetUserName() {
		
		 Scanner scanner = new Scanner(System.in);

	        System.out.print("Enter a Twitter UserName:");
	        UserName= scanner.nextLine();
	        System.out.println(UserName);
		
	}
	
	public static void print(String arg1,String arg2,String arg3) {
		System.out.println(arg1+ " "+arg2+" "+arg3);
	}
	
	//Main method will be called many time to execute all the following methods
   public static void TweetAnalysis() {
		// To Get the UserName from Restful Link and save it on name.
		GetUserName();
		// If UserName gives twitter name/@aladinhamod.....etc../.
		if (!UserName.isEmpty()) {
			// This Method Will give Number Of Followed And Most Active Followed And NumOfTweetsReceived
			GetNumOfFollowedMostActiveFollowedAndNumOfTweetsReceived(UserName);
			//Number Of Retweets During Jan 2018
			GetNumberOfReTweet(UserName);
			// NumberFollowed 
			print("NumberFollowed", UserName, String.valueOf(TotalNumberOfFollwed));
			// MostActiveFollowed 
			print("MostActiveFollowed", UserName, MostActiveFollowed);
			// NumberOfRetweets 
			print("NumberOfRetweets", UserName, String.valueOf(NumberOfReTweets));
			// NumberOfTweetsReceived 
			print("NumberOfTweetsReceived", UserName, String.valueOf(TotalNumberOfTweetsReceived));
		} else {
			System.out.println("Please Check Your Internet Connection");
		}
   }
	
	// Twitter API Will Return Number of NumOfFollowedMostActiveFollowedAndNumOfTweetsReceived
	public static void GetNumOfFollowedMostActiveFollowedAndNumOfTweetsReceived(String _UserName) {
		//HashMap to Save Number of Users
		ListofFollower = new HashMap<String, Integer>();
		TotalNumberOfTweetsReceived=0;

		do {
			try {
				//To get the number of followed
				_User = twitter.getFriendsList(_UserName, coursor);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Loop to Read the data from followed list 
			//to get Name of Best user 
			for (User floower : _User) {
				String username = floower.getScreenName();// Friend Name ,, follower name
				int flooercout = floower.getStatusesCount(); /// Follower status count
				//Save the date to Hashmap
				ListofFollower.put(username, flooercout);
				//Get the TotalNumberOfTweetsReceived   is total of user status
				TotalNumberOfTweetsReceived += flooercout;
			}
			//Contionus to finish data (maybe save in more that page)
		} while ((coursor = _User.getNextCursor()) != 0);
		//To get the active user from the followed list
		Map.Entry<String, Integer> maxEntry = null;
		TotalNumberOfFollwed = ListofFollower.size();
		System.out.println("Tolal Number of Followed : " + TotalNumberOfFollwed);// Question 1
		//Comparing the tweets for the users to get the active follwer.
		 for (Entry<String, Integer> entry : ListofFollower.entrySet()) {

			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}
		 //Getting the date from the comparing 
		MostActiveFollowed = maxEntry.getKey();
		int NumberofTweet = maxEntry.getValue();
		System.out.println("Most Active Followed : " + MostActiveFollowed + " By Tweets number : " + NumberofTweet); // Question 4
		System.out.println("Total Number Of Tweets Received : " + TotalNumberOfTweetsReceived);//Qestion 2 
	}
	//Get the number of retweet by jan 2018
	public static void GetNumberOfReTweet(String _UserName) {
		//the first page
		int pageno = 1;
		String user = _UserName;
		//Save the list of tweets by date
		datestatus = new LinkedList<>();
		//Date format to get he month MM and the Year YYYY
		SimpleDateFormat sdf = new SimpleDateFormat("MM/YYYY");
		//TO save the status of user
		List<Status> statuses = new ArrayList<Status>();
		//Loop to read maybe more than 1 page
		while (true) {

			try {

				int size = statuses.size();
				Paging page = new Paging(pageno++, 200);
				//get all the user time line and save it ti statues
				statuses.addAll(twitter.getUserTimeline(user, page));
				//read the status one by one and get the retweets filterd by date
				for (Status s : statuses) {
					Date date = s.getCreatedAt();
					String dt = sdf.format(date);
					boolean is = s.isRetweet();
					Long id = s.getId();
					String str = "01/2018";
					if (!datestatus.contains(id)) {
						if ((dt.equals(str)) && (is)) {
							datestatus.add(id);
						}
					}
				}

				if (statuses.size() == size)
					break;
			} catch (TwitterException e) {

				e.printStackTrace();
			}
		}
		//Numebr of retweets by date
		NumberOfReTweets = datestatus.size();// Question .3
		System.out.println("Total Tweets Number Of ReTweets during  Jan 2018 : " + NumberOfReTweets);

	}
	 
	 
	//This method to preper my twitter api and build the connection with twitter using twitter4j
	public static void Init() {
		_ConfigurationBuilder = new ConfigurationBuilder();
		_ConfigurationBuilder.setDebugEnabled(true).setOAuthConsumerKey("Use Your Key")
				.setOAuthConsumerSecret("Use Your Key")
				.setOAuthAccessToken("Use Your Key")
				.setOAuthAccessTokenSecret("Use Your Key");
		_TwitterFactory = new TwitterFactory(_ConfigurationBuilder.build());
		twitter = _TwitterFactory.getInstance();
	}

}
