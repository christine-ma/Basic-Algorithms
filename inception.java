import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;

// Data structure for a node in a linked list
class Item {
	int data;
	Item next;

	Item(int data, Item next) {
		this.data = data;
		this.next = next;
	}
}

// Data structure for representing a graph
class Graph {
	int n;  // # of nodes in the graph

	Item[] A; 
	// For u in [0..n), A[u] is the adjecency list for u

	Graph(int n) {
		// initialize a graph with n vertices and no edges
		this.n = n;
		A = new Item[n];
	}

	void addEdge(int u, int v) {
		// add an edge u -> v to the graph

		A[u] = new Item(v, A[u]);
	}
}

// Data structure holding data computed by DFS
class DFSInfo {
	int k; 
	// # of trees in DFS forest

	int[] T;
	// For u in [0..n), T[u] is initially 0, but when DFS discovers
	// u, T[u] is set to the index (which is in [1..k]) of the tree 
	// in DFS forest in which u belongs.

	int[] L;
	// List of nodes in order of decreasing finishing time

	int count;
	// initially set to n, and is decremented every time
	// DFS finishes with a node and is recorded in L

	DFSInfo(Graph graph) {
		int n = graph.n;
		k = 0;
		T = new int[n];
		L = new int[n];
		count = n;
	}
}


// your "main program" should look something like this:

public class inception {

	static void recDFS(int u, Graph graph, DFSInfo info) {
		// perform a recursive DFS, starting at u
		info.T[u] = info.k; //On the DFS of G this assigns a vertex to a tree / component
		Item successor = graph.A[u];
		while(successor !=null) {//	Iterates through every successor
			if ((info.T[successor.data]) == 0) { //Undiscovered vertex
				recDFS(successor.data, graph, info);
			}
			successor = successor.next;
		}
		info.count--;
		info.L [info.count] = u; // "Insert" node to the end
	}

	static DFSInfo DFS(int[] order, Graph graph) {
		// performs a "full" DFS on given graph, processing 
		// nodes in the order specified (i.e., order[0], order[1], ...)
		// in the main loop.  
		DFSInfo info = new DFSInfo (graph);
		for (int i = 0; i < order.length; i++) {
			if (info.T[order[i]] == 0) { //So there is not a component assigned to vertex i
				++info.k; //Make a new root to a tree
				recDFS(order[i], graph, info);
			}
		}
		return info;
	}

	static boolean[] computeSafeNodes(Graph graph, DFSInfo info) {
		// returns a boolean array indicating which nodes
		// are safe nodes.  The DFSInfo is that computed from the
		// second DFS.
		
		//Plan:
		//For this sake True = NOT SAFE, False = SAFE
		//If component at u is not the same component as successor then safeLoc = true;
		//Then set all vertices in that component to true
		boolean [] safeComp = new boolean [info.k+1]; //Boolean array of safe Components: index 1 -> k+1
		boolean [] safeNodes = new boolean [graph.n]; //Initialize a boolean array of safe Locations: index = vertex
		for (int u = 0; u < graph.n; u++) {
			Item successor = graph.A[u];
			while(successor !=null) {//	Iterates through every successor
				if ((info.T[u]) != info.T[successor.data]) { //Nodes exist in different components
					safeComp[info.T[u]] = true;
					break;
				}
				successor = successor.next;
			}
		}
		for (int s = 1; s < safeComp.length; s++ ) {//Now set every node in a not safe component to true
			if (safeComp[s]==true) {
				for (int node = 0; node < graph.n; node++ ) {
					if (s == info.T[node]) {
						safeNodes[node]=true;
					}
				}
			}
		}

		return safeNodes;
	}

	static Graph reverse(Graph graph) {
		Graph rev = new Graph (graph.n);// size is the number of vertices
		for (int parent = 0; parent < graph.n; parent++) {
			Item successor = graph.A[parent];
			while (successor != null) {
				rev.addEdge(successor.data, parent); //flip so successor -> parent
				successor = successor.next;
			}
		}
		return rev;
	}

	//find the safe spaces = component has no out-degree so C adjacency = null
	public static void main(String[] args) throws Exception {
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		String[] data = line.split("\\s+");
		int intersections = Integer.valueOf(data[0]);
		int streets = Integer.valueOf(data[1]);
		Graph lvl = new Graph (intersections); //Initialize the graph

		for (int i = 0; i < streets; i++) { 
			line = scanner.nextLine();
			data = line.split("\\s+");
			// for u- > v:	 u = Integer.valueOf(data[0]) v = Integer.valueOf(data[1]) 
			lvl.addEdge(Integer.valueOf(data[0]), Integer.valueOf(data[1])); //[0...locations) 
		}
		//First find the reverse of lvl
		Graph reverse = reverse(lvl);

		//dfs on reverse of lvl the order in this case is just start from 0 node
		int [] firstOrder = new int [reverse.n]; //Get the order: 0..n-1
		for (int order = 0; order < reverse.n; order++) {
			firstOrder[order] = order;
		}
		DFSInfo returnOrder = DFS(firstOrder, reverse);

		//Then run dfs on the order outputted by the reverse lvl
		DFSInfo components = DFS(returnOrder.L, lvl);

		//find the safe spaces
		boolean [] safeSpace = computeSafeNodes(lvl, components);
		for (int s = 0; s < safeSpace.length; s++) { //Iterate through the boolean array to output which vertex is safe
			if (safeSpace[s] == false) {//This will naturally output nodes in numerical order
				output.append(s+" ");
			}
		}
		output.flush();
		scanner.close();

	}


}