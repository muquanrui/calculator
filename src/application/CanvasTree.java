package application;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CanvasTree {
	
	private CanvasNode root;
	private double paddingLR;
	private double paddingTB;
	private double canvasWidth;
	private double canvasHeight;
	
	public CanvasTree(CTree cr) {
		root = new CanvasNode(cr.getRoot().getText());
		paddingLR = 40;
		paddingTB = 0;
		canvasWidth = getWidth(root, cr.getRoot(), paddingLR) + paddingLR * 2;
		canvasHeight = getHeight(root, cr.getRoot(), 1) + paddingTB * 2;
	}
	
	public CanvasTree(CTree cr, double lr, double tb) {
		root = new CanvasNode(cr.getRoot().getText());
		paddingLR = lr;
		paddingTB = tb;
		canvasWidth = getWidth(root, cr.getRoot(), paddingLR) + paddingLR * 2;
		canvasHeight = getHeight(root, cr.getRoot(), 1) + paddingTB * 2;
	}
	
	private double getWidth(CanvasNode cvNode, CNode node, double start) {
		double lWidth = 0.0, rWidth = 0.0;
		if(node.lChild != null) {
			CanvasNode lNode = new CanvasNode(node.lChild.getText());
			cvNode.setLeftNode(lNode);
			lWidth = getWidth(lNode, node.lChild, start);
		}
		if(node.rChild != null) {
			CanvasNode rNode = new CanvasNode(node.rChild.getText());
			cvNode.setRightNode(rNode);
			rWidth = getWidth(rNode, node.rChild, start + lWidth);
		}
		double width = lWidth + rWidth >= 100 ? lWidth + rWidth : 100;
		double x = 0.0;
		if(node.isLeaf()) x = start + 50;
		else {
			x += cvNode.getLeftNode().getX() / 2;
			if(node.rChild != null) {
				x += cvNode.getRightNode().getX() / 2;
			}else {
				x += cvNode.getLeftNode().getX() / 2;
			}
		}
		cvNode.setX(x);
		return width;
	}
	
	private double getHeight(CanvasNode cvNode, CNode node, int depth) {
		double lHeight = 0.0, rHeight = 0.0;
		if(node.lChild != null) {
			lHeight = getHeight(cvNode.getLeftNode(), node.lChild, depth + 1);
		}else {
			lHeight = depth * 100;
		}
		if(node.rChild != null) {
			rHeight = getHeight(cvNode.getRightNode(), node.rChild, depth + 1);
		}else {
			rHeight = depth * 100;
		}
		double height = lHeight >= rHeight ? lHeight : rHeight;
		cvNode.setY(paddingTB + depth * 100 - 50);
		return height;
	}
	
	public void draw() {
		try {
            Stage st = new Stage();
            Pane pn = new Pane();
    		pn.setPrefWidth(canvasWidth);
    		pn.setPrefHeight(canvasHeight);
    		root.addIntoPane(pn);
            st.setTitle("»­²¼");
            st.setScene(new Scene(pn, canvasWidth, canvasHeight));
            st.setResizable(false);
            st.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private class CanvasNode{
		private Circle cr;
		private Text tx;
		private Line lLine;
		private Line rLine;
		private CanvasNode lNode;
		private CanvasNode rNode;
		
		public CanvasNode(String txt) {
			cr = new Circle(40);
			cr.setFill(Color.LIGHTBLUE);
			tx = new Text(txt);
			tx.setFill(Color.RED);
			tx.setFont(Font.font(20));
			lLine = null;
			rLine = null;
		}
		
		public void setLeftNode(CanvasNode left) {
			lNode = left;
		}
		
		public void setRightNode(CanvasNode right) {
			if(right != null) {
				rNode = right;
			}
		}
		
		public CanvasNode getLeftNode() {
			return lNode;
		}
		
		public CanvasNode getRightNode() {
			return rNode;
		}
		
		public void setX(double x) {
			cr.setCenterX(x);
			tx.setX(x - tx.getLayoutBounds().getWidth() / 2);
		}
		
		public void setY(double y) {
			cr.setCenterY(y);
			tx.setY(y + tx.getLayoutBounds().getHeight() / 4);
		}
		
		public double getX() {
			return cr.getCenterX();
		}
		
		public double getY() {
			return cr.getCenterY();
		}
		
		public String getText() {
			return tx.getText();
		}
		
		public void addIntoPane(Pane pn) {
			if(lNode != null) {
				System.out.println(tx.getText() + " - left : " + lNode.getText());
				lLine = new Line(getX(), getY(), lNode.getX(), lNode.getY());
				lLine.setStroke(Color.LIGHTSALMON);
				lLine.setStrokeWidth(3);
				pn.getChildren().add(lLine);
				lNode.addIntoPane(pn);
			}
			if(rNode != null) {
				System.out.println(tx.getText() + " - right : " + rNode.getText());
				rLine = new Line(getX(), getY(), rNode.getX(), rNode.getY());
				rLine.setStroke(Color.LIGHTSALMON);
				rLine.setStrokeWidth(3);
				pn.getChildren().add(rLine);
				rNode.addIntoPane(pn);
			}
			pn.getChildren().add(cr);
			pn.getChildren().add(tx);
		}
		
	}
}
