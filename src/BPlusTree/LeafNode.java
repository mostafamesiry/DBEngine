package BPlusTree;

import java.util.ArrayList;

import WreckingBall.Triplet;

public class LeafNode extends Node {
	int minN;
	int minP;
	ArrayList<Triplet> dataPointer;
	public LeafNode(int n) {
		super(n);
		this.minN=(this.getN()+1)/2;
		this.minP=minN;
		dataPointer=new ArrayList<Triplet>();
	}
	public LeafNode(int n,ArrayList<Node> pointers){
		super(n, pointers);
		this.minN=(this.getN()+1)/2;
		this.minP=minN;
	}
	public int getMinN() {
		return minN;
	}
	public int getMinP() {
		return minP;
	}
	public ArrayList<Triplet> getDataPointer() {
		return dataPointer;
	}
	

}
