package BPlusTree;

import java.io.Serializable;
import java.util.ArrayList;

import WreckingBall.Triplet;

public class BPlusTree implements Serializable{
	Node root;
	int n;
	public BPlusTree(int n){
		root=new LeafNode(n);
		this.n=n;
	}
	public void insertBPlusTree(int x){
		Node root=insertHelper(this.root,x);
		if(root !=null){
			this.root=root;
			for(int k=1;k<this.root.getPointers().size();k++)
			{
				this.root.getValues().add(getMin(this.root.getPointers().get(k)));
			}
		}
	}
	public static Node insertHelper(Node theNode,int number) {
		if(! (theNode instanceof LeafNode)){
			int i=0;
			for(i=0;i<theNode.getValues().size();i++){

				if(theNode.getValues().get(i)>number){
					break;
				}
				if(i==theNode.getValues().size()-1){
					i++;
					break;
				}
			}
			Node returned=insertHelper(theNode.getPointers().get(i),number);
			if(returned==null){
				return null;
			}
			else{

				Node n1=returned.getPointers().get(0);
				Node n2=returned.getPointers().get(1);
				theNode.getPointers().remove(theNode.getPointers().get(i));


				if(theNode.getValues().size()<=theNode.n-1){
					theNode.getPointers().add(i,n1);
					theNode.getPointers().add(i+1,n2);

					theNode.getValues().clear();
					for(int k=1;k<theNode.getPointers().size();k++)
					{
						theNode.getValues().add(getMin(theNode.getPointers().get(k)));
					}

					return null;
				}


				else{
					theNode.getPointers().add(i,n1);
					theNode.getPointers().add(i+1,n2);
					Node nonLeaf1 = new NonLeafNode(theNode.n);
					Node nonLeaf2 = new NonLeafNode(theNode.n);
					int half = (theNode.getPointers().size()+1)/2;




					int j=0;
					for(j=0;j<half;j++){
						nonLeaf1.getPointers().add(theNode.getPointers().get(j));
					}
					for(j=half;j<theNode.getPointers().size();j++)
					{
						nonLeaf2.getPointers().add(theNode.getPointers().get(j));
					}

					for(int k=1;k<nonLeaf1.getPointers().size();k++)
					{
						nonLeaf1.getValues().add(getMin(nonLeaf1.getPointers().get(k)));
					}



					for(int k=1;k<nonLeaf2.getPointers().size();k++)
					{
						nonLeaf2.getValues().add(getMin(nonLeaf2.getPointers().get(k)));
					}




					Node newRoot =new Node(theNode.n);
					newRoot.getPointers().add(nonLeaf1);
					newRoot.getPointers().add(nonLeaf2);

					return newRoot;





				}
			}
		}
		else{// if it is instance of Leaf Node
			if(theNode.getValues().size()>=theNode.n)
			{
				LeafNode n1 = new LeafNode(theNode.n);
				LeafNode n2 = new LeafNode(theNode.n);
				int half = (theNode.getValues().size()+1)/2;
				int i=0;
				ArrayList<Triplet> pointers = ((LeafNode)theNode).dataPointer;


				for(i=0;i<half;i++){
					n1.getValues().add(theNode.getValues().get(i));
					n1.dataPointer.add(pointers.get(i));
					//n1.dataPointer.add(pointers.get((i)+1));

				}
				for(i=half;i<theNode.getValues().size();i++)
				{
					n2.getValues().add(theNode.getValues().get(i));
					n2.dataPointer.add(pointers.get(i));
					//n2.dataPointer.add(pointers.get((i)+1));
				}



				n2.getValues().add(number);
				n2.getValues().sort(null);
				n1.getValues().sort(null);


				Node newRoot =new Node(theNode.n);
				newRoot.getPointers().add(n1);
				newRoot.getPointers().add(n2);
				return newRoot;
			}
			else
			{
				theNode.getValues().add(number);
				theNode.getValues().sort(null);
				return null;
			}
		}

	}
	public void deleteBPlusTree(int x){
		LeafNode node=this.searchBPlusTree(x,false);
		if(node!=null){
			int i;
			for(i = 0;i<node.getValues().size();i++)
			{
				if(node.getValues().get(i)==x)
					break;
			}
			node.getDataPointer().get(i).setDeleted(true);
		}


	}


	public LeafNode searchBPlusTree(int x,boolean insert){
		Node current=root;
		int i;
		while(! (current instanceof LeafNode)){
			for(i=0;i<current.getValues().size();i++){
				if(current.getValues().get(i)>new Integer(x)){
					break;
				}
			}
			current=current.getPointers().get(i);
		}
		if (current.getValues().contains(new Integer(x))){
			int index = current.getValues().indexOf(new Integer(x));
			if(insert){
				return (LeafNode)current;
			}
			else{
				if(!((LeafNode)current).getDataPointer().get(index).isDeleted()){
					return (LeafNode)current;
				}
				else
				{
					return null;
				}
			}
		}
		else
			return null;


	}

	public static int getMin(Node n)
	{
		if(n instanceof LeafNode)
		{
			return n.getValues().get(0);
		}
		Node current = n;
		while(!(current instanceof LeafNode)){
			if(current.getPointers().get(0) instanceof LeafNode)
			{
				return current.getPointers().get(0).getValues().get(0);
			}
			else
			{
				current = current.getPointers().get(0);
			}
		}
		return 0;
	}

	public static void main(String[] args) {
		BPlusTree x=new BPlusTree(2);
		//		x.root.getValues().add(5);
		//		x.root.getPointers().add(new NonLeafNode(2));
		//		x.root.getPointers().add(new NonLeafNode(2));
		//		
		//		x.root.getPointers().get(0).getValues().add(3);
		//		x.root.getPointers().get(0).getPointers().add(new LeafNode(2));
		//		x.root.getPointers().get(0).getPointers().add(new LeafNode(2));
		//		x.root.getPointers().get(0).getPointers().get(0).getValues().add(2);
		//		x.root.getPointers().get(0).getPointers().get(1).getValues().add(3);
		//		x.root.getPointers().get(0).getPointers().get(1).getValues().add(4);
		//		
		//		x.root.getPointers().get(1).getValues().add(8);
		//		x.root.getPointers().get(1).getPointers().add(new LeafNode(2));
		//		x.root.getPointers().get(1).getPointers().add(new LeafNode(2));
		//		x.root.getPointers().get(1).getPointers().get(0).getValues().add(5);
		//		x.root.getPointers().get(1).getPointers().get(1).getValues().add(8);
		//		x.root.getPointers().get(1).getPointers().get(1).getValues().add(9);

		x.insertBPlusTree(4);
		x.insertBPlusTree(4);
		x.insertBPlusTree(4);
		x.insertBPlusTree(4);
		//		x.insertBPlusTree(4);
		//		x.insertBPlusTree(9);
		//		x.insertBPlusTree(1);
		//		x.insertBPlusTree(11);
		//		x.insertBPlusTree(12);
		//		x.insertBPlusTree(13);
		//		x.insertBPlusTree(14);
		//		x.insertBPlusTree(7);
		//		x.insertBPlusTree(6);
		//System.out.println(getMin(x.root.getPointers().get(1)));
		//System.out.println(x.root.getPointers().get(1).getValues().size());
		x.root.printTree(2);
	}
	public Node getRoot() {
		return root;
	}
	public int getN() {
		return n;
	}
}
