/* PrimVsKruskal.java
   CSC 226 - Fall 2023
   Assignment 3 - Prim MST versus Kruskal MST Template
   
   The file includes the "import edu.princeton.cs.algs4.*;" so that you can use
   any of the code in the algs4.jar file. You should be able to compile your program
   with the command
   
	javac -cp .;algs4.jar PrimVsKruskal.java
	
   To conveniently test the algorithm with a large input, create a text file
   containing a test graphs (in the format described below) and run
   the program with
   
	java -cp .;algs4.jar PrimVsKruskal file.txt
	
   where file.txt is replaced by the name of the text file. Note: different operating systems have different commands.
   You should all know how to run the algs4.jar file from lab work.
   
   The input consists of a graph (as an adjacency matrix) in the following format:
   
    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>
	
   Entry G[i][j] >= 0.0 of the adjacency matrix gives the weight (as type double) of the edge from 
   vertex i to vertex j (if G[i][j] is 0.0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that G[i][j]
   is always equal to G[j][i].
*/

 import edu.princeton.cs.algs4.*;
 import java.util.Scanner;
 import java.io.File;
 import java.util.ArrayList;

//Do not change the name of the PrimVsKruskal class
public class PrimVsKruskal{
	static private Edge[] edgeTo; // shortest edge from tree to vertex
	static private double[] distTo; // distTo[w] = edgeTo[w].weight()
	static private boolean[] marked; // true if v in mst
	static private IndexMinPQ<Double> primPQ; // eligible crossing edges
	static private IndexMinPQ<Double> kruskalPQ; 
	static private ArrayList<Edge> kruskalMST = new ArrayList<Edge>(); // list of edge in kruskals mst
	static private ArrayList<Edge> notInPrim = new ArrayList<Edge>(); //edges not in the MST created by Prim
	static private boolean pvk = true; // both MST are the same
	static private boolean ufSame = true; //checks if the uf is remaining the same with each KruskalsAddEdge call


	static public void PrimMST(double[][] G, int n) {
		//System.out.println("Enter PrimMST");
		edgeTo = new Edge[n];
		distTo = new double[n];
		marked = new boolean[n];
		for(int v = 0; v < n; v++)
			distTo[v] = Double.POSITIVE_INFINITY;
		primPQ = new IndexMinPQ<Double>(n);
		distTo[0] = 0.0;
		primPQ.insert(0, 0.0);
	}

	static private ArrayList<Integer> adjacent(double[][] G, int v, int n) {
		//System.out.println("Enter adjacent");
		ArrayList<Integer> adj = new ArrayList<>(n);
		for (int w = 0; w < n; w++) {
			if (G[v][w] != 0)
				adj.add(w);
		}
		return adj;
	}

	static private boolean sameEdge(Edge e, Edge f) {
		int eV = e.either();
		int eW = e.other(eV);
		int fV = f.either();
		int fW = f.other(fV);
		if ((eV == fV && eW == fW) || (eW == fV && eV == fW)) {
			return true;
		}
		return false;
	}

	static private void visit(double[][] G, int v, int n) {
		//System.out.println("Enter visit");
		marked[v] = true;
		ArrayList<Integer> adj = adjacent(G, v, n);
		for (int w : adj) {
			if (marked[w]) {
				Edge cross = new Edge(v, w, G[v][w]);
				if (!sameEdge(cross, edgeTo[v])) {
					notInPrim.add(cross);
					
					// check if in kruskals
					for (Edge e: kruskalMST) {
						if (sameEdge(e, cross)) {
							pvk = false;
							break;
						}
					}
				}
				continue;
			}
			if (G[v][w] < distTo[w]) {
				Edge e =  new Edge(v, w, G[v][w]);
				edgeTo[w] = e;
				distTo[w] = G[v][w];
				for (Edge f : notInPrim) {
					if (sameEdge(e, f)) {
						notInPrim.remove(f);
					}
				}
				if (primPQ.contains(w)) primPQ.changeKey(w, distTo[w]);
				else primPQ.insert(w, distTo[w]);
			}
		}
	}

