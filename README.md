# Tweet_Crawler
A crawler using the Twitter Search REST API to collect tweets on various topics in different languages

The specific task accomplished by this code:
Collect half a million tweets :

in 5 languages
Across 5 days
Across 5 different topics

These tweets will be stored in JSON format and then uploaded to a Solr instance for indexing.
Tokenization, stemming, removal of stop words will be done at the Solr instance, 
post which the tweet data will be available in an indexed format ready for further analysis