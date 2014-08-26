package com.mebigfatguy.esv;

import java.awt.Dimension;

import javax.swing.tree.DefaultMutableTreeNode;

public class SheepNode extends DefaultMutableTreeNode {

	private String gen;
	private String id;
	private Dimension dim;
	
	public SheepNode(String gen, String id, Dimension dim) {
		this.gen = gen;
		this.id = id;
		this.dim = dim;
	}

	public String getGen() {
		return gen;
	}

	public String getId() {
		return id;
	}

	public Dimension getDim() {
		return dim;
	}
	
	public String toString() {
		return "Generation " + gen;
	}
}
