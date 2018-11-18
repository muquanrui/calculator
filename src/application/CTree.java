package application;

public class CTree {
	
	private CNode root;

	public CTree(CNode r) {
		root = r;
	}
	
	public CTree() {
		root = null;
	}

	public void setRoot(CNode rt) {
		root = rt;
	}

	public CNode getRoot() {
		return root;
	}

}
