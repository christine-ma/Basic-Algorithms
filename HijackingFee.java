import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;

class Node {
	String guide;
	int value;
}

class InternalNode extends Node {
	Node child0, child1, child2;
}

class LeafNode extends Node {
}
class TwoThreeTree {
	Node root;
	int height;

	TwoThreeTree() {
		root = null;
		height = -1;
	}
}

class WorkSpace {
	// this class is used to hold return values for the recursive doInsert
	// routine (see below)

	Node newNode;
	int offset;
	boolean guideChanged;
	Node[] scratch;
}


public class HijackingFee {

	public static void main(String[] args) throws Exception  {
		TwoThreeTree tree = new TwoThreeTree();
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		Scanner scanner = new Scanner(System.in);
		int n = Integer.parseInt(scanner.nextLine());
		for (int i = 0; i < n; i++) {
			String line = scanner.nextLine();
			String[] data = line.split("\\s+");
			switch (Integer.valueOf(data[0])) {
			case 1: //Insert Node + Value of Node
				insert (data[1], Integer.valueOf(data[2]), tree);
				break;
			case 2:
				//Increase all entrance fees between a and b, by k amount, pass through the tree
				addRange(data[1], data[2], Integer.valueOf(data[3]), tree);
				break;
			default: //Case: 3
				///Return entrance fee for planet name a
				String out = findFee(tree, data[1]);
				output.append(out);
				break;
			}
		}
		output.flush();
		scanner.close();


	}
	static Node[] search (TwoThreeTree tree, String s) {
		Node[] path = new Node [tree.height+1];
		int h = tree.height;
		Node p = tree.root;
		path[0]=p;
		for (int i = 0; i < h; i++) {
			InternalNode intp = (InternalNode)p;
			if (s.compareTo(intp.child0.guide) <=0) {
				p = intp.child0;    
			}
			else if((intp.child2 == null || (s.compareTo(intp.child1.guide) <=0))){
				p =  intp.child1;
			}
			else {
				p = intp.child2;
			}
			path[i+1]=p;
		}
		return path;
	}

	static String findFee (TwoThreeTree tree, String key) { //Search function to find the value of the key
		int totalFee = 0;
		int h = tree.height;
		Node p = tree.root;
		for (int i = 0; i < h; i++) {
			InternalNode intp = (InternalNode) p;
			totalFee += p.value;
			if (key.compareTo(intp.child0.guide) <=0) {
				p = intp.child0;
			}
			else if((intp.child2 == null || (key.compareTo(intp.child1.guide) <=0))){
				p =  intp.child1;
			}
			else {
				p = intp.child2;
			}
		}
		if (key.compareTo(p.guide)==0) { //Leaf Level
			totalFee += p.value;
			return String.valueOf(totalFee)+"\n";
		}

		return "-1\n";
	}

	static void addRange (String a, String b, int updateValue, TwoThreeTree tree) {
		if(tree.root==null) {//Empty Tree
			return;
		}
		String x = a; //Key ordering
		String y = b;
		if (a.compareTo(b) > 0) {
			x = b;
			y = a;
		}
		Node [] pathX = search(tree,x); //Search Paths
		Node [] pathY = search(tree,y);
		String leafXGuide = pathX[pathX.length-1].guide; //Guides used for node comparison
		String leafYGuide = pathY[pathY.length-1].guide;
		Node diverge = null;
		int divergeheight = 0;
		for (int i = 0; i<pathX.length;i++) {
			if (pathX[i]!=pathY[i]) {
				diverge = pathX[i-1];//Diverge Node Exists
				divergeheight = (i-1);
				break;
			}
		}
		//Check Edges
		if (x.compareTo(leafXGuide)<=0 && y.compareTo(leafXGuide)>=0){//Checking whether x is in the tree
			addAll(pathX[pathX.length-1], updateValue); //Update Leaf X value
		}
		for (int i = 0; i<pathX.length;i++) {
			if (pathX[i]!=pathY[i]) {
				diverge = pathX[i-1];//Diverge Node Exists
				divergeheight = (i-1);
				break;
			}
		}
		if (diverge !=null) {//There is a diverge point
			//Write for x
			for (int i = (pathX.length-2); i >= 0;i--) {//Start at node next to leaf
				InternalNode intX = ((InternalNode)(pathX[i]));
				if(pathX[i] == diverge) {
					break;
				}
				if ((leafXGuide).compareTo(intX.child1.guide) < 0) {
					leafXGuide = intX.child1.guide; //Change the compared node so that there is no repeat additions
					addAll(intX.child1, updateValue);
				}
				if (intX.child2 != null && (leafXGuide).compareTo(intX.child2.guide) < 0) {
					leafXGuide = intX.child2.guide;
					addAll(intX.child2, updateValue);

				}
			}

			//Process diverge if there is a middle child and x and y are on different branches
			if (((leafXGuide.compareTo(((InternalNode)diverge).child0.guide)<=0)) && (((InternalNode)diverge).child1.guide).compareTo(leafYGuide)<0){
				addAll(((InternalNode)diverge).child1, updateValue);
			}

			//Write for y
			for (int i = divergeheight + 1; i < pathY.length-1; i++) {//Process nodes on pathY until Leaf
				InternalNode intY = ((InternalNode)(pathY[i]));
				if (intY.child0.guide.compareTo(leafYGuide) < 0 ) {
					addAll((intY.child0),updateValue);
				}
				if (intY.child1.guide.compareTo(leafYGuide) < 0 ) {
					addAll((intY.child1),updateValue);
				}
			}
			if(x.compareTo(leafYGuide)<=0 && y.compareTo(leafYGuide)>=0) {//Checking whether y is in the tree
				addAll(pathY[pathY.length-1], updateValue); //Update Leaf Y value
			}
		}
	}
	
