package BPlusTree;

import java.io.Serializable;
import java.util.ArrayList;

public class Node implements Serializable{
	int n;
	ArrayList<Node> pointers;
	ArrayList<Integer> values;

	public Node(int n,ArrayList<Node> pointers){
		this.n=n;
		this.pointers=pointers;
		values=new ArrayList<Integer>();
	}

	public Node(int n){
		this.n=n;
		pointers=new ArrayList<Node>();
		values=new ArrayList<Integer>();
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public ArrayList<Node> getPointers() {
		return pointers;
	}

	public void setPointers(ArrayList<Node> pointers) {
		this.pointers = pointers;
	}

	public ArrayList<Integer> getValues() {
		return values;
	}

	public void setValues(ArrayList<Integer> values) {
		this.values = values;
	}

	public static void printRow(Node n,int x)
	{
		printHelper(0,x,n);
	}


	public static void printHelper(int i,int x, Node n2) {
		if(i==x)
		{
			for(int j=0;j<n2.getValues().size();j++){
				System.out.print(n2.getValues().get(j));
				if(j!=n2.getValues().size()-1){
					System.out.print(" , ");
				}
			}
		}
		else
		{
			for(int j=0;j<n2.getPointers().size();j++)
			{
				System.out.print("( ");
				printHelper(i+1,x,n2.getPointers().get(j));
				System.out.print(" )");
				
				
			}
			
		}
		
	}

	public String toString()
	{
		String s ="";
		for(int i=0;i<this.getValues().size();i++)
		{
			s+=this.getValues().get(i)+" ";
		}
		s+='\n';
		for(int i=0;i<this.getPointers().size();i++)
		{
			if(this.getPointers().get(0) instanceof LeafNode){
				for(int j=0;j<this.getValues().size();j++){
					s+=this.getPointers().get(i).getValues().get(j)+" ";
				}

			}
			else{
				s+=this.getPointers().get(i).toString()+" ";
			}

		}
		return s;



	}
	public void printTree(int height)
	{
	
		for(int i=0;i<height;i++)
		{
			printRow(this, i);
			System.out.println("");
			System.out.println("");
		}
	}


}
