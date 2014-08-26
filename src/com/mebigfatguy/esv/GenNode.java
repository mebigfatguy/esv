package com.mebigfatguy.esv;

import java.awt.Dimension;

import javax.swing.tree.DefaultMutableTreeNode;

public class GenNode extends DefaultMutableTreeNode {

	private String gen;
	private Dimension dim;
	
	public GenNode(String gen, Dimension dim) {
		this.gen = gen;
		this.dim = dim;
	}

	public String getGen() {
		return gen;
	}

	public Dimension getDim() {
		return dim;
	}
	
	public String toString() {
		return "Generation " + gen;
	}
}
