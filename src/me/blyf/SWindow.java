package me.blyf;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import com.sun.awt.AWTUtilities;

public class SWindow extends JWindow {
	private static final long serialVersionUID = -7214337431381133330L;
	private static final DecimalFormat DF = new DecimalFormat("0.00");
	private JPanel panel;
	private MMListener listener;
	private List<String> codes;
	private List<SLabel> labelList = new ArrayList<SLabel>();
	private int color = 185;

	public SWindow() {
		AWTUtilities.setWindowOpaque(this, false);
		setAlwaysOnTop(true);
		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		add(panel);
		listener = new MMListener(this);
		codes = new ArrayList<String>();
	}

	public void start() {
		Timer timer = new Timer();
		timer.schedule(new STimerTask(this, codes), 0, 3000);
	}

	public void update() {
		try {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < codes.size(); i++) {
				sb.append(codes.get(i)).append(",");
			}
			if (codes.size() > 0) {
				URL url = new URL("http://hq.sinajs.cn/list=" + sb.toString());
				URLConnection connection = url.openConnection();
				InputStream is = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String s = reader.readLine();
				while (s != null && !"".equals(s.trim())) {
					Entity entity = spliteStockInfo(s);
					boolean isNew = true;
					for (Component c : panel.getComponents()) {
						if (c instanceof SLabel && ((SLabel) c).getCode().equals(entity.id)) {
							isNew = false;
							((SLabel) c).setText(entity.toString());
						}
					}
					if (isNew) {
						SLabel label = new SLabel(entity.id);
						label.setText(entity.toString());
						label.addMouseListener(listener);
						label.addMouseMotionListener(listener);
						label.setFocusable(true);
						panel.add(label);
						labelList.add(label);
					}
					this.validate();
					s = reader.readLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void add(String code) {
		codes.add(code);
	}

	public void delete(SLabel label) {
		panel.remove(label);
		for (String s : codes) {
			if (s.equals(label.getCode())) {
				codes.remove(s);
				break;
			}
		}
	}
	
	public void setDarker() {
		if (color > 0) {
			color = color - 5 < 0 ? 0 : color -5;
			for (SLabel label : labelList) {
				label.setForeground(new Color(color, color, color));
			}
		}
	}
	
	public void setLighter() {
		if (color < 255) {
			color = color + 5 > 255 ? 255 : color + 5;
			for (SLabel label : labelList) {
				label.setForeground(new Color(color, color, color));
			}
		}
	}
	
	public List<SLabel> getLabelList(){
		return labelList;
	}
	
	public void toggle(SLabel label){
		
	}

	private Entity spliteStockInfo(String info) {
		String[] infos = info.split("=");
		Entity entity = new Entity();
		if (infos.length == 2) {
			// var hq_str_sh600718
			String id = infos[0].substring(11);
			String[] n = infos[1].split(",");
			double proportion = (Double.valueOf(n[3]) - Double.valueOf(n[2]))
					/ Double.valueOf(n[3]) * 100;
			entity.id = id.trim();
			entity.name = n[0].substring(1).trim();
			entity.start = n[1].trim();
			entity.price = n[3].trim();
			entity.proportion = DF.format(proportion);
		}
		return entity;
	}
}

class STimerTask extends TimerTask {
	private SWindow win;

	public STimerTask(SWindow dialog, List<String> codes) {
		this.win = dialog;
	}

	@Override
	public void run() {
		win.update();
	}
}

class Entity {
	public String id;
	public String name;
	public String start;
	public String price;
	public String proportion;

	@Override
	public String toString() {
		return String.format("%8s", id) + String.format("%6s", name) + String.format("%6s", start)
				+ String.format("%6s", price)
				+ String.format("%6s", proportion);
	}
}

class SLabel extends JLabel {
	private static final long serialVersionUID = -3274483684332899683L;
	private String code;

	public SLabel(String code) {
		this.code = code;
		setForeground(new Color(185,185,185));
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}

class MMListener implements MouseMotionListener, MouseListener {
	private SWindow win;
	private int startx;
	private int starty;

	public MMListener(SWindow win) {
		this.win = win;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int endx = e.getXOnScreen();
		int endy = e.getYOnScreen();
		int x = endx - startx + win.getX();
		int y = endy - starty + win.getY();
		win.setLocation(x, y);
		startx = endx;
		starty = endy;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		startx = e.getXOnScreen();
		starty = e.getYOnScreen();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON2 && e.getSource() instanceof SLabel){
			SLabel label = (SLabel) e.getSource();
			win.toggle(label);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
