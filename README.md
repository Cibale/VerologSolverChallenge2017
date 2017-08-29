# VerologSolverChallenge2017


Project for Verolog Solver Challenge 2017. , more info about it: https://verolog.ortec.com/wp-content/uploads/2016/06/Challenge_problem.pdf


In this file is described the flow of the algorithm.  

Flow of algorithm consists of two main steps:  
	1) Decide which request will be executed on which day  
	2) Decide which vehicle will execute which request  

For 1) we have two possible solutions:  
	1.1) Random  
	1.2) Greedy algorithm  	
	1.3) Genetic algorithm
	1.4) DFS with memoization  

After we decide days, we create "negative requests", i.e. request when we need to pick up tools from customer.  
  
For 2) we propose Genetic algorithm as solution with several greedy optimizations.  
  
1.1) Random  
	- for each request, set some random (in bounds) day when it is executed  
	- that didn't go so well :)  
  	
1.2) Greedy algorithm  
	- for each request, set day when it is executed in the following way:  
		1. look for all days when it can be executed.  
		2. pick a day which is "most empty", i.e. day where the particular tool is least used.   
  
1.3) Genetic Algorithm description  
	1.3.1) Chromosome description  
        Chromosome consists of request list. daysMap where key of the map is day number and value is other map where key is tool id and value its usage.  
	1.3.2) Crossover description  
        There are two types of crossovers. In crossover type 1 there is one cross point x which is random number between 0 and number of requests. Childs requests from 0 to x are setted at same days as first parents and other part is taken from other parent. Crossover type 2 focuses on days which exceeds maximal number of available tool. It chooses requests corresponding to day and tool slot which exceed number tools available. Day and tool type are selected using wheel selection.  
Crossover of type 2 is selected with probability of 0.6 only if there are slots which are exceeded.  
     1.3.3) Mutation description  
    		There are two types of mutation. In first type each request has probabiltiy to be assigned to random day. Second type selects again with wheel selection day and tool slot which exceed number of tools available, and each request corresponding to that slot has probability to be assigned to random day.  
Mutation of type 2 is selected with probability of 0.6 only if there are slots which are exceeded.  
 	1.3.4) Selection description  
    		For selection 3-tournament is used.  
	
	1.3.5) Chosing right parameters  
    		Number of generations 500. After that algorithm gets stuck in local optima.  
    		Mutation 0.3.  
    		Population number 80.  
    		Crossover probability 0.85  
  
	1.3.5) Evaluation function  
	Maximal number of tools consumption is used as cost and as punishment exceeded value of each slot.  
  
  
1.4) DFS with memoization description  
	- idea: Attach for each request on which day it is executed, but take care about count of tools  
	- <day<toolId, count>> - map for memoization  
	- sort requests in array by tool they use, lastDayDelivery - firstDayDelivery, reverse(numberOfTools)  
	- each node is requst with some day for delivery in interval [firstDay, lastDay]  
	- children are nodes generated in the following way:  
		- all children have request.id of next request in an array  
		- picked day for delivery is one of the values between firstDay and lastDay  
		- all children differ only in picked day  
	- check if some branch is available by using a map above  
  	
  
2) Description of Genetic Algorithm  
	2.1) Chromosome description  
		We have array of requests and array of vehicles.  
			Each request has a pointer on a vehicle which executes it.  
			Each vehicle has pointers to requests it must execute.  
  				- additionally, vehicle stores route which he needs to take, capacity of route etc.  
  	
	2.2) Crossover description  
		If there is a crossover, then it is single point crossover in array of requests.  
  	
	2.3) Mutation description  
		For each request, if there is a mutation, attach it some random vehicle.  
  	
	2.4) Selection description  
		3-tournament selection  

	2.5) Evaluation function  
		Total cost of a solution. Punishment is calculated based on exceeded distance and exceeded capacity.  
  
	2.5) Chosing right parameters  
		Best solutions (w.r.t. time) with the following parameters:  
			Generation size: 100  
			Number of generations: 500  
			Crossover probability: 0.8  
			Mutation probability: 0.2  
  
3) Several optimizations which are done "on the way"  
	3.1) Optimizing route distance  
		Greedy algorithm - pick next customer which is closest to the previous customer (or depot if first customer)  	  
	3.2) Optimizing route capacity  
		If there are requests with same tool id, put negative requests first.  
		If capacity is still exceeded, go to depot. Put the last request as first request in a new tour.  