	static private double[][] PrimMSTMatrix(int n) {
		double[][] mst = new double[n][n];
		for (int v = 0; v < edgeTo.length; v++) {
			Edge e = edgeTo[v];
			if (e != null) {
				mst[e.either()][e.other(e.either())] = e.weight();
				mst[e.other(e.either())][e.either()] = e.weight();
			}
		}
		return mst;
	}

	static public MinPQ<Edge> ListEdges(double[][] G, int n) {
		//System.out.println("Enter ListEdges");
		MinPQ<Edge> kruskalEdges = new MinPQ<Edge>(n*n);
		for (int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				kruskalEdges.insert(new Edge(i, j, G[i][j]));
			}
		}
		return kruskalEdges;
	}

	static private UF KruskalAddEdge(UF uf, Edge e) {
		//System.out.println("Enter KruskalAddEdge");
		if (e.weight() == 0) return uf;
		ufSame = false;
		int v = e.either(), w = e.other(v);
		if (!uf.connected(v, w)) {
			uf.union(v, w);
			for (Edge i : notInPrim) {
				if (sameEdge(e, i)) {
					pvk = false;
					return uf;
				}
			}
			kruskalMST.add(e);
		}
		return uf;
	}
	
	static public double[][] KruskalMSTMatrix(int n) { 
		//System.out.println("Enter KruskalMSTMatrix");
		double[][] mst = new double[n][n];
		for (Edge e : kruskalMST) {
			if (e.weight() != 0) {
				mst[e.either()][e.other(e.either())] = e.weight();
				mst[e.other(e.either())][e.either()] = e.weight();
			}
		}
		return mst; 
	}



	/* PrimVsKruskal(G)
		Given an adjacency matrix for connected graph G, with no self-loops or parallel edges,
		determine if the minimum spanning tree of G found by Prim's algorithm is equal to 
		the minimum spanning tree of G found by Kruskal's algorithm.
		
		If G[i][j] == 0.0, there is no edge between vertex i and vertex j
		If G[i][j] > 0.0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/
	static boolean PrimVsKruskal(double[][] G){
		int n = G.length;

		PrimMST(G, n);

		MinPQ<Edge> kruskalPQ = ListEdges(G, n);
		UF uf = new UF(n);

		while(!primPQ.isEmpty() && !kruskalPQ.isEmpty()){
			visit(G, primPQ.delMin(), n);
			Edge e = kruskalPQ.delMin();
			uf = KruskalAddEdge(uf, e);
			while(ufSame) {
				e = kruskalPQ.delMin();
				uf = KruskalAddEdge(uf, e);
			}
			if(!pvk){
				return false;
			}
			ufSame = true;
		}


		if (!primPQ.isEmpty()) {
			while(!primPQ.isEmpty()) {
				visit(G, primPQ.delMin(), n);
			}
		}
		if (!kruskalPQ.isEmpty()) {
			while(!kruskalPQ.isEmpty()) {
				Edge e = kruskalPQ.delMin();
				uf = KruskalAddEdge(uf, e);
				if(!pvk){
					return false;
				}
			}
		}

		return pvk;	
	}
		
	/* main()
	   Contains code to test the PrimVsKruskal function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below. 
	*/
   public static void main(String[] args) {
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
		
		int n = s.nextInt();
		double[][] G = new double[n][n];
		int valuesRead = 0;
		for (int i = 0; i < n && s.hasNextDouble(); i++){
			for (int j = 0; j < n && s.hasNextDouble(); j++){
				G[i][j] = s.nextDouble();
				if (i == j && G[i][j] != 0.0) {
					System.out.printf("Adjacency matrix contains self-loops.\n");
					return;
				}
				if (G[i][j] < 0.0) {
					System.out.printf("Adjacency matrix contains negative values.\n");
					return;
				}
				if (j < i && G[i][j] != G[j][i]) {
					System.out.printf("Adjacency matrix is not symmetric.\n");
					return;
				}
				valuesRead++;
			}
		}
		
		if (valuesRead < n*n){
			System.out.printf("Adjacency matrix for the graph contains too few values.\n");
			return;
		}	
		
        boolean pvk = PrimVsKruskal(G);
        System.out.printf("Does Prim MST = Kruskal MST? %b\n", pvk);
    }
}
