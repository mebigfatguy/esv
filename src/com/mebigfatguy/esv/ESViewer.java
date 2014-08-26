package com.mebigfatguy.esv;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class ESViewer extends JFrame {

	private TreeNode root;
	private DefaultTreeModel navModel;
	private JTree navPanel;
	private JPanel videoPanel;
	
	public ESViewer(SheepServerAccessor accessor) {
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(4, 4));
		
		root = new DefaultMutableTreeNode();
		navModel = new DefaultTreeModel(root);
		navPanel = new JTree(navModel);
		cp.add(navPanel, BorderLayout.WEST);
		
		videoPanel = new JPanel();
		videoPanel.setPreferredSize(new Dimension(800, 600));
		cp.add(videoPanel, BorderLayout.CENTER);
		
		pack();
	}
}
