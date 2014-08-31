package com.mebigfatguy.esv;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SheepServerAccessor {

	private static final String SHEEP_URL = "http://community.sheepserver.net/query.php?q=redir&u=%s&p=%s&v=%s&i=%s";
	private static final String LIST_SHEEP_ULR = "%scgi/list?u=%s&v=%s";
	private static final String ACCOUNT = "esv";
	private static final String VERSION = "LNX_2.7b12";
	private static final String GENATT = "gen";
	private static final String SIZEATT = "size";
	private static final String IDATT = "id";
	private static final String URLATT = "url";
	private static final char[] HEX_CONVERT = "0123456789ABCDEF".toCharArray();
	
	private String sheepUrl;
	private String uid;
	private Set<SheepListener> listeners;
	private Thread watchThread;
	
	public SheepServerAccessor() throws IOException {	
		uid = buildUid();
		sheepUrl = getSheepURL();
		getVideoDir().mkdirs();
		listeners = new HashSet<SheepListener>();
	}
	
	public void addSheepListener(SheepListener listener) {
		listeners.add(listener);
	}
	
	public void removeSheepListener(SheepListener listener) {
		listeners.remove(listener);
	}
	
	public static File getVideoDir() {
		return new File(System.getProperty("java.io.tmpdir"), ".esv");
	}
	
	public void watch() {
		watchThread = new Thread(new Runnable() {
			
			@Override()
			public void run() {
				try {
					while (!Thread.interrupted()) {
						try {
							updateNewSheep();
							Thread.sleep(10000);
						} catch (IOException e) {
							Thread.sleep(600000);
						}
					}
				} catch (InterruptedException ie) {
				}
			}
		});
		
		watchThread.setDaemon(true);
		watchThread.start();
	}
	
	private void updateNewSheep() throws IOException {
		try {

			URL u = new URL(String.format(LIST_SHEEP_ULR, sheepUrl, uid, VERSION));
			try (InputStream is = new GZIPInputStream(u.openStream())) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document d = db.parse(is);
				
				XPathFactory xpf = XPathFactory.newInstance();
				XPath xp = xpf.newXPath();
				
				XPathExpression xpe = xp.compile("list");
				Element listEl = (Element) xpe.evaluate(d, XPathConstants.NODE);
				String gen = listEl.getAttribute(GENATT);
				String size = listEl.getAttribute(SIZEATT);
				
				String[] sizes = size.split(" +");
				Dimension dim = new Dimension(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]));
				
				fireGeneration(gen, dim);
				File dir = new File(getVideoDir(), gen + "_" + dim.width + "," + dim.height);
				dir.mkdir();
				
				xpe = xp.compile("/list/sheep");
				NodeList nl = (NodeList) xpe.evaluate(d, XPathConstants.NODESET);
				for (int i = 0; i < nl.getLength(); i++) {
					Element n = (Element) nl.item(i);
					String id = n.getAttribute(IDATT);
					String url = n.getAttribute(URLATT);
					String vidSize = n.getAttribute(SIZEATT);
					File vidFile = new File(dir, id);
					try (InputStream vis = new BufferedInputStream(new URL(url).openStream());
						 OutputStream vos = new BufferedOutputStream(new FileOutputStream(vidFile))) {
					    copy(vis, vos, Long.parseLong(vidSize));
					} catch (IOException ioe) {
						vidFile.delete();
					}
					
					fireNewSheep(gen, id, dim);
				}
			}
		} catch (MalformedURLException | ParserConfigurationException | XPathExpressionException | SAXException e) {
			throw new IOException("Failed fetching sheep", e);
		}
	}
	
	private String getSheepURL() throws IOException {
		try {

			URL u = new URL(String.format(SHEEP_URL, ACCOUNT, buildHash(ACCOUNT, ACCOUNT), VERSION, uid));
			
			try (InputStream is = u.openStream()) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document d = db.parse(is);
				
				XPathFactory xpf = XPathFactory.newInstance();
				XPath xp = xpf.newXPath();
				
				XPathExpression xpe = xp.compile("/query/redir/@host");
				return (String) xpe.evaluate(d, XPathConstants.STRING);
			}

		} catch (NoSuchAlgorithmException | MalformedURLException | SAXException | ParserConfigurationException | XPathExpressionException e) {
			throw new IOException("Failed fetching sheep server", e);
		}
	}
	
	private void fireGeneration(String gen, Dimension dim) {
		for (SheepListener listener : listeners) {
			listener.newGeneration(gen, dim);
		}
	}
	private void fireNewSheep(String gen, String id, Dimension dim) {
		for (SheepListener listener : listeners) {
			listener.newSheep(gen, id, dim);
		}
	}
	
	private static String buildHash(String account, String password) throws NoSuchAlgorithmException {
		MessageDigest m = MessageDigest.getInstance("MD5");
		
		m.update((password + account + "sh33p").getBytes(StandardCharsets.UTF_8));
		byte[] digest = m.digest();
		
		char[] hex = new char[digest.length * 2];
	    for ( int j = 0; j < digest.length; j++ ) {
	        int v = digest[j] & 0xFF;
	        hex[j * 2] = HEX_CONVERT[v >>> 4];
	        hex[j * 2 + 1] = HEX_CONVERT[v & 0x0F];
	    }
	    return new String(hex);
	}
	
	private static String buildUid() {
		Random r = new Random(System.currentTimeMillis());
		long rid = r.nextLong();
		return Long.toHexString(rid);
	}
	
	private static void copy(InputStream is, OutputStream os, long remaining) throws IOException {
		byte[] buffer = new byte[102400];

		while (remaining > 0) {
			int reqLen = (int) Math.min(102400,  remaining);
			int actLen = is.read(buffer, 0, reqLen);
			remaining -= actLen;
			os.write(buffer, 0, actLen);
		}
	}
}
