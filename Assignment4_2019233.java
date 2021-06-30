package ThreeLeggedDefenders;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.io.*;
import java.lang.*;


//Tree Node class
class TreeNode
{
	//to store value
	public int val;
	
	//to store the children
	public List<TreeNode> adjacentNodes;
	
	//to store the depth of the node
	public int depth;
	
	//if the node is visited or not
	public boolean visited;
}


//tree class for constructing tree
class Tree 
{	
	//Root of the tree 
	public TreeNode Root;
	
	//count of the Nodes in the tree 
	public static int NodeCount;
	
	//height of the tree
	private int height;
	
	//random number generator object
	private Random rand;
	
	//hashmap to store treenode values to improve time efficiency of the algorithm
	public static HashMap<Integer,Integer> treeNodes = new HashMap<Integer, Integer>();
	
	//hashmap to store values given by user if they are present or not to improve time efficiency of the algorithm
	public static HashMap<Integer,Integer> values = new HashMap<Integer, Integer>();
	
	//constructor
	public Tree(TreeNode root,int NodeCount)
	{
		this.height=0;
		this.Root = root;
		this.NodeCount = NodeCount;
		rand = new Random();
	}
	
	//function to generate the tree
	public void generateTree(TreeNode root, int ht)
	{
		//base case
		if(root==null) 
		{
			return;
		}
		
		//set the value of root 
		this.setRandomNodeValue(root);
		
		//set its depth
		root.depth = ht;
		
		//put the node in the treeNodes hashMap
		this.treeNodes.put(root.val, 0);
		
		//generate random number of children in the tree i.e from 2 to 5
		this.generateAdjacentNodes(root);
		
		//mark it as visited 
		root.visited=true;
		
		//recurse for its children and create until all nodes are covered
		for(int i=0;root.adjacentNodes!=null&&i<root.adjacentNodes.size();i++) 
		{
			ht++;
			this.generateTree(root.adjacentNodes.get(i),ht);
			ht--;
		}
	}
	
	//special random children number generator which takes care of the stackOverflow Error
	//also keeps in mid the Nodecount and the maximum depth of a node
	public int getRandomArraySize(TreeNode node)
	{
		if(this.NodeCount>5&&node.depth<1000) 
		{
			return rand.nextInt(4)+1;
		}
		else if(this.NodeCount>1&&node.depth<1000)
		{
			return rand.nextInt(this.NodeCount-1)+1;
		}
		else if(this.NodeCount==1&&node.depth<1000)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	//set a random value of a node given such that its unique
	private void setRandomNodeValue(TreeNode node)
	{
		node.val = rand.nextInt(1000000)+1;
		while(treeNodes.containsKey(node.val)==true) {
			node.val = rand.nextInt(1000000)+1;
		}
	}
	
	//generate children of a given node
	private void generateAdjacentNodes(TreeNode node)
	{
		
		//if no nodes are available children cannot be created 
		if(this.NodeCount<=0)return;          //hence return;
		
		//if size of the array by the random size generator is <=0 then return
		int size = getRandomArraySize(node)%6;
		if(size<=0)return;
		
		
		//create a list of nodes whose size is equal to the number of children that are given 
		//by the array size generator
		
		node.adjacentNodes = new LinkedList<TreeNode>();
		
		//increment height
		this.height++;
		
		//now initialize each child node
		for(int i=0;i<size;i++)
		{
			if(this.NodeCount>0) {
			node.adjacentNodes.add(new TreeNode());
			
			//decrement the value of the NodeCount
			this.NodeCount--;
			}
		}
	}
	
	
	//getter method for returning height
	public int getHeight()
	{
		return this.height;
	}
	
	//recursive function for traversing the tree
	public void traverseTree(TreeNode root)
	{
		
		//base case 
		if(root==null)
		{	
			return;
		}
		
		//if root is not null
		if(root!=null)
		{
			
			//check if the root node value is present in tree node and if its visited or not
			if(root!=null&&treeNodes.get(root.val)==0) {
			}
			if(this.values.containsKey(root.val))
			{
				System.out.println("NODE WITH VALUE " +root.val+" FOUND IN THE TREE AT DEPTH: "+root.depth);
			}
			
			//uncomment the below commented portion to see the tree traversal
			//CAUTION: FOR LARGE INPUTS LIKE 10^6 can take 8 to 9 seconds to print the whole tree!!!!
			
			/**
			if(root.adjacentNodes!=null) 
			{
				System.out.println(root.val+" has "+root.adjacentNodes.size()+" Nodes ");
				for(TreeNode n:root.adjacentNodes)
					System.out.println(n.val+" ");
			}
			else
			{
				System.out.println(root.val+" has "+0+" Nodes ");
			}
			*/
		}
		
		//if no children are present return
		if(root.adjacentNodes==null)
		{
			return;
		}
		
		//if size of the children array is 0 return
		if(root.adjacentNodes.size()==0) 
		{
			return;
		}
		
		//else do the work by recursing over each child node
		else
		{
			//check if its not visited and if it has root.val 
			if(treeNodes.containsKey(root.val)&&treeNodes.get(root.val)==0) 
			{root.visited=false;}
			
			//replace the value of root.val in the hashMap
			treeNodes.replace(root.val, 0, 0);
			
			//recurse further
			for(int i=0;i<root.adjacentNodes.size();i++)
			{
				traverseTree(root.adjacentNodes.get(i));
			}
		}
	}
}


///class DFSsearch that extends Recursive action 
//hence this class's object can be invoked in the FORK JOIN POOL

class DFSsearch extends RecursiveAction
{
	
