package me.blyf;

import java.awt.Color;
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
	private SLabel detailLabel;
	public static int color = 205;
	private boolean showDetail = false;

	public SWindow() {
		AWTUtilities.setWindowOpaque(this, false);
		setAlwaysOnTop(true);
		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		add(panel);
		listener = new MMListener(this);
		detailLabel = new SLabel(null);
		detailLabel.addMouseListener(listener);
		detailLabel.addMouseMotionListener(listener);
		detailLabel.setVisible(false);
		panel.add(detailLabel);
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
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"GBK"));
				String s = reader.readLine();
				while (s != null && !"".equals(s.trim())) {
					Entity entity = spliteStockInfo(s);
					if (detailLabel.getEntity() !=null && entity.id.equals(detailLabel.getEntity().id)){
						detailLabel.setEntity(entity);
						detailLabel.setText(entity.toDetail());
					}
					boolean isNew = true;
					for (SLabel lab : labelList) {
						if (lab.getEntity().id.equals(entity.id)) {
							isNew = false;
							lab.setEntity(entity);
							lab.setText(entity.toOverview());
						}
					}
					if (isNew) {
						SLabel label = new SLabel(entity);
						label.setText(entity.toOverview());
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

	public void setDarker() {
		if (color > 0) {
			color = color - 5 < 0 ? 0 : color -5;
			Color c = new Color(color, color, color); 
			for (SLabel label : labelList) {
				label.setForeground(c);
			}
			detailLabel.setForeground(c);
		}
	}
	
	public void setLighter() {
		if (color < 255) {
			color = color + 5 > 255 ? 255 : color + 5;
			Color c = new Color(color, color, color); 
			for (SLabel label : labelList) {
				label.setForeground(c);
			}
			detailLabel.setForeground(c);
		}
	}
	
	public List<SLabel> getLabelList(){
		return labelList;
	}
	
	public void toggle(Entity entity){
		if (showDetail){
			detailLabel.setVisible(false);
			for(SLabel s : labelList){
				s.setVisible(true);
			}
		} else {
			detailLabel.setEntity(entity);
			detailLabel.setText(entity.toDetail());
			detailLabel.setVisible(true);
			for(SLabel s : labelList){
				s.setVisible(false);
			}
		}
		showDetail = !showDetail;
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
			entity.open = n[1].trim();
			entity.close = n[2].trim();
			entity.price = n[3].trim();
			entity.highest = n[4].trim();
			entity.lowest = n[5].trim();
			entity.b1[0] = n[10].trim();
			entity.b1[1] = n[11].trim();
			entity.b2[0] = n[12].trim();
			entity.b2[1] = n[13].trim();
			entity.b3[0] = n[14].trim();
			entity.b3[1] = n[15].trim();
			entity.b4[0] = n[16].trim();
			entity.b4[1] = n[17].trim();
			entity.b5[0] = n[18].trim();
			entity.b5[1] = n[19].trim();
			entity.s1[0] = n[20].trim();
			entity.s1[1] = n[21].trim();
			entity.s2[0] = n[22].trim();
			entity.s2[1] = n[23].trim();
			entity.s3[0] = n[24].trim();
			entity.s3[1] = n[25].trim();
			entity.s4[0] = n[26].trim();
			entity.s4[1] = n[27].trim();
			entity.s5[0] = n[28].trim();
			entity.s5[1] = n[29].trim();
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
	public String open;
	public String close;
	public String price;
	public String highest;
	public String lowest;
	public String[] b1 = new String[]{"",""};
	public String[] b2 = new String[]{"",""};
	public String[] b3 = new String[]{"",""};
	public String[] b4 = new String[]{"",""};
	public String[] b5 = new String[]{"",""};
	public String[] s1 = new String[]{"",""};
	public String[] s2 = new String[]{"",""};
	public String[] s3 = new String[]{"",""};
	public String[] s4 = new String[]{"",""};
	public String[] s5 = new String[]{"",""};
	public String proportion;

	public String toOverview() {
		String overview = "<html>" + String.format("%10s", name) + String.format("%10s", price)
				+ String.format("%10s", proportion) + "%" + "</html>";
		return overview;
	}
	
	public String toDetail(){
		String detail = "<html><head>" + "<style type='text/css'>table{border-collapse:collapse;border-spacing:0;}td{text-align:right;display:block;padding:0 5px 0 0;border:0px;}</style></head><body>"
				+ String.format("%10s", id.substring(2)) + String.format("%10s", name) + String.format("%10s", proportion) + "%" + "<br />" 
				+ String.format("%10s", open) + String.format("%10s", close) + String.format("%10s", highest) + String.format("%10s", lowest) + String.format("%10s", price) + "<br />" 
				+ "<table>"
				+ "<tr>" + "<td>" + (Integer.valueOf(s5[0]) / 100) + "</td>"+ "<td>" + s5[1] + "</td>"+ "<td>" + (Integer.valueOf(b1[0]) / 100) + "</td>"+ "<td>" + b1[1] + "</td>" + "</tr>" 
				+ "<tr>" + "<td>" + (Integer.valueOf(s4[0]) / 100) + "</td>"+ "<td>" + s4[1] + "</td>"+ "<td>" + (Integer.valueOf(b2[0]) / 100) + "</td>"+ "<td>" + b2[1] + "</td>" + "</tr>"
				+ "<tr>" + "<td>" + (Integer.valueOf(s3[0]) / 100) + "</td>"+ "<td>" + s3[1] + "</td>"+ "<td>" + (Integer.valueOf(b3[0]) / 100) + "</td>"+ "<td>" + b3[1] + "</td>" + "</tr>"
				+ "<tr>" + "<td>" + (Integer.valueOf(s2[0]) / 100) + "</td>"+ "<td>" + s2[1] + "</td>"+ "<td>" + (Integer.valueOf(b4[0]) / 100) + "</td>"+ "<td>" + b4[1] + "</td>" + "</tr>"
				+ "<tr>" + "<td>" + (Integer.valueOf(s1[0]) / 100) + "</td>"+ "<td>" + s1[1] + "</td>"+ "<td>" + (Integer.valueOf(b5[0]) / 100) + "</td>"+ "<td>" + b5[1] + "</td>" + "</tr>"
				+ "</table>"
				+ "</body></html>";
//		System.out.println(detail);
		return detail;
	}
}

class SLabel extends JLabel {
	private static final long serialVersionUID = -3274483684332899683L;
	private Entity entity;

	public SLabel(Entity entity) {
		this.setEntity(entity);
		setForeground(new Color(SWindow.color,SWindow.color,SWindow.color));
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
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
		if (e.getButton() == MouseEvent.BUTTON3 && e.getSource() instanceof SLabel){
			SLabel label = (SLabel) e.getSource();
			win.toggle(label.getEntity());
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
