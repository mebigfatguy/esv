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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Downloader implements Runnable {

	private static final int BUFFER_SIZE = 102400;
	private URL source;
	private File destination;
	private long vidSize;
	private String generation;
	private String sheepId;
	private Dimension dimension;
	private SheepFirer sheepFirer;
	
	public Downloader(URL src, File dest, long size, String gen, String id, Dimension dim, SheepFirer firer) {
		source = src;
		destination = dest;	
		vidSize = size;
		generation = gen;
		sheepId = id;
		dimension = dim;
		sheepFirer = firer;
	}
	
	@Override
	public void run() {
		try (InputStream vis = new BufferedInputStream(source.openStream());
			 OutputStream vos = new BufferedOutputStream(new FileOutputStream(destination))) {
		    copy(vis, vos, vidSize);
		    sheepFirer.fireNewSheep(generation, sheepId, dimension);
		} catch (IOException ioe) {
			destination.delete();
		}
	}
	
	private static void copy(InputStream is, OutputStream os, long remaining) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];

		while (remaining > 0) {
			int reqLen = (int) Math.min(BUFFER_SIZE, remaining);
			int actLen = is.read(buffer, 0, reqLen);
			remaining -= actLen;
			os.write(buffer, 0, actLen);
		}
	}
}
