# PaytmLabs SDE Challenge

## Coding Question

Write an interface for a data structure that can provide the moving average of the last N elements added, add elements to the structure and get access to the elements. Provide an efficient implementation of the interface for the data structure.

### Minimum Requirements

1. Provide a separate interface (IE `interface`/`trait`) with documentation for the data structure
2. Provide an implementation for the interface
3. Provide any additional explanation about the interface and implementation in a README file.

## Design Question

Design A Google Analytic like Backend System.
We need to provide Google Analytic like services to our customers. Please provide a high level solution design for the backend system. Feel free to choose any open source tools as you want.

### Requirements

1. Handle large write volume: Billions of write events per day.
2. Handle large read/query volume: Millions of merchants wish to gain insight into their business. Read/Query patterns are time-series related metrics.
3. Provide metrics to customers with at most one hour delay.
4. Run with minimum downtime.
5. Have the ability to reprocess historical data in case of bugs in the processing logic.

# Solution

## Coding Question

The solution was implemented in Scala, with an sbt project. You may run it inside the sbt shell with

`runMain vlizotte.sdechallenge.SdeChallenge`

To run the unit tests, inside the sbt shell:

`test`

The code exposes the `MovingAverageable` trait which is implemented by the `MovingAverage` class. At creation, it takes the window size as well as the initial list of values (usually empty). 

`MovingAverage` is an immutable data type. Adding an element to it returns a new instance. This is fine for our scenario since we always operate at the head of the list (which is a fast operation with Scala lists): new elements are prepended, and the moving average only cares about the first N elements. Being an immutable data type, it also does not need any private fields or getters / setters, since users are incapable of messing with the internals of an immutable type. 

When it comes to performance, the usual trade-off would be to choose one of those 2 options:
 - Compute and cache the average whenever a new element is added
 - Simple compute the average whenever it is requested
 
The first solution is ideal when the average is read more often than new values are added. However, if we were to add 10 values before reading the average, we would have wasted time computing an average that gets thrown away before being read even once.

However, by using memoization, it is possible to get the best of both worlds: only compute when needed, and only compute once. Scala makes that easy with `lazy val`, which is how `average` is implemented. It makes use of memoization to only have to compute the average once for that instance of the class, which is as long as the elements remain unchanged.

## Design Question

The purpose of the analytics system is to gather information about usage patterns, for example how users interact with a website or app.

This design is about the backend part, but we still need to describe a bit of the front-end part to at least understand what is being communicated.

### Front-end

The website or app will run code to gather user interactions and regularly transmit them to the backend. The payload could have these fields:
 - `sessionId`: a unique identifier generated when the user starts using the website, to link together multiple payloads as being part of the same session
 - `userInfo`: a collection of data about the user, such as browser signature, locale, device type, etc
 -` dateTimeLocal` and `dateTimeUtc`: when the event took place
 - `analyticsCustomerId`: identifies the owner of the website within our system
 - `url`: which page the user is on
 - `action`: what the user did. Possible values could be “pageLoaded”, “linkClicked”, “navigatedBack”, etc
 - `actionParameters`: extra details specific to the triggering action, such as the link clicked, the mouse cursor position, how far the user scrolled down the page, etc

### Back-end

Our back-end will make use of a message queue to process these requests. It is a scalable system that allows for efficient batch processing of messages, albeit not necessarily in real time. Let’s use Kafka as an example. After hitting our API through a load balancer, messages will be queued in our Kafka cluster: this is the Producer part. The partition key could be the `sessionId`: this way, messages will be randomly distributed but related ones will live on the same system.
 
On the consumer side, we could have Spark jobs consuming these messages and performing aggregation. It makes sense to aggregate by `analyticsCustomerId`: the main use-case is merchants querying data for their business. There will be very little, if any, queries across multiple customers, so each customer’s data can essentially live in isolation. These Spark jobs can easily be set to run at least every half-hour to maintain our analytics up-to-date.

For storage, a time-series database such as Prometheus would allow efficient querying. We can store transformed data that matches the way it will be queried, making it fast to retrieve. Then a dashboard / data visualization tool such as Grafana can connect to Prometheus for querying and display purposes.

If we ever encountered a bug in the processing logic, we would need access to the original raw messages for reprocessing. We can have another Kafka consumer that simply dumps the individual messages to an append-only table in a database such as Cassandra. We can also set Kafka’s persistence to a high enough value that several days worth of data is kept before being overwritten by newer entries, so we wouldn’t even need to touch Cassandra if the errors are recent.
 
All these Apache tools can be coordinated through ZooKeeper, which ensures stability and reliability, thus minimum downtime. 
