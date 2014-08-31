/*
 * esv - electric sheep viewer
 * Copyright 2014 MeBigFatGuy.com
 * Copyright 2014 Dave Brosius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.mebigfatguy.esv;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		navPanel.setShowsRootHandles(true);
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
				
		SheepFilter filter = new SheepFilter();
		dir.listFiles(filter);
		
		if (root.getChildCount() == 0) {
			root.add(new LoadingNode());
		}
		
		
		navModel = new DefaultTreeModel(root);
	}

	@Override
	public void newGeneration(String gen, Dimension dim) {
		DefaultMutableTreeNode loading = (DefaultMutableTreeNode) root.getFirstChild();
		if (loading instanceof LoadingNode) {
			root.remove(loading);	
			navModel.nodeStructureChanged(root);
		}
		
		if (root.getChildCount() > 0) {
			
			GenNode node = (GenNode) root.getFirstChild();
			while (node != null) {
				if (node.getGen().equals(gen)) {
					return;
				}
				
				node = (GenNode) node.getNextNode();
			}
		}
		
		GenNode node = new GenNode(gen, dim);
		root.add(node);
		loading = new LoadingNode();
		node.add(loading);
		navModel.nodeStructureChanged(root);
	}
	
	@Override
	public void newSheep(String gen, String id, Dimension dim) {
		DefaultMutableTreeNode loading = (DefaultMutableTreeNode) root.getFirstChild();
		if (loading instanceof LoadingNode) {
			root.remove(loading);	
			navModel.nodeStructureChanged(root);
		}
		
		if (root.getChildCount() > 0) {
			
			GenNode node = (GenNode) root.getFirstChild();
			while (node != null) {
				if (node.getGen().equals(gen)) {
					loading = (DefaultMutableTreeNode) node.getFirstChild();
					if (loading instanceof LoadingNode) {
						node.remove(loading);
					}
					
					SheepNode child = new SheepNode(gen, id, dim);
					node.add(child);
					navModel.nodeStructureChanged(node);
					return;
				}
				
				node = (GenNode) node.getNextNode();
			}
		}
	}
	
	class SheepFilter implements FileFilter {
		private final Pattern genFolderPattern = Pattern.compile("(.*)_(\\d+),(\\d+)");
		private GenNode lastFolder = null;
		
		@Override
		public boolean accept(File pathName) {
			if (pathName.isDirectory()) {
				String name = pathName.getName();
				
				Matcher m = genFolderPattern.matcher(name);
				if (m.matches()) {
					lastFolder = new GenNode(m.group(1), new Dimension(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))));
					root.add(lastFolder);
					
					pathName.listFiles(this);	
					return false;
				}
			}
			
			SheepNode node = new SheepNode(lastFolder.toString(), pathName.getName(), new Dimension(800, 600));
			lastFolder.add(node);
			return false;
		}

	}
}
