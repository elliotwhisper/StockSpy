package me.blyf;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class Main {
	private static final int SCREEN_W = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static final int SCREEN_H = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private static SWindow win = null;
	private static boolean show = true;

	public static void main(String[] args) throws Exception {
		String confPath = null;
		if (args.length != 0) {
			confPath = args[0];
		} else {
			confPath = "s.dat";
		}
		File file = new File(confPath);
		if (!file.exists()) {
			throw new Exception("No config file specified.");
		}
		List<String> list = getStockCodes(file);
		win = new SWindow();
		for (String s : list) {
			win.add(s);
		}
		
		win.setBounds((SCREEN_W-300)/2, (SCREEN_H-160)/2, 300, 160);
		win.start();
		JIntellitype.getInstance().registerHotKey(0, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int) 'Q');
		JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int) 'P');
		JIntellitype.getInstance().registerHotKey(2, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int) '9');
		JIntellitype.getInstance().registerHotKey(3, JIntellitype.MOD_CONTROL + JIntellitype.MOD_ALT, (int) '0');
		JIntellitype.getInstance().addHotKeyListener(new HotkeyListener() {
			@Override
			public void onHotKey(int index) {
				if (index == 0) {
					show = !show;
					win.setVisible(show);
				} else if (index == 1) {
					System.exit(0);
				} else if (index == 2) {
					win.setLighter();
				} else if (index == 3) {
					win.setDarker();
				}
			}
		});
		
		win.setVisible(show);
	}

	public static List<String> getStockCodes(File file) throws Exception {
		List<String> list = new ArrayList<String>();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String s = br.readLine();
		while (s != null) {
			if (s.startsWith("sh") || s.startsWith("sz"))
				list.add(s.trim());
			s = br.readLine();
		}
		br.close();
		return list;
	}
}
