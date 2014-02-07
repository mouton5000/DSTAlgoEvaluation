DSTAlgoEvaluation
=================

Java code to build and run evaluation of Approximation Algorithms for the Directed Steiner Tree problem

The algorithms were implemented and evaluated in order to write the paper which title is
"A practical Greedy Algorithm for the Directed Steiner Tree problem", currently submited for the SEA-2014 computer science conference. 

# Run this project

If you want to run this project, either you are reading the article and want to compare your results with ours, or you are just interested in the Directed Steiner Tree problem. 

It is a Eclipse java project, but I only give you the sources in the src directory. If you download them, be sure to also download the Steinlib folder, and put it in the root directory of you java project, as it is necessary to run the examples in the main method.

# Run the main method

The main method are in the src/Main.java class, and contains some examples you can look at and run to understand how you can use the whole code to evaluate the algorithms.

Each method is currently commented, but associated with some explanation to undurstant what they do.

# Evaluate the algorithms

This project contains the implementation of 4 algorithms to approximate the Directed Steiner Tree problem, but does not contains all the benchmark to evaluate them. You can find a benchmark at http://steinlib.zib.de/steinlib.php. But this benchmark contains only undireced instances. To get directed instances, you have to run one of the three methods createBidirectedInstances, createAcyclicInstances or createStronglyConnectedInstances from the src/Main.java class as explained in the examples in the same class.

Once it is done, launch the testAlgorithm method from the src/Main.java class, as explained in the examples in the same class, to evaluate an algorithm and return the results in the standart input. 

# Directed Steiner Tree Approximation Algorithms

The 4 approximation algorithms are
- GFLACAlgorithm, described in the articledtc
- RoosAlgorithm, from "FasterDSP: A faster approximation algorithm for directed Steiner tree problem" by Hsieh, Ming-I et Al.
- WongAlgorithm, "A dual ascent approach for Steiner Tree Problems on a directed graph", by R. Wong
- ShPAlgorithm, which computes all the shortest path from the root to all terminals.

Each algorithm class is in the package graphTheory.algorithms.steinerProblems.steinerArborescenceApproximation
