
package main;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.event.MouseEvent;

public class Launcher extends PApplet{

	//TODO
	
	// word, part of speech, definition, frequency, x and y, list of connections
	
	FileManager fM = new FileManager();
	
	
	String fullData;
	ArrayList<Word> words = new ArrayList<Word>();
		// this is all of the word constructs, constructed from the data.
	
	ArrayList<ArrayList<Word>> connections = new ArrayList<ArrayList<Word>>();
		// a list of all pairs of connected words
	
	float width;
	float height;
	float screenx;
	float screeny;
	
	float pastMouseX = -1;
	float pastMouseY = -1;
	
	boolean typing = false;
	String currentTyped = "";
	
	double timeToBlink = 0.5;
	
	double rotateAmount = -0.25;
	boolean highlight = false;
	
	ArrayList<Word> searchMenu = new ArrayList<Word>();
	
	Word hoverWord;
	Word selectedWord;
	
	PFont defaultFont;
	
	ArrayList<Button> buttons = new ArrayList<Button>();
	//JSONObject json = new JSONObject();
	
	public static void main(String[] args) {
		System.out.println("-------------\nINPUT NAME\n-------------\n");
		PApplet.main("main.Launcher");
	}

	public void settings() {
		fullScreen(P2D);
		//size(2000,1400,P2D);
	}

	public void setup() {
		try { getData(); } catch (IOException e) {System.out.println("ASDA");}
		for (Word w : words) {
			w.setRank(getFreqRank(w));
		}
		setConnected();
		trimData();
		width = displayWidth - 400;
		height = displayHeight;
		screenx = 0;
		screeny = 0;
		/*for (String p : PFont.list()) {
			System.out.println(p);
		}*/
		defaultFont = createFont("Arial",400);
		buttons.add(new Button("closeProgram", this, "X", new PVector(displayWidth - 69,19), new PVector(50,50), Color.gray));
		buttons.get(0).enable();
		buttons.add(new Button("rezoom", this, "Reset", new PVector(displayWidth - 130,displayHeight - 60), new PVector(80,30), Color.gray));
		buttons.get(1).enable();
		buttons.add(new Button("highlight", this, "Show Synonyms", new PVector(displayWidth-300,600), new PVector(200,50), Color.gray, true));
		buttons.add(new Button("random", this, "Random Word", new PVector(displayWidth-350,displayHeight-60), new PVector(200,30), Color.gray, true));
		buttons.get(3).enable();
	}

