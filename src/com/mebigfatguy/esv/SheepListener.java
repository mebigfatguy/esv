package com.mebigfatguy.esv;

import java.awt.Dimension;

public interface SheepListener {
	void newGeneration(String gen, Dimension dim);
	void newSheep(String gen, String id, Dimension dim);
}
