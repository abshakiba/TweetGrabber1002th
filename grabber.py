import tweepy
import time
import requests

'''
Global Variables:
'''
consumer_key = 'k3WGJ7XibFzogyhZAw7EtXkSh'
consumer_secret = 'wMQ3BAOKJ4KNgdEh6G15IQeuq1H8rK2ZTNbsuzt0lUvicUE1HW'
access_token = '1523803256-AjIUybfYToyiEXadWZB5Swa0tNxQiC7fc5ud8eL'
access_token_secret = 'UBqBS2oIztwQnHmsYBbCGrOmnHdxndDoFFiX822SyGFaM'

#Tweets made by followers of this account will be grabbed
source_account = 'CNN'

#Amazon SQS Queue URL
#This is the queue to pass tweets to Sentiment Analysis engine
sqs_queue_url = 'https://sqs.us-east-1.amazonaws.com/623750023256/TweetsQueue'


'''
Filters out the followings:
  - non-English tweets
  - 
'''
def is_valid(tweet):
	if tweet.user.lang == 'en':
		return True
	return False


'''
Get all tweets of a user
'''
def get_and_submit_tweets(twitter_user):
	timeline = api.user_timeline(screen_name = twitter_user, count = 200, include_rts = True)
	print(len(timeline))
	for tweet in timeline:
		if is_valid(tweet):
			submit_tweet(tweet)
			time.sleep(3)

def submit_tweet(tweet):
	payload = {'Action': 'SendMessage', 'MessageBody': tweet.text}
	requests.get(sqs_queue_url, params=payload)
	print('The following tweet is sent to the output queue on Amazon SQS:')
	print(tweet.text)
	print('-----------')


'''
The Job:
'''

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)

api = tweepy.API(auth)

get_and_submit_tweets(source_account)


