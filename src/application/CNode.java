package application;

public abstract class CNode {
	private String text;
	public CNode lChild;
	public CNode rChild;
	public boolean isSingle;
	
	public CNode(String txt) {
		text = txt;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean isLeaf() {
		return false;
	}
}