	//threshold value above which tasks cannot be executed using fork JOIN POOL 
	//as tasks will go beyond the efficient execution value
	private int threshold=2;     //experimental value
	
	//root of the tree
	private TreeNode Root;
	
	
	//HashMap treeNodes to store the tree node
	public static HashMap<Integer,Integer> treeNodes;
	
	//nodeValue HashMap  to store the input integers by user
	private static HashMap<Integer,Integer> nodeValues;
	
	
	//constructor 
	public DFSsearch(TreeNode root,HashMap<Integer,Integer> treeNodes,HashMap<Integer,Integer> nodeValues) {
		this.Root = root;
		this.treeNodes = treeNodes; 
		this.nodeValues = nodeValues;
	}
	
	//overridden method in the recursive action class
	@Override
	public void compute() {
		
		//base case
		if(Root==null)
		{	
			return;
		}
		
		//if Root is not null
		if(Root!=null)
		{
			
			//check if the tasks is above the threshold value
			if(Root.depth>threshold) {
				
				//traverse it sequentially
				this.traverseTree(Root);
				return;
			}
			
			//if the nodeValues hashMap has the root.val then print its value and depth 
			if(this.nodeValues.containsKey(Root.val))
			{
				System.out.println("NODE WITH VALUE " +Root.val+" FOUND IN THE TREE AT DEPTH: "+Root.depth);
			}
			
			//uncomment the below part to look at tree traversal
			
			/**
//			if(Root.adjacentNodes!=null) 
//			{
//				System.out.println(Root.val+" has "+Root.adjacentNodes.size()+" Nodes ");
//				for(TreeNode n:Root.adjacentNodes)
//					System.out.println(n.val+" ");
//			}
//			else 
//			{
//				System.out.println(Root.val+" has "+0+" Nodes ");
//			}
 * 
 */
		}
		
		//if root doesn't have any child simply return
		if(Root.adjacentNodes==null||Root.adjacentNodes.size()==0) 
		{
			return;
		}
		
		//else do the work
		else
		{
			//recurse over each child
			for(int i=0;i<Root.adjacentNodes.size();i++)
			{
				//create a task DFSsearch object 
				DFSsearch dfs= new DFSsearch(Root.adjacentNodes.get(i),this.treeNodes,this.nodeValues);
				
				//fork it
				dfs.fork();
				
				//wait till the pool doesn't have any thread idle or has not finished the work
				dfs.helpQuiesce();
			}
		}
	}
	
