/* BaseballElimination.java
   CSC 226 - Fall 2023
   Assignment 5 - Baseball Elimination Program

   Marissa Parsons
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java BaseballElimination

	compile: javac -cp algs4.jar BaseballElimination.java
	
   To conveniently test the algorithm with a large input, create a text file
   containing one or more test divisions (in the format described below) and run
   the program with
	java -cp .;algs4.jar BaseballElimination file.txt (Windows)
   or
    java -cp .:algs4.jar BaseballElimination file.txt (Linux or Mac)
   where file.txt is replaced by the name of the text file.
   
   The input consists of an integer representing the number of teams in the division and then
   for each team, the team name (no whitespace), number of wins, number of losses, and a list
   of integers represnting the number of games remaining against each team (in order from the first
   team to the last). That is, the text file looks like:
   
	<number of teams in division>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>

	
   An input file can contain an unlimited number of divisions but all team names are unique, i.e.
   no team can be in more than one division.
*/

import edu.princeton.cs.algs4.*;
import java.util.*;
import java.io.File;

//Do not change the name of the BaseballElimination class
public class BaseballElimination{
	
	// We use an ArrayList to keep track of the eliminated teams.
	public ArrayList<String> eliminated = new ArrayList<String>();
	private ArrayList<Integer>[] teams;
	private ArrayList<String> teamNames = new ArrayList<String>();
	private Integer numTeams;
	private Integer curLead = 0;
	private Integer sCapacity;
	private Integer vertices;

	/* BaseballElimination(s)
		Given an input stream connected to a collection of baseball division
		standings we determine for each division which teams have been eliminated 
		from the playoffs. For each team in each division we create a flow network
		and determine the maxflow in that network. If the maxflow exceeds the number
		of inter-divisional games between all other teams in the division, the current
		team is eliminated.
	*/
	public BaseballElimination(Scanner s){
		CreateTeamsArray(s);
		EliminateTeams();

	}

	/*
	* EliminateTeams()
	* checks all teams in teams array
	* first check to see if team can even achieve more wins than that of the current most wins
	* then creates and checks if team is eliminated through a flow network
	*/
	public void EliminateTeams(){
		//determine number of vertices needed in flow network
		vertices = ((numTeams-1)*(numTeams-2))/2 + numTeams + 1;

		for (int i = 0; i < numTeams; i++) {
			//determine if team can get more wins than the current most wins
			int curWins = (Integer) teams[i].get(0);
			int potentialWins = (Integer) teams[i].get(1);
			if (curWins + potentialWins < curLead) {
				eliminated.add(teamNames.get(i));
				continue;
			}
			
			//determine if team is eliminated through network flow
			FlowNetwork networkGraph = CreateNetworkGraph(i);

			FordFulkerson fordNetwork = new FordFulkerson(networkGraph, vertices - 1, i);
			//System.out.println(networkGraph.toString());
			if (fordNetwork.value() < sCapacity) {
				eliminated.add((String) teamNames.get(i));
			}
		}
	}

	/* 
	* CreateNetworkGraph(t)
	* Creates a network flow which can determine a teams elimination
	* input: the index corresponding to a team in our teams array
	*
	* our s is our highest numbered vertex.
	* our t vertex is the same index in the teams array that os associated to the team the network is being created for
	* vertexs of 0 to number of teams - 1 represent the teams in the same indices of the teams array
	* for example: if teams array has order [Detroit, Golden_State, Huston, Indiana, LA_Lakers] and we call 
	* 			   CreateNetworkGraph(1), we will be creating a network flow to determine the elimination of Golden_State
	*			   t = 1, and vetices 0,2,3,4 will be the vertices representing Detroit, Huston, Indiana, and LA_Lakers
	*			   respectively. 0,2,3,4 will be the only vertices with edges going to t.
	*			   s will be our highest element, 12, and will have edges to vertices 5-11
	*/

	public FlowNetwork CreateNetworkGraph(int t){
		//reset sCapacity for new Network Graph
		sCapacity = 0;

		FlowNetwork networkGraph = new FlowNetwork(vertices);
		
		//our s is our highest index
		int s = vertices - 1;

		//create edges from vertices representing teams to t
		for (int i = 0; i < numTeams; i++) {
			if (i == t) {
				continue;
			}
			FlowEdge edge = new FlowEdge(i, t, teams[t].get(0) + teams[t].get(1) - teams[i].get(0));
			networkGraph.addEdge(edge);
		}

		//create edges from s to vertices representing games and edges from games vertices to teams vertices
		//counter to add edges to vertices s-l 
		int l = 1;
		for (int j = 0; j < numTeams; j++) {
			if (j == t) continue;
			for (int k = j+1; k < numTeams; k++) {
				if (k == t) continue;
				int capacity = teams[j].get(k+2);
				sCapacity += capacity;
				//edge s to game
				FlowEdge edge = new FlowEdge(s, s-l, capacity);
				networkGraph.addEdge(edge);
				//edges game to teams
				edge = new FlowEdge(s-l, j, Double.POSITIVE_INFINITY);
				networkGraph.addEdge(edge);
				edge = new FlowEdge(s-l, k, Double.POSITIVE_INFINITY);
				networkGraph.addEdge(edge);
				l++;
			}

		}

		return networkGraph;
	}

	/* 
	* CreateTeamsArray()
	*
	* Creates 2 arrays about teams from information passed through scanner
	* array 1: teams - an array of arrays with integer information about the teams
	*			nested array content: current wins, games left, games per team for each team
	* array 2: teamNames - array of strings of the team names
	* information in teams[i] and teamNames[i] are about the same team
	*/
	public void CreateTeamsArray(Scanner s){
		numTeams = Integer.parseInt(s.next());
		teams = new ArrayList[numTeams];

		//add arrays to indices in teams array
		int i = 0;
		while (i < numTeams) {
			teams[i] = new ArrayList<>();
			i++;
		}
		
		i = 0;
		while (s.hasNext()) {
			//index for specific team array
			int j = 0;	

			teamNames.add(s.next());

			int wins = Integer.parseInt(s.next());
			if (wins > curLead) {
				curLead = wins;
			}
			teams[i].add(wins);						
			teams[i].add(Integer.parseInt(s.next()));					
			while (s.hasNext() && j < numTeams) {
				teams[i].add(Integer.parseInt(s.next()));
				j++;
			}
			i++;
		}
	}
		
	/* main()
	   Contains code to test the BaseballElimantion function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){

		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		
		BaseballElimination be = new BaseballElimination(s);		

		if (be.eliminated.size() == 0)
			System.out.println("No teams have been eliminated.");
		else
			System.out.println("Teams eliminated: " + be.eliminated);
	}
}
