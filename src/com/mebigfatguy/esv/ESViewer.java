package com.mebigfatguy.esv;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ESViewer extends JFrame implements SheepListener {

	private SheepServerAccessor sheepAccessor;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel navModel;
	private JTree navPanel;
	private JPanel videoPanel;
	
	public ESViewer(SheepServerAccessor accessor) {
		
		setTitle("Electric Sheep Viewer");
		sheepAccessor = accessor;
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(4, 4));
		
		buildModel();
		navPanel = new JTree(navModel);
		navPanel.setRootVisible(false);
		navPanel.setPreferredSize(new Dimension(200, 100));
		cp.add(navPanel, BorderLayout.WEST);
		
		videoPanel = new JPanel();
		videoPanel.setPreferredSize(new Dimension(800, 600));
		cp.add(videoPanel, BorderLayout.CENTER);
		
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		accessor.addSheepListener(this);
	}
	
	private void buildModel() {
		root = new DefaultMutableTreeNode();
		File dir = SheepServerAccessor.getVideoDir();
		
		
		dir.listFiles(new FileFilter() {
			
			DefaultMutableTreeNode lastFolder = null;
			
			@Override
			public boolean accept(File pathName) {
				if (pathName.isDirectory()) {
					lastFolder = new DefaultMutableTreeNode();
					lastFolder.setUserObject(pathName.getName());
					root.add(lastFolder);
					return true;
				}
				
				SheepNode node = new SheepNode(lastFolder.toString(), pathName.getName(), new Dimension(800, 600));
				lastFolder.add(node);
				return false;
			}
		});
		
		navModel = new DefaultTreeModel(root);
	}

	@Override
	public void newSheep(String gen, String id, Dimension dim) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getFirstChild();
		while (node != null) {
			if (node.toString().equals(gen)) {
				SheepNode child = new SheepNode(gen, id, dim);
				node.add(child);
				navModel.nodeStructureChanged(node);
				return;
			}
			
			node = node.getNextNode();
		}
		
		node = new DefaultMutableTreeNode(gen);
		root.add(node);
		
		SheepNode child = new SheepNode(gen, id, dim);
		node.add(child);
		navModel.nodeStructureChanged(node);
	}
}
