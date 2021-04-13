import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Scanner;

class Candidate {
	String name;
	Long priority; //This will be Candidate Score
	int pos; //This is for finding the position of the minHeap 1...n, the index of heap is (pos -1)
	Candidate (String name, Long priority, int pos){
		this.name = name;
		this.priority = priority;
		this.pos = pos;
	}

}
class minHeap{//currentSize the heap from 1 to n
	private Candidate[] heap; //Heap filled with objects
	int currentSize; //The number of items, acts like the "size" of the current array
	int fullSize; //Initial Size of Array
	minHeap(int fullSize){
		this.fullSize = fullSize;
		this.currentSize = 0;		
		heap = new Candidate[fullSize]; //Initialize whole array
	}
	private int parent (int pos) {
		return (pos/2);
	}
	private int left (int pos) {
		return (2*pos);	
	}
	private int right (int pos) {
		return (2*pos)+1;	
	}
	public void insert (Candidate c) { //No need to worry about more elements inserted since they only get deleted afterwards
		heap[currentSize++] = c; //Pos = 1...n, Index = 0, n-1
		if ((c.pos)-1 != 0) {// If not the main root
			floatUp(c);
		}
	}
	public Long peek() {
		return heap[0].priority;
	}
	public void floatUp(Candidate obj) {
		if (obj.pos == 1) { //Object is at the highest point
			return;
		}
		if (heap[parent(obj.pos)-1].priority > obj.priority) { //Keep Floating
			swap(obj, heap[parent(obj.pos)-1]);
			floatUp(obj);
		}
	}
	public void floatDown (Candidate obj) {
		if ((obj.pos) *2 <= currentSize-1) { //If obj.pos is at the bottom height, no need to float
			if (right(obj.pos)-1 > currentSize){ //In the event that a right child does not exist check the left child
				if (heap[left(obj.pos)-1].priority < obj.priority) {
					swap (heap[left(obj.pos)-1], obj);
					floatDown(obj);
				}
				else {
					return;
				}
			}
			else if ((heap[left(obj.pos)-1].priority < heap[obj.pos-1].priority )||( heap[right(obj.pos)-1].priority < heap[obj.pos-1].priority )) { //Swap required
				if (heap[left(obj.pos)-1].priority < heap[right(obj.pos)-1].priority) { //Float element down left
					swap (heap[left(obj.pos)-1], obj);
					floatDown(obj);
				}
				else { //Right Side is Less, Float down right
					swap (heap[right(obj.pos)-1], obj);
					floatDown(obj);
				}
			}
		}

	}

	public Candidate delete() {
		Candidate min = heap[0];
		heap[0] = heap[currentSize-1]; //Take the last element in the position and put on top
		heap[0].pos = 1;
		heap[currentSize-1] = null; 
		currentSize--;
		//Call method to fix tree starting from root
		floatDown(heap[0]);
		return min;
	}
	public void swap (Candidate small, Candidate large) {
		Candidate temp = large;
		int largePos = large.pos;
		heap[(large.pos)-1] = small;
		heap[(small.pos)-1] = temp;
		large.pos = small.pos;
		small.pos = largePos;
	}
}


public class sparta {

	public static void main(String[] args) throws Exception{
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
		Scanner scanner = new Scanner(System.in);
		int n = Integer.parseInt(scanner.nextLine()); //Number of Candidates
		HashMap<String, Candidate> canMap = new HashMap<>(); 
		minHeap minHeap = new minHeap (n);
		for (int i = 0; i < n; i++) { //s is the name of the candidate and a is the original score of the candidate.
			String line = scanner.nextLine();
			String[] data = line.split("\\s+");
			Candidate c = new Candidate(data[0],Long.valueOf(data[1]),i+1); //Name, Priority, (Initial) pos of minHeap
			canMap.put(c.name, c); //Pass the Name and Whole Object (location)
			minHeap.insert(c); //Whole Object (location)
		}
		int m = Integer.parseInt(scanner.nextLine()); //Number of Operations
		for (int i = 0; i < m; i++) {
			String line = scanner.nextLine();
			String[] data = line.split("\\s+");
			switch (Integer.valueOf(data[0])) {
			case 1: //1 s b, which tells that the score of candidate s has improved by b
				canMap.get(data[1]).priority = Long.valueOf(data[2]) + canMap.get(data[1]).priority;
				minHeap.floatDown(canMap.get(data[1])); //Since priorities were increased, fix heap
				break;
			default: //Case: 2 k which tells that an evaluation has been conducted with a standard k.
				Long k = Long.valueOf(data[1]);
				while(minHeap.peek() < k){
					//All candidates with score less than k will be permanently discarded in this evaluation.
					Candidate delete = minHeap.delete();
					canMap.remove(delete.name);
				}
				output.append(String.valueOf(canMap.size())+"\n");
				break;
			}

		}
		output.flush();
		scanner.close();

	}

}
