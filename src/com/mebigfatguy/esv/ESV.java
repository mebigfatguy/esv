package com.mebigfatguy.esv;

import javax.swing.JOptionPane;

public class ESV {

	public static void main(String[] args) {
		try {
			SheepServerAccessor accessor = new SheepServerAccessor();
			ESViewer viewer = new ESViewer(accessor);
			viewer.setLocationRelativeTo(null);
			viewer.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
