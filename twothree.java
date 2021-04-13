//import java.io.*;
//import java.util.*;
/*class Node {
    String guide;
    // guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
    Node child0, child1, child2;
    // child0 and child1 are always non-null
    // child2 is null iff node has only 2 children
}

class LeafNode extends Node {
    // guide points to the key

    int value;
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

public class twothree {

    public static void main(String[] args) throws Exception{
        TwoThreeTree tree = new TwoThreeTree();
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        for (int i =0; i < n;i++) {
            String line = scanner.nextLine();
            String[] data = line.split("\\s+");
            insert(data[0], Integer.valueOf(data[1]),tree);
        }
        int m = Integer.parseInt(scanner.nextLine()); //number of queries
        for (int i= 0; i < m;i++) {
            String line = scanner.nextLine();
            String[] query = line.split("\\s+");
            String out = printRange(query[0],query[1],tree);
            output.write(out);
        }
        output.flush();
        scanner.close();

    }
    static String printRange(String a, String b, TwoThreeTree tree) throws Exception {
        StringWriter out = new StringWriter();
        if(tree.root==null) {//Empty Tree
            return "";
        }
        String x = a; //Key ordering
        String y = b;
        if (a.compareTo(b) > 0) {
            x = b;
            y = a;
        }
        Node [] pathX = search(tree,x); //Search Paths
        Node [] pathY = search(tree,y);
        Node diverge = null;
        int divergeheight = 0;
        String leafXGuide = pathX[pathX.length-1].guide; //Guides used for node comparison
        String leafYGuide = pathY[pathY.length-1].guide;
        if (x.compareTo(leafXGuide)<=0 && y.compareTo(leafXGuide)>=0){//Checking whether x is in the tree
            String s = (pathX[pathX.length-1].guide+' '+ ((LeafNode)(pathX[pathX.length-1])).value+"\n");//X Leaf
            out.append(s);
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
                if(pathX[i]==diverge) {
                    break;
                }
                if ((leafXGuide).compareTo(((InternalNode)(pathX[i])).child1.guide) <0) {
                    leafXGuide = ((InternalNode)(pathX[i])).child1.guide; //Change the compared node so that there is no repeat prints
                    String s =  printAll(((InternalNode)(pathX[i])).child1, i+1,tree.height);
                    out.append(s);
                }
                if (((InternalNode)(pathX[i])).child2 !=null && (leafXGuide).compareTo(((InternalNode)(pathX[i])).child2.guide) < 0) {
                    leafXGuide = ((InternalNode)(pathX[i])).child2.guide;
                    String s = printAll(((InternalNode)(pathX[i])).child2, i+1,tree.height);
                    out.append(s);

                }
            }
            //Process diverge if there is a middle child and x and y are on different branches
            if (((leafXGuide.compareTo(((InternalNode)diverge).child0.guide)<=0)) && (((InternalNode)diverge).child1.guide).compareTo(leafYGuide)<0){
                String s = printAll(((InternalNode)diverge).child1,divergeheight+1,tree.height);
                out.append(s);
            }
            //Write for y
            for (int i = divergeheight+1; i < pathY.length-1;i++) {//Process nodes on pathY until Leaf
                if (((InternalNode)(pathY[i])).child0.guide.compareTo(leafYGuide) <0 ) {
                    String s = printAll((((InternalNode)(pathY[i])).child0),i+1,tree.height);
                    out.append(s);
                }
                if (((InternalNode)(pathY[i])).child1.guide.compareTo(leafYGuide) <0 ) {
                    String s = printAll((((InternalNode)(pathY[i])).child1),i+1,tree.height);
                    out.append(s);
                }
            }
            if(x.compareTo(leafYGuide)<=0 && y.compareTo(leafYGuide)>=0) {//Checking whether y is in the tree
                String s = (pathY[pathY.length-1].guide+' '+ ((LeafNode)(pathY[pathY.length-1])).value+"\n");//Y leaf
                out.append(s);
            }
        }
        out.close();
        return out.toString();
    }

    static String printAll(Node root, int nodeHeight, int treeHeight){
        StringWriter sWriter = new StringWriter();
        if(nodeHeight == treeHeight) {//LeafLevel    
            String s =(root.guide+' '+((LeafNode)root).value +"\n");
            sWriter.append(s);
            return sWriter.toString();
        }
        InternalNode rt = ((InternalNode)root);
        String s = printAll(rt.child0,nodeHeight+1,treeHeight);
        sWriter.append(s);
        String s1 = printAll(rt.child1,nodeHeight+1,treeHeight);
        sWriter.append(s1);
        if(rt.child2!=null) {
            String s2 = printAll(rt.child2,nodeHeight+1,treeHeight);
            sWriter.append(s2);
        }
        return sWriter.toString();
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

}*/

