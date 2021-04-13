/*import java.io.*;
import java.util.*;

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
		// add an edge i -> j to the graph

		A[u] = new Item(v, A[u]);
	}
}

// Data structure holding data computed by DFS
class DFSInfo {

	// node colors
	static final int WHITE = 0;
	static final int GRAY  = 1;
	static final int BLACK = 2;

	int[] color;  // variable storing the color
	// of each node during DFS
	// (WHITE, GRAY, or BLACK)

	int[] parent; // variable storing the parent 
	// of each node in the DFS forest

	int d[];      // variable storing the discovery time 
	// of each node in the DFS forest

	int f[];      // variable storing the finish time 
	// of each node in the DFS forest


	DFSInfo(Graph graph) {
		int n = graph.n;
		color = new int[n];
		parent = new int[n];
		d = new int[n];
		f = new int[n];
	}
}


// your "main program" should look something like this:

public class labrynth {
	public static int time = 0; //global time variable

	static void recDFS(int u, Graph graph, DFSInfo info) {
		// perform a recursive DFS, starting at u	
		info.color[u] = 1;//grey
		info.d[u] = ++time;

		Item successor = graph.A[u];
		while(successor !=null) {//	Iterates through every successor
			if ((info.color[successor.data])==0) { // successor is white
				info.parent[successor.data] = u;
				recDFS(successor.data, graph, info);
			}
			successor = successor.next;
		}

		info.color[u] = 2; // u is black
		info.f[u] = ++time;
	}


	static DFSInfo DFS(Graph graph) {

		// performs a "full" DFS on given graph
		DFSInfo info = new DFSInfo (graph);
		for (int i = 0; i< graph.n; i++) {
			info.color[i] = 0; //white
			info.parent[i] = -1;//Out of bounds parent represents the null
		}
		for (int i = 0; i< graph.n; i++) {
			if (info.color[i] == 0) {
				recDFS(i, graph, info);
			}
		}
		return info;
	}

	static Item findCycle(Graph graph, DFSInfo info) {
		// If graph contains a cycle x_1 -> ... x_k -> x_1,
		// return a pointer to the head of the linked list
		// (x_1,..., x_k); otherwise, return null.
		// NOTE: if there is a cycle, you should just return
		// one cycle --- it does not matter which one.

		// To do this, scan through the edges of graph,
		// using info.f to locate a back edge.
		// Once you find a back edge, use info.parent
		// to build the list of nodes in the cycle
		// in the correct order.
		Item toReturn = null;
		for (int u = 0; u < graph.n; u++) { //Iterate through the nodes to find a back-edge
			Item successor = graph.A[u];
			while (successor!=null) { 
				//Check for a back edge
				boolean backEdgeCheck = ((info.f[u]) <= (info.f[successor.data]));	//ƒ[u] <= ƒ[v]

				if (( backEdgeCheck) == true) { //BackEdge Exists = Cycle Exists

					toReturn = new Item (u, null); //Add the "last" node with the back edge


					if (u == successor.data) { //If the back edge is a self loop cycle
						toReturn = new Item (successor.data, toReturn);
						return toReturn;
					}

					int parent = info.parent[u]; //Get the parent of u

					do { //If the back edge points to a different node than itself
						toReturn = new Item(parent, toReturn);

						parent = info.parent[parent]; //update the parent	
					} while(parent != successor.data);

					toReturn = new Item (parent, toReturn);	//Add the last parent to finish the cycle
					return toReturn; //Since a cycle is found, return
				}
				successor = successor.next;//Keep checking
			}

		}

		//No backEdge, therefore no cycle exists
		return toReturn;
	}

	public static void main(String[] args) throws Exception {
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		//n # of rooms , m # of passages — a, b passage

		Scanner scanner = new Scanner(System.in);

		String line = scanner.nextLine();

		String[] data = line.split("\\s+");
		int rooms = Integer.valueOf((data[0])); //# of rooms "m"
		int passages = Integer.valueOf((data[1]));
		Graph lab = new Graph (rooms);

		for (int i = 0; i < passages; i++) { //# of passages "n"
			line = scanner.nextLine();
			data = line.split("\\s+");
			int u = Integer.valueOf((data[0])); 
			int v = Integer.valueOf((data[1])); 
			lab.addEdge(u-1, v-1); //u-1, v-1, because the index goes [0...rooms) so add 1 when outputting
		}
		DFSInfo infoResult = DFS(lab);
		Item cycle = findCycle(lab, infoResult);
		if (cycle !=null) {
			output.append("1\n");
			while(cycle !=null) {//	Iterates through the cycle
				output.append ((cycle.data+1) +" "); //Add 1 because of the index
				cycle = cycle.next;
			}
		}
		else {
			output.append("0\n");
		}

		output.flush();
		scanner.close();
	}

}*/