	//traverse the tree sequentially
	public void traverseTree(TreeNode root)
	{
		//base case
		if(root==null)
		{	
			return;
		}
		
		//if root is not null or is not visited
		if(root!=null&&treeNodes.get(root.val)==0)
		{
			//print the depth and its value if its there in the set of values input
			// by the user
			if(this.nodeValues.containsKey(root.val))
			{
				System.out.println("NODE WITH VALUE " +root.val+" FOUND IN THE TREE AT DEPTH: "+root.depth);
			}
			
			//uncomment the commented part to see the tree traversal
			
			/**
//			if(root.adjacentNodes!=null) 
//			{
//				System.out.println(root.val+" has "+root.adjacentNodes.size()+" Nodes ");
//				for(TreeNode n:root.adjacentNodes)
//					System.out.println(n.val+" ");
//			}
//			else
//			{
//				System.out.println(root.val+" has "+0+" Nodes ");
//			}
 * 
 */
		}
		
		//if no children are present then simply return
		if(root.adjacentNodes==null||root.adjacentNodes.size()==0) 
		{
			return;
		}
		
		//do the work 
		else
		{
			//if the treeNodes contains the key and if is not visited
			if(treeNodes.containsKey(root.val)&&treeNodes.get(root.val)==0) {
				treeNodes.replace(root.val, 0, 1);
				for(int i=0;i<root.adjacentNodes.size();i++)
				{
					traverseTree(root.adjacentNodes.get(i));
				}
			}
		}
	}
}

//class that does explicit multithreading 
//by extending the Thread class

class DFSsearchExplicit extends Thread
{
	
	//root of the tree
	private static TreeNode Root;
	
	//treeNode hashMap
	public static HashMap<Integer,Integer> treeNodes;
	
	//nodevalues input by the user
	private static HashMap<Integer,Integer> nodeValues;
	
	//constructor
	public DFSsearchExplicit(TreeNode root,HashMap<Integer,Integer> treeNodes,HashMap<Integer,Integer> nodeValues) 
	{
		this.Root = root;
		this.treeNodes = treeNodes; 
		this.nodeValues = nodeValues;
	}
	
	//traverse the tree
	public void traverseTree(TreeNode root)
	{
		
		//base case
		if(root==null)
		{	
			return;
		}
		
		//if root is not null and is not visited
		if(root!=null&&treeNodes.get(root.val)==0)
		{
			if(this.nodeValues.containsKey(root.val))
			{
				System.out.println("NODE WITH VALUE " +root.val+" FOUND IN THE TREE AT DEPTH: "+root.depth);
			}
		//uncomment the commented part to see the tree traversal
			
			/**
//			if(root.adjacentNodes!=null) 
//			{
//				System.out.println(root.val+" has "+root.adjacentNodes.size()+" Nodes ");
//				for(TreeNode n:root.adjacentNodes)
//					System.out.println(n.val+" ");
//			}
//			else
//			{
//				System.out.println(root.val+" has "+0+" Nodes ");
//			}
 * 
 */
		}
		
		//if parent does not have any children simply return
		if(root.adjacentNodes==null||root.adjacentNodes.size()==0) 
		{
			return;
		}
		
		//else do the work
		else
		{
			if(treeNodes.containsKey(root.val)&&treeNodes.get(root.val)==0) {
				treeNodes.replace(root.val, 0, 1);
				for(int i=0;i<root.adjacentNodes.size();i++)
				{
					traverseTree(root.adjacentNodes.get(i));
				}
			}
		}
	}
	
	//synchronized overridden method run in thread class
	@Override
	public synchronized void run() 
	{
		//do the sequential traversal
		this.traverseTree(this.Root);
	}
}

//main class
public class Assignment4_2019233{

