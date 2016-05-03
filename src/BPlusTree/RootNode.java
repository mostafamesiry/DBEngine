package BPlusTree;

import java.util.ArrayList;

public class RootNode extends Node{
	int minN;
	int minP;
	public RootNode(int n) {
		super(n);
		this.minN=1;
		this.minP=2;
	}
	public RootNode(int n,ArrayList<Node> pointers){
		super(n, pointers);
		this.minN=1;
		this.minP=2;
	}
	public RootNode(int n,ArrayList<Node> pointers,ArrayList<Node> values){
		super(n, pointers);
		this.minN=1;
		this.minP=2;
	}

}
