/*
 * esv - electric sheep viewer
 * Copyright 2014-2016 MeBigFatGuy.com
 * Copyright 2014-2016 Dave Brosius
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

import java.awt.Dimension;
import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class SheepNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -8634910107701195574L;

	private String gen;
	private String id;
	private Dimension dim;
	
	public SheepNode(String gen, String id, Dimension dim) {
		this.gen = gen;
		this.id = id;
		this.dim = dim;
	}

	public String getRelativePath() {
		File dir = new File(gen + "_" + dim.width + "," + dim.height);
		return new File(dir, id).toString();
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
	
	@Override
	public String toString() {
		return "Sheep " + id;
	}
}