	//function to sleep the thread and takes care of waiting till
	//all threads have finished their work
	public static void sleep(long time, Thread t)
	{
		
		if(t!=null)
		{
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				Thread.sleep(time+1,(int) ((int)(Math.pow(10, 5))));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//function to calculate the speedup
	public static void calcSpeedUp(double time_seq,double time_par)
	{
		System.out.println("THE SPEED UP IS: ");
		System.out.println(time_seq/time_par);
	}
	
	//function to calculate the Parallel Efficiency
	public static void calcParallelEfficiency(double time_seq,double time_par)
	{
		System.out.println("THE PARALLEL EFFICIENCY IS: ");
		double N_proc = Runtime.getRuntime().availableProcessors();
		System.out.println((time_seq/time_par)/N_proc);
	}
	
	public static void main(String[] args) {
		
		//time variable
		long time=0;
		
		//Scanner for taking input
		Scanner sc = new Scanner(System.in);
		
		
		System.out.println("ENTER THE NUMBER OF NODES IN THE TREE: ");
		//take the number of nodes
		int N = sc.nextInt();
		
		//create a tree root node
		TreeNode node = new TreeNode();
		TreeNode ptr = node;
		
		//create N-1 nodes 
		Tree tr = new Tree(node,N-1);
		
		tr.generateTree(ptr,tr.getHeight());
		
		
		System.out.println("PLEASE ENTER THE NUMBER OF NODES TO SEARCH IN THE TREE");
		
		int numNodes = sc.nextInt();
		
		//user input values
		HashMap<Integer,Integer> NodeValues = new HashMap<Integer, Integer>();
		
		System.out.println("PLEASE ENTER "+ numNodes +" NODE VALUES TO LOOK UP IN THE TREE");
		
		for(int i=0;i<numNodes;i++)
		{
			int value =sc.nextInt();
			NodeValues.put(value,0);
			tr.values.put(value,0);
		}
		
		//calculate sequential traversal time
		long startTime = System.nanoTime();
		
		//sleep
		sleep(time,null);
		//wait 

		//traverse the tree
		tr.traverseTree(node);
		
		//wait 
		//sleep
		sleep(time,null);
		
//		calculate the endtime
		long endTime = System.nanoTime();
		
		
		//calculate the sequential time
		double time_seq = (endTime - startTime-1000)/1000000.0;
		System.out.printf("Took %.6f ms",time_seq);
		System.out.println();
		
		System.out.println("PLEASE SPECIFY THE TECHNIQUE: ");
		
		//taking the technique
		String s = sc.next();
		
		System.out.println("PLEASE ENTER THE NUMBER OF THREADS: ");
		
		//take the number of threads as input
		int numThreads  = sc.nextInt();
		
		
		// ForkJoinPool part
		if(s.equals("ForkJoinPool")) 
		{
			//create a pool
			ForkJoinPool mypool = new ForkJoinPool(numThreads);
			DFSsearch dfs = new DFSsearch(ptr,tr.treeNodes,NodeValues);
			
			//calculate time_parallel
			startTime = System.nanoTime();
			mypool.invoke(dfs);
			endTime = System.nanoTime();
			sleep(time,null);
			
			//time 
			double ForkJoinPoolTime = (endTime - startTime-1000)/1000000.0;
			System.out.printf("Took %.6f ms",ForkJoinPoolTime);
			System.out.println("\n");
			
			//print the results
			calcSpeedUp(time_seq, ForkJoinPoolTime);
			calcParallelEfficiency(time_seq, ForkJoinPoolTime);
		}
		//Explicit threading part
		else
		{
			//create threads and calculate time
			startTime = System.nanoTime();
			for(int i=0;i<numThreads;i++)
			{
				DFSsearchExplicit t = new DFSsearchExplicit(node, tr.treeNodes,NodeValues);
				t.start();
				sleep(time,t);
			}
			endTime = System.nanoTime();
			sleep(time,null);
			
			//time
			double thread_time =(endTime - startTime-1000)/1000000.0;
			System.out.printf("Took %.6f ms",(thread_time));
			System.out.println("\n");
			
			//print the results
			calcSpeedUp(time_seq, thread_time);
			calcParallelEfficiency(time_seq, thread_time);
		}
	}
}
