package main;

import java.util.ArrayList;

import processing.core.PVector;

public class Word {

	private String word;
	private ArrayList<String> partOfSpeech;
	private Integer frequency;
	private PVector position;
	private ArrayList<String> connectedWords;
	private float currentSize;
	private float maxSize;
	private float growSize;
	private int rank;
	private int total;
	private int cCount;
	
	public Word(String word, ArrayList<String> partOfSpeech, Integer frequency, PVector position, ArrayList<String> connectedWords, int total) {
		this.word = word;
		this.partOfSpeech = partOfSpeech;
		this.frequency = frequency;
		this.position = position;
		this.total = total;
		this.connectedWords = connectedWords;
		this.currentSize = getDefaultSize();
		this.maxSize = currentSize * 3;
		this.growSize = currentSize / 3;
	}
	public String getWord() {
		return word;
	}
	public ArrayList<String> getPOS() {
		return partOfSpeech;
	}
	public Integer getFreq() {
		return frequency;
	}
	public PVector getPos() {
		return position;
	}
	public ArrayList<String> getConnected() {
		return connectedWords;
	}
	public float getSize() {
		return currentSize;
	}
	public float getMaxSize() {
		return maxSize;
	}
	public float getMinSize() {
		return getDefaultSize();
	}
	public void grow() {
		currentSize += growSize;
	}
	public void shrink() {
		currentSize -= growSize;
	}
	public float getDefaultSize() {
		return 5;
	}
	public void setRank(int r) {
		rank = r;
		
	}
	public int getRank() {
		return rank;
	}
	public void setConnected(ArrayList<String> c) {
		connectedWords = c;
	}
	public int getCount() {
		return cCount;
	}
	public void setCount(int c) {
		cCount = c;
	}
}