	static void addAll(Node node, int updateValue)    {
		node.value += updateValue;
		return;
	}

	static void insert(String key, int value, TwoThreeTree tree) {
		// insert a key value pair into tree (overwrite existsing value
		// if key is already present)

		int h = tree.height;

		if (h == -1) {
			LeafNode newLeaf = new LeafNode();
			newLeaf.guide = key;
			newLeaf.value = value;
			tree.root = newLeaf; 
			tree.height = 0;
		}
		else {
			WorkSpace ws = doInsert(key, value, tree.root, h);

			if (ws != null && ws.newNode != null) {
				// create a new root

				InternalNode newRoot = new InternalNode();
				if (ws.offset == 0) {
					newRoot.child0 = ws.newNode; 
					newRoot.child1 = tree.root;
				}
				else {
					newRoot.child0 = tree.root; 
					newRoot.child1 = ws.newNode;
				}
				resetGuide(newRoot);
				tree.root = newRoot;
				tree.height = h+1;
			}
		}
	}

	static WorkSpace doInsert(String key, int value, Node p, int h) {
		// auxiliary recursive routine for insert

		if (h == 0) {
			// we're at the leaf level, so compare and 
			// either update value or insert new leaf

			LeafNode leaf = (LeafNode) p; //downcast
			int cmp = key.compareTo(leaf.guide);

			if (cmp == 0) {
				leaf.value = value; 
				return null;
			}

			// create new leaf node and insert into tree
			LeafNode newLeaf = new LeafNode();
			newLeaf.guide = key; 
			newLeaf.value = value;

			int offset = (cmp < 0) ? 0 : 1;
			// offset == 0 => newLeaf inserted as left sibling
			// offset == 1 => newLeaf inserted as right sibling

			WorkSpace ws = new WorkSpace();
			ws.newNode = newLeaf;
			ws.offset = offset;
			ws.scratch = new Node[4];

			return ws;
		}
		else {
			InternalNode q = (InternalNode) p; // downcast
			int pos;
			WorkSpace ws;
			//Push the value of the internal node into the child and then zero the internal node
			q.child1.value += q.value;
			q.child0.value += q.value;
			if (q.child2 !=null) {
				q.child2.value += q.value;
			}
			q.value = 0;
			if (key.compareTo(q.child0.guide) <= 0) {
				pos = 0; 
				ws = doInsert(key, value, q.child0, h-1);
			}
			else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
				pos = 1;
				ws = doInsert(key, value, q.child1, h-1);
			}
			else {
				pos = 2; 
				ws = doInsert(key, value, q.child2, h-1);
			}

			if (ws != null) {
				if (ws.newNode != null) {
					// make ws.newNode child # pos + ws.offset of q

					int sz = copyOutChildren(q, ws.scratch);
					insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
					if (sz == 2) {
						ws.newNode = null;
						ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
					}
					else {
						ws.newNode = new InternalNode();
						ws.offset = 1;
						resetChildren(q, ws.scratch, 0, 2);
						resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
					}
				}
				else if (ws.guideChanged) {
					ws.guideChanged = resetGuide(q);
				}
			}

			return ws;
		}
	}

	static int copyOutChildren(InternalNode q, Node[] x) {
		// copy children of q into x, and return # of children

		int sz = 2;
		x[0] = q.child0; x[1] = q.child1;
		if (q.child2 != null) {
			x[2] = q.child2; 
			sz = 3;
		}
		return sz;
	}

	static void insertNode(Node[] x, Node p, int sz, int pos) {
		// insert p in x[0..sz) at position pos,
		// moving existing extries to the right

		for (int i = sz; i > pos; i--)
			x[i] = x[i-1];

		x[pos] = p;
	}

	static boolean resetGuide(InternalNode q) {
		// reset q.guide, and return true if it changes.

		String oldGuide = q.guide;
		if (q.child2 != null)
			q.guide = q.child2.guide;
		else
			q.guide = q.child1.guide;

		return q.guide != oldGuide;
	}


	static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
		// reset q's children to x[pos..pos+sz), where sz is 2 or 3.
		// also resets guide, and returns the result of that

		q.child0 = x[pos]; 
		q.child1 = x[pos+1];

		if (sz == 3) 
			q.child2 = x[pos+2];
		else
			q.child2 = null;

		return resetGuide(q);
	}

}




