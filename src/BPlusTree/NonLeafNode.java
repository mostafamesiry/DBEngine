package BPlusTree;

import java.util.ArrayList;

public class NonLeafNode extends Node {
	int minN;
	int minP;
	public NonLeafNode(int n) {
		super(n);
		this.minN=(int)Math.ceil((double)(this.getN()+1)/2);
		this.minP=minN-1;
	}
	public NonLeafNode(int n,ArrayList<Node> pointers){
		super(n, pointers);
		this.minN=(int)Math.ceil((double)(this.getN()+1)/2);
		this.minP=minN-1;
	}
}
