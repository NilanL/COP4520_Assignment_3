# COP4520 Assignment 3

## Part 1

### Methodology

While the problem seemed concerning at first, I consulted the book for insight in how to implement a concurrent linked list. The implementation I decided to work on was the "Optimistic Sychronization" linked list in section 9.6. This is because this form of concurrent linked list was the quickest form to implement while still using locks. It is also very optimal for doing quick efficient searches through a large set of data. With the linked list potentially getting up to 500,000 elements long, such a functionality was necessary. I made modifications to the data structures to support two necessary functionalities for the simulation of randomness with getRandom(), and for the recording of the length of the linked list. The length of the linked list was often checked to verify when the list is no longer full. Actions are randomly selected as a number, and the action is randomly chosen with the use of Random() and a switch. I also created a counter to manage 

I generated the list of unordered presents with the use of Java streams.

</br>
### Experimental Evaulation

To ensure that the algorithm was correctly functioning, I added multiple temporary in-line comments to verify decrementing/incrementing of presents. I also verified the proper use of locks by inspecting the data to ensure that a mutual exclusion error was not occuring. Afterwards I then inititated multiple rounds of testing with 1 and 4 threads to verify its multithreaded nature. Results below.

1 Thread
500 - 0.166 seconds
5000 - 1.324 seconds
50000 - 13.524 seconds
500000 - 147.227 seconds

4 Threads
500 - 0.164 seconds
5000 - 1.223 seconds
50000 - 11.867 seconds
500000 - 123.014 seconds

</br>
## Part 2

### Methodology
I created a lock-based array list implementation for the storing of sensor readings, one for each sensor 8 total.
I then stored each lock-based array list within a larger array list which represents one minute of time. With each minute having each sensor write its reading into it's own respective area in the lock-based array list. I used a custom exception "SensoReadingCancelledException" for when the current reading has not yet finished operation, but a new reading must be inititated. 

### Experimental Evaulation

## Running

1. Pull repo
2. Navigate to local repo directory
3. Run command "javac Assignment3.java"
4. Run command "java Assignment3" for both parts
5. Run command "java Assignment3 part1" for the first part
6. Run command "java Assignment3 part2" for the second part only
7. Part 1 will be printed as the process occurs
8. Part 2 will be printed after the process has concluded
