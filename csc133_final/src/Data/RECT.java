package Data;

public class RECT {
	//Fields
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private String tag;
	private String hoverLabel;
	private Frame gHover;
	
	//Constructor
	public RECT() {
		x1 = 0;
		x2 = 1;
		y1 = 0;
		y2 = 1;
		tag = "vanillaRECT";
	}
	public RECT(int x1, int y1, int x2, int y2, String tag) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.tag = tag;
		hoverLabel = "";
		gHover = null;
	}
	
	public RECT(int x1, int y1, int x2, int y2, String tag, String hoverLabel) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.tag = tag;
		this.hoverLabel = hoverLabel;
		gHover = null;
	}
	
	public RECT(int x1, int y1, int x2, int y2, String tag, Frame gHover) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.tag = tag;
		hoverLabel = "";
		this.gHover = gHover;
	}
	
	//Methods
	public String getTag() {
		return tag;
	}
	public int getX1() {
		return x1;
	}
	public int getY1() {
		return y1;
	}
	public int getX2() {
		return x2;
	}
	public int getY2() {
		return y2;
	}
	
	public String getHoverLabel() {
		return hoverLabel;
	}
	
	public Frame getGraphicalHover() {
		return gHover;
	}
	
	public boolean isCollision(int x, int y) {
		if (x >= x1 && x <= x2) {
			if (y >= y1 && y <= y2) {
				return true;
			}
		}
		return false;
	}
	public boolean isCollision(RECT r1, RECT r2) {
		int r1x1 = r1.getX1();
		int r1x2 = r1.getX2();
		int r1y1 = r1.getY1();
		int r1y2 = r1.getY2();
		int r2x1 = r2.getX1();
		int r2x2 = r2.getX2();
		int r2y1 = r2.getY1();
		int r2y2 = r2.getY2();		
		if (r1x2 > r2x1 && r1y2 > r2y1)
			if (r1x1 < r2x2 && r1y1 < r2y2)
				return true;
		if (r1x1 < r2x1 && r1y1 > r2y1)
			if (r1x2 > r2x2 && r1y2 < r2y2)
				return true;
		if (r2x2 > r1x1 && r2y2 > r1y1)
			if (r2x1 < r1x2 && r2y1 < r1y2)
				return true;
		if (r2x1 < r1x1 && r2y1 > r1y1)
			if (r2x2 > r1x2 && r2y2 < r1y2)
				return true;
		return false;
	}
	
	public boolean isClicked(Click c, int buttonComparator) {
		if (c.getButton() != buttonComparator)
			return false;  //Not our button
		return isCollision(c.getX(), c.getY());
	}
}