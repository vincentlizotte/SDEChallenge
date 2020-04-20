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

Not done yet