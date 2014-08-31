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
