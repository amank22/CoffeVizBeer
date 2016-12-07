# CoffeVizBeer
Coffee Viz Beer Android app source code.Licensed under Apache 2.0

###Coffee viz Beer is a platform where you can share combinations of two things. These combos can be anything or of anyone. Make sure you keep it weird.

##[Like us on Facebook](www.facebook.com/coffeevizbeer)

![Coffee Viz Beer](/15069044_945563148914026_8773361373992254835_o.jpg)

There are people and things that are so much different, so opposite in nature and people don’t
really see them together but when they do come, they make a combination that others have
never imagined.
“This is all about finding such stories that people might not have imagined”
This app will have stories of such combinations like food combinations you have not thought of,
maybe like Pizza and nutella.
You might share some of stories where you became friend with a person who is out of your
league.
User can find really interesting stories and combinations.

##How to add test device for Ads?
1. Go to string.xml in resources.
2. Find string with id "test_device_id"
3. Run the app in debug mode once to find your device id in logcat.
   Find "Ads: Use AdRequest.Builder.addTestDevice("YOUR_DEVICE_ID") to get test ads on this device." like this in logcat.
3. Replace the device id to get test ads.