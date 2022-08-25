package main;

import java.awt.Color;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class Button {

	private String id;
	private PApplet p;
	private String text;
	private PVector pos;
	private PVector size;
	private Color baseColor;
	private Color textColor;
	private PFont font;
	private float textSize;
	private float roundedCorners;
	private float borderWidth;
	private Color borderColor;
	private boolean menuShow;
	private Boolean enabled = false;
	
	public Button(String id, PApplet p, String text, PVector pos, PVector size, Color baseColor, Color textColor, PFont font, float textSize, float roundedCorners, float borderWidth, Color borderColor) {
		this.id = id;
		this.p = p;
		this.text = text;
		this.pos = pos;
		this.size = size;
		this.baseColor = baseColor;
		this.textColor = textColor;
		this.font = font;
		this.textSize = textSize;
		this.roundedCorners = roundedCorners;
		this.borderWidth = borderWidth;
		this.borderColor = borderColor;
	}
	public Button(String id, PApplet p, String text, PVector pos, PVector size, Color baseColor) {
		this.id = id;
		this.p = p;
		this.text = text;
		this.pos = pos;
		this.size = size;
		this.baseColor = baseColor;
		textColor = Color.black;
		//font = PFont.findFont("Arial");
		font = p.createFont("Arial",100);
		textSize = getFontSize(text);
		roundedCorners = 0;
		borderWidth = 0;
		borderColor = Color.black;
	}
	public Button(String id, PApplet p, String text, PVector pos, PVector size, Color baseColor, Boolean menu) {
		this.id = id;
		this.p = p;
		this.text = text;
		this.pos = pos;
		this.size = size;
		this.baseColor = baseColor;
		textColor = Color.black;
		//font = PFont.findFont("Arial");
		font = p.createFont("Arial",100);
		textSize = getFontSize(text);
		roundedCorners = 0;
		borderWidth = 0;
		borderColor = Color.black;
		this.menuShow = menu;
	}
	public Boolean showMenu() {
		return menuShow;
	}
	public String getID() {
		return id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void enable() {
		enabled = true;
	}
	public void disable() {
		enabled = false;
	}
	public void draw() {
		if (!enabled) {
			return;
		}
		p.noStroke();
		p.fill(baseColor.getRGB());
		p.rect(pos.x,pos.y,size.x,size.y);
		p.fill(textColor.getRGB());
		p.textFont(font);
		p.fill(textColor.getRGB());
		p.textSize(textSize);
		p.textAlign(PApplet.CENTER,PApplet.CENTER);
		p.text(text,pos.x+size.x/2,pos.y+(float)(size.y/(2.5)));
	}
	public void draw(Boolean b) {
		if (b) {
			p.noStroke();
			p.fill(baseColor.getRGB());
			p.rect(pos.x,pos.y,size.x,size.y);
			p.fill(textColor.getRGB());
			p.textFont(font);
			p.fill(textColor.getRGB());
			p.textSize(textSize);
			p.textAlign(PApplet.CENTER,PApplet.CENTER);
			p.text(text,pos.x+size.x/2,pos.y+(float)(size.y/(2.5)));
		}
	}
	public Boolean click(float mx, float my) {
		if (!enabled) {
			return false;
		}
		if (mx > pos.x && mx < pos.x + size.x && my > pos.y && my < pos.y + size.y) {
			return true;
		}
		return false;
	}
	public Boolean click(float mx, float my, Boolean override) {
		if (override) {
			if (mx > pos.x && mx < pos.x + size.x && my > pos.y && my < pos.y + size.y) {
				return true;
			}
			
		}
		return false;
	}
	public int getFontSize(String text) {
		int s = 10;
		p.textFont(font);
		p.textSize(s);
		do {
			s++;
			p.textSize(s);
		}
		while (p.textWidth(text) < 0.8 * size.x && s < size.y);
		return s;
	}
}