	public void draw() {
		
		if (mousePressed && mouseButton == CENTER) {
			if (pastMouseX == -1) {
				pastMouseX = mouseX;
				pastMouseY = mouseY;
			}
			else if (mouseX != pastMouseX || mouseY != pastMouseY) {
				screenx += mouseX - pastMouseX;
				screeny += mouseY - pastMouseY;
			}
			pastMouseX = mouseX;
			pastMouseY = mouseY;
		}
		else {
			pastMouseX = -1;
			pastMouseY = -1;
		}
		
		textFont(defaultFont);
		textSize(10);
		drawBackground();
		drawEdges();
		drawNodes(false);
		drawNodes(true);
		drawUI();
	}
	public void drawBackground() {
		background(0,0,0);
	}
	public void drawNodes(Boolean big) {
		for (int i = 0; i < words.size(); i++) {
			Word w = words.get(i);	
			
			PVector nodePos = mapToScreen(w.getPos());
			if (mouseX < displayWidth - 400) {
				if (w.getSize() < w.getMaxSize()) {
					if (Math.sqrt(Math.pow(mouseX-nodePos.x, 2)+Math.pow(mouseY-nodePos.y, 2)) <= w.getMinSize()){
						w.grow();
						hoverWord = w;
					}
				}
				if (w.getSize() > w.getMinSize()) {
					if (Math.sqrt(Math.pow(mouseX-nodePos.x, 2)+Math.pow(mouseY-nodePos.y, 2)) > w.getMinSize()) {
						w.shrink();
						hoverWord = null;
					}
				}
			}
			
			if (big == (w.getMinSize() != w.getSize())) {
				double rotate = 0;
				if (w.getSize() > w.getMinSize()) {
					rotate = Math.PI * 2 * (w.getSize()-w.getMinSize())/(w.getMaxSize()-w.getMinSize()) * rotateAmount;
				}
				for (int c = 0; c < w.getPOS().size(); c++) {
					noStroke();
					fill(posColor(w.getPOS().get(c)).getRGB());
					arc(nodePos.x,nodePos.y,w.getSize()*2,w.getSize()*2,(float)(c*2*Math.PI/w.getPOS().size()+Math.PI*2/3+rotate),(float)((c+1)*2*Math.PI/w.getPOS().size()+Math.PI*2/3+rotate),PIE);
					if (selectedWord == w) {
						noFill();
						stroke(posColor(w.getPOS().get(c)).brighter().brighter().brighter().getRGB());
						strokeCap(SQUARE);
						strokeWeight(w.getSize()/3);
						arc(nodePos.x,nodePos.y,w.getSize()*2,w.getSize()*2,(float)(c*2*Math.PI/w.getPOS().size()+Math.PI*2/3+rotate),(float)((c+1)*2*Math.PI/w.getPOS().size()+Math.PI*2/3+rotate),OPEN);
					}
				}
				strokeCap(ROUND);
			}
			
		}
	}
	public void drawEdges() {
		ArrayList<Word> umbrella = new ArrayList<Word>();
		if (selectedWord != null && highlight) {
			for (String s : selectedWord.getConnected()) {
				umbrella.add(getWord(s));
			}
			//System.out.println(umbrella);
		}
		for (ArrayList<Word> c : connections) {
			PVector nodePos = mapToScreen(c.get(0).getPos());
			stroke(255,100);
			strokeWeight(1);
			if (highlight && (c.get(0) == selectedWord || c.get(1) == selectedWord)) {
				stroke(255,0,0,255);
				strokeWeight(3);
			}
			/*if (umbrella.contains(words.get(j))) {
				stroke(255,0,0);
			}*/
			
			PVector nodePos2 = mapToScreen(c.get(1).getPos());
			line(nodePos.x,nodePos.y,nodePos2.x,nodePos2.y);
		}
		
		//make this line over everything except the selected/hovered word
		
	}
	public void drawUI() {
		fill(100,100);
		noStroke();
		rect(displayWidth-370,30,280,60,20,20,0,0);
		rect(displayWidth-370,90,340,173,0,20,20,20);
		fill(100);
		rect(displayWidth-355,45,250,40,10,10,10,10); // search
		
		fill(255);
		textAlign(LEFT,BASELINE);
		textSize(30);
		text(currentTyped,displayWidth-340,75);
		if (typing) {
			if (timeToBlink > 0) {
				stroke(255);
				strokeWeight(2);
				strokeCap(SQUARE);
				line(displayWidth-340 + textWidth(currentTyped),50,displayWidth-340 + textWidth(currentTyped),80);
			}
			timeToBlink -= 1/frameRate;
			if (timeToBlink < -0.5) {
				timeToBlink = 0.5;
			}
		}
		noStroke();
		fill(60);
		rect(displayWidth-350,105,240,33);
		rect(displayWidth-100,105,50,33);
		rect(displayWidth-350,140,240,33);
		rect(displayWidth-100,140,50,33);
		rect(displayWidth-350,175,240,33);
		rect(displayWidth-100,175,50,33);
		rect(displayWidth-350,210,240,33);
		rect(displayWidth-100,210,50,33);
		fill(255);
		textAlign(LEFT,BASELINE);
		textSize(25);
		for (int i = 0; i < searchMenu.size(); i++) {
			text(searchMenu.get(i).getWord(),displayWidth-340,130+i*35);
		}
		textSize(15);
		for (int i = 0; i < searchMenu.size(); i++) {
			text(reduceNumber(searchMenu.get(i).getFreq()),displayWidth-95,130+i*35);
		}
		if (hoverWord != null) {
			wordUI(hoverWord,false);
		}
		else if (selectedWord != null) {
			wordUI(selectedWord,true);
			
		}
		
		drawButtons();
	}
	public void drawButtons() {
		for (Button b : buttons) {
			b.draw();
		}
	}
	public void wordUI(Word w, Boolean detailed) {
		fill(100,100);
		noStroke();
		rect(displayWidth-370,300,340,400,20,20,20,20);
		
		fill(255);
		textAlign(LEFT,CENTER);
		int size = 40;
		textSize(40);
		while (textWidth(w.getWord()) > 300) {
			size--;
			textSize(size);
		}
		text(w.getWord(),displayWidth-340,340);
		
		stroke(255);
		strokeWeight(4);
		if (hoverWord != null) {
			PVector nodePos = mapToScreen(hoverWord.getPos());
			line(nodePos.x,nodePos.y,displayWidth-348,344);
		}
		else if (selectedWord != null) {
			PVector nodePos = mapToScreen(selectedWord.getPos());
			line(nodePos.x,nodePos.y,displayWidth-348,344);
		}
		line(displayWidth-348,344-size/2,displayWidth-348,344+size/2);
		//draw the little bit that should go over this ui
		textAlign(RIGHT,CENTER);
		fill(200);
		textSize(20);
		text("f: "+addCommas(w.getFreq()),displayWidth-60,400);
		text(Math.round((words.size()-w.getRank())*100/words.size())+"% (#"+w.getRank()+")",displayWidth-60,420);
		text("s: "+w.getCount(),displayWidth-60,440);
		textSize(20);
		
		for (int i = 0; i < w.getPOS().size(); i++) {
			fill(posColor(w.getPOS().get(i)).getRGB());
			String c = w.getPOS().get(i).substring(0,1).toUpperCase() + w.getPOS().get(i).substring(1);
			text(c,displayWidth-60,488+20*i);
		}
		stroke(50);
		strokeWeight(4);
		
		line(displayWidth-50,408,displayWidth-50,457);
		line(displayWidth-50,476,displayWidth-50,487+20*w.getPOS().size());
		
		for (Button b : buttons) {
			if (b.showMenu()) {
				b.draw(true);
			}
		}
	}
	public void keyPressed() {
		if (typing) {
			if (Character.isLetter(key)) {
				currentTyped += key;
				timeToBlink = 0.5;
				searchMenu = topNInList(words, currentTyped, 4);
			}
			else if (keyCode == BACKSPACE && currentTyped.length() > 0) {
				currentTyped = currentTyped.substring(0,currentTyped.length()-1);
				timeToBlink = 0.5;
				if (currentTyped.length() > 0) {
					searchMenu = topNInList(words, currentTyped, 4);
				}
				else {
					searchMenu = new ArrayList<Word>();
				}
			}
		}
	}
	public void mousePressed() {
		if (hoverWord != null) {
			if (selectedWord != hoverWord) {
				selectedWord = hoverWord;
				
			}
			else {
				selectedWord = null;
				
			}
		}
		click();
	}
	public ArrayList<Word> umbrellaSearch(Word start, int distance) {
		if (distance == 0) {
			return new ArrayList<Word>();
		}
		ArrayList<Word> list = new ArrayList<Word>();
		for (String s : start.getConnected()) {
			for (Word w : umbrellaSearch(getWord(s),distance - 1)) {
				if (!list.contains(w)) {
					list.add(w);
				}
			}
		}
		return list;
	}
	public void getData() throws IOException {
		fullData = fM.readJSON("data/data");
		//System.out.println(fullData);
		JSONObject full = (JSONObject)JSONValue.parse(fullData);
		//System.out.println(full);
		JSONArray data = (JSONArray)full.get("data");
		for (int i = 0; i < data.size(); i++) {
			JSONObject word = (JSONObject)data.get(i);
			//System.out.println(word);
			ArrayList<String> partsOfSpeech = new ArrayList<String>();
			JSONArray array1 = (JSONArray)word.get("POS");
			for (int a = 0; a < array1.size(); a++) {
				partsOfSpeech.add((String) array1.get(a));
			}
			ArrayList<String> connections = new ArrayList<String>();
			JSONArray array = (JSONArray)word.get("synonyms");
			for (int a = 0; a < array.size(); a++) {
				connections.add((String) array.get(a));
			}
			Word newWord = new Word((String)word.get("word"),partsOfSpeech,((Long)word.get("frequency")).intValue(),new PVector((float)(double)word.get("x"),(float)(double)word.get("y")),connections,data.size());
			words.add(newWord);
		}
	}
	public Color posColor(String POS) {
		switch (POS){
		case "noun":
			return new Color(200,0,0);
		case "adjective":
			return new Color(0,0,200);
		case "verb":
			return new Color(0,200,0);
		case "adverb":
			return new Color(200,150,0);
		}
		return new Color(0);
	}
	public Word getWord(String word) {
		for (Word w : words) {
			if (w.getWord().equals(word)) {
				return w;
			}
		}
		return null;
	}
	public Boolean areConnected(Word a, Word b) {
		if (a.getConnected().contains(b.getWord()) || b.getConnected().contains(a.getWord())) {
			return true;
		}
		return false;
	}
	public void setConnected() {
		for (int i = 0; i < words.size()-1; i++) {
			for (int j = i+1; j < words.size(); j++) {
				if (areConnected(words.get(i),words.get(j))) {
					ArrayList<Word> n = new ArrayList<Word>();
					n.add(words.get(i));
					n.add(words.get(j));
					connections.add(n);
				}
			}
		}
	}
	public PVector mapToScreen(PVector o) {
		//takes a PVector in the range of 0-1 on both axes and transforms it to the proper area.
		return new PVector(o.x*width+screenx,o.y*height+screeny);
	}
	public Button getButton(String id) {
		for (Button b : buttons) {
			if (b.getID().equals(id)) {
				return b;
			}
		}
		return null;
	}
	public void mouseWheel(MouseEvent event) {
		float e = event.getCount();
		if (e > 0) {
			zoom(mouseX, mouseY, -1);
		}
		else if (e < 0) {
			zoom(mouseX, mouseY, 1);
		}
	}
	public void zoom(float x, float y, float amount) {
		//amount = 1 represents a zoom out by 0.1% of screensize
		float widthChange = (float)0.1*amount*width;
		float heightChange = (float)0.1*amount*height;
		screenx -= (x - screenx) * widthChange / width;
		screeny -= (y - screeny) * heightChange / height;
		width += widthChange;
		height += heightChange;
		
		
	}
	public void click() {
		if (mouseX > displayWidth - 355 && mouseX < displayWidth - 105 && mouseY > 45 && mouseY < 95) {
			typing = true;
		}
		else {
			if (typing) {
				typing = false;
			}
		}
		if (mouseX > displayWidth - 350 && mouseX < displayWidth - 110 && mouseY > 105 && mouseY < 138) {
			if (searchMenu.size() > 0) {
				selectedWord = searchMenu.get(0);
			}
		}
		if (mouseX > displayWidth - 350 && mouseX < displayWidth - 110 && mouseY > 140 && mouseY < 173) {
			if (searchMenu.size() > 1) {
				selectedWord = searchMenu.get(1);
			}
		}
		if (mouseX > displayWidth - 350 && mouseX < displayWidth - 110 && mouseY > 175 && mouseY < 208) {
			if (searchMenu.size() > 2) {
				selectedWord = searchMenu.get(2);
			}
		}
		if (mouseX > displayWidth - 350 && mouseX < displayWidth - 110 && mouseY > 210 && mouseY < 243) {
			if (searchMenu.size() > 3) {
				selectedWord = searchMenu.get(3);
			}
		}
		for (Button b : buttons) {
			if (b.click(mouseX,mouseY) || (b == getButton("highlight") && selectedWord != null && b.click(mouseX, mouseY, true))) {
				switch (b.getID()) {
				case "closeProgram":
					exit();
					break;
				case "rezoom":
					width = displayWidth - 400;
					height = displayHeight;
					screenx = 0;
					screeny = 0;
					break;
				case "highlight":
					//System.out.println(highlight);
					if (!highlight) {
						highlight = true;
						getButton("highlight").setText("Hide Synonyms");
					}
					else {
						highlight = false;
						getButton("highlight").setText("Show Synonyms");
					}
					break;
				case "random":
					selectedWord = words.get((int)Math.floor(Math.random()*words.size()));
					break;
				}
			}
		}
	}
	public ArrayList<Word> topNInList(ArrayList<Word> list, String start, int num){
		//returns the top (num) words in the list that start with the string (start)
		ArrayList<Word> fin = new ArrayList<Word>();
		for (Word w : list) {
			if ((w.getWord().length() > start.length() && w.getWord().substring(0,start.length()).equals(start)) || w.getWord().equals(start)) {
				//System.out.println(fin);
				
				Boolean dontAdd = false;
				for (int z = 0; z < fin.size(); z++) {
					
					if (w.getFreq() > fin.get(z).getFreq()) {
						fin.add(z,w);
						dontAdd = true;
						break;
					}
					
				}
				if (!dontAdd) {
					fin.add(w);
				}
			}
		}
		ArrayList<Word> ret = new ArrayList<Word>();
		for (Word w : fin) {
			if (ret.size() < num) {
				ret.add(w);
			}
			else {
				for (int k = 0; k < ret.size(); k++) {
					if (w.getFreq() > ret.get(k).getFreq()) {
						ret.set(k, w);
					}
				}
			}
		}
		//System.out.println(fin);
		return ret;
	}
	public String reduceNumber(int num) {
		if (num < 1000) {
			return String.valueOf(num);
		}
		if (num < 1000000) {
			return String.valueOf(Math.round(num/1000))+"K";
		}
		if (num < 1000000000) {
			return String.valueOf(Math.round(num/1000000))+"M";
		}
		return String.valueOf(Math.round(num/1000000000))+"B";
	}
	public String addCommas(int num) {
		if (num < 1000) {
			return String.valueOf(num);
		}
		if (num < 1000000) {
			return String.valueOf((int)Math.floor(num/1000))+","+String.valueOf(num%1000);
		}
		if (num < 1000000000) {
			return String.valueOf((int)Math.floor(num/1000000))+","+String.valueOf((int)Math.floor(num/1000)%1000)+","+String.valueOf(num%1000);
		}
		return String.valueOf((int)Math.floor(num/1000000000))+","+String.valueOf((int)Math.floor(num/1000000)%1000)+","+String.valueOf((int)Math.floor(num/1000)%1000)+","+String.valueOf(num%1000);
	}
	public int getFreqRank(Word w) {
		int count = 1;
		for (int i = 0; i < words.size(); i++) {
			if (w.getFreq() < words.get(i).getFreq()) {
				count++;
			}
		}
		return count;
	}
	public void trimData() {
		//doesnt actually trim data, just sets the connection number
		for (Word w : words) {
			int c = 0;
			for (Word w2 : words) {
				if (areConnected(w,w2)) {
					c++;
				}
			}
			w.setCount(c);
		}
	}
}