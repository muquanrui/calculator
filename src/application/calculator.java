package application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * 可能出现的错误： 1.输入非法字符 2.括号不闭合 3.连续的操作符 4.
 */

public class calculator implements Initializable {

	@FXML
	private Label textResult;

	@FXML
	private ScrollPane scrollResult;
	
	@FXML
	private Button Num0;
	
	@FXML
	private Button Num1;
	
	@FXML
	private Button Num2;
	
	@FXML
	private Button Num3;
	
	@FXML
	private Button Num4;
	
	@FXML
	private Button Num5;
	
	@FXML
	private Button Num6;
	
	@FXML
	private Button Num7;
	
	@FXML
	private Button Num8;
	
	@FXML
	private Button Num9;
	
	@FXML
	private Button Factorial;
	
	@FXML
	private Button Square;
	
	@FXML
	private Button Root;
	
	@FXML
	private Button ClearError;
	
	@FXML
	private Button Clear;
	
	@FXML
	private Button Divide;
	
	@FXML
	private Button Negative;
	
	@FXML
	private Button Backspace;
	
	@FXML
	private Button Multiply;
	
	@FXML
	private Button Minus;
	
	@FXML
	private Button Plus;
	
	@FXML
	private Button Dot;
	
	@FXML
	private Button Equal;
	
	@FXML
	private Button BracketLeft;
	
	@FXML
	private Button BracketRight;
		
	
	
	private static String preLines;
	private static String curLine;

	/**
	 * 算数表达式计算处理思路： 程序从左到右的扫描表达式，提取出操作数，操作符，以及括号 1.如果是操作数，直接将其压入operandStack栈中
	 * 2.如果是+，-运算符，处理operatorStack栈定中的所有运算符，处理完之后将提取出的运算符压入栈中
	 * 3.如果是*,/运算符，处理栈顶的所有*,/运算符，如果此时的栈顶的运算符是+，-那么直接入栈，处理完之后将提取出的运算符入栈
	 * 4.如果是(,那么直接压入operatorStack栈中 5.如果是),重复处理来自operatorStack栈顶的运算符，知道看到栈顶的(
	 */
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// TODO (don't really need to do anything here).
		String textAll = textResult.getText();
		int currentLine = 0;
		for (int i = 0; i < textAll.length(); i++) {
			if (textAll.charAt(i) == '\n') {
				currentLine = i + 1;
			}
		}
		preLines = textAll.substring(0, currentLine);
		if (currentLine == textAll.length()) {
			curLine = "";
		} else {
			curLine = textAll.substring(currentLine, textAll.length());
		}

		//doTests();
	}
	
	@FXML
	public void pressNum(ActionEvent event) {
		Object btnNum = event.getSource();
		Button btnCur = (Button) btnNum;
		if (curLine.equals("0")) {
			curLine = "";
		}
		curLine += btnCur.getText();
		textResult.setText(preLines + curLine);
	}

	@FXML
	public void pressOperand(ActionEvent event) {
		Object btnNum = event.getSource();
		Button btnCur = (Button) btnNum;
		String operandPress = btnCur.getText();
		
		
		if (operandPress.equals("＋")) {
			curLine += "+";
		} else if (operandPress.equals("－")) {
			curLine += "-";
		} else if (operandPress.equals("×")) {
			curLine += "*";
		} else if (operandPress.equals("÷")) {
			curLine += "/";
		} else {
			curLine += operandPress;
		}

		textResult.setText(preLines + curLine);
	}

	@FXML
	public void pressBackspace(ActionEvent event) {
		curLine = curLine.substring(0, curLine.length() - 1);
		if (curLine.length() == 0) {
			curLine = "0";
		}
		textResult.setText(preLines + curLine);
	}

	@FXML
	public void clearAll(ActionEvent event) {
		preLines = "";
		curLine = "0";
		textResult.setText(preLines + curLine);
	}

	@FXML
	public void clearError(ActionEvent event) {
		curLine = "0";
		textResult.setText(preLines + curLine);
	}

	@FXML
	public void toNegative(ActionEvent event) {
		if (trim(curLine, '0').length() == 0)
			return;
		int lastNum = curLine.length() - 1;
		while (lastNum >= 0 && Character.isDigit(curLine.charAt(lastNum))) {
			lastNum--;
		}
		if(curLine.charAt(lastNum) == '.') {
			lastNum--;
			while (lastNum >= 0 && Character.isDigit(curLine.charAt(lastNum))) {
				lastNum--;
			}
		}
		StringBuilder cl = new StringBuilder(curLine);
		if (lastNum >= 0 && cl.charAt(lastNum) == '-' && (lastNum == 0 || !Character.isDigit(cl.charAt(lastNum - 1)))) {
			cl.deleteCharAt(lastNum);
		} else {
			cl.insert(lastNum + 1, '-');
		}
		curLine = new String(cl);
		textResult.setText(preLines + curLine);
	}

	@FXML
	public void toFact(ActionEvent event) {
		if (curLine.equals("0")) {
			curLine = "";
		}
		curLine += "!";
		textResult.setText(preLines + curLine);
	}

	@FXML
	public void toRoot(ActionEvent event) {
		if (curLine.equals("0")) {
			curLine = "";
		}
		curLine += "√";
		textResult.setText(preLines + curLine);
	}

	@FXML
	public void toSquare(ActionEvent event) {
		if (curLine.equals("0")) {
			curLine = "";
		}
		curLine += "^";
		textResult.setText(preLines + curLine);
	}

	
	
	public void scrollToBottom() {
		scrollResult.setVvalue(1);
	}
	
	public void evaluateExp(ActionEvent event) {
		evaluate result = new evaluate();
		String re= result.evaluateExp(curLine);
		
		if(!re.equals("Error")) {
			preLines += curLine + "\n";
			curLine = re;
			textResult.setText(preLines + curLine);// 运算结果
			draw(result.getTree().getRoot());
		}
		else {
			preLines += curLine + "\n";
			curLine = "0";
			textResult.setText(preLines + "Error");
		}
		scrollToBottom();
		result.printTree();
	}
	
	public void draw(CNode root) {
		try {
            Stage st = new Stage();
            Pane pn = new Pane();
    		CanvasNode rootNode = new CanvasNode(root.getText());
    		double width = getWidth(rootNode, root, 40) + 80;
    		double height = getHeight(rootNode, root, 1);
    		pn.setPrefWidth(width);
    		pn.setPrefHeight(height);
    		rootNode.addIntoPane(pn);
            st.setTitle("画布");
            st.setScene(new Scene(pn, width, height));
            st.setResizable(false);
            st.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		cvNode.setY(depth * 100 - 50);
		return height;
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

	public String trim(String source, char trimChar) {
		int tail = source.length() - 1;

		while (tail > 0 && source.charAt(tail) == '0') {
			source = source.substring(0, source.length() - 1);
			tail--;
		}

		return source;
	}
	
	public void press(KeyEvent event) {
		if(event.isShiftDown()) {
			KeyCode code=event.getCode();
			switch(code) {
			case EQUALS:
				curLine+="+";
				textResult.setText(preLines + curLine);
				Plus.setStyle("-fx-background-color: #F0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT8:
				curLine+="*";
				textResult.setText(preLines + curLine);
				Multiply.setStyle("-fx-background-color: #F0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 

				break;
			case DIGIT9:
				curLine+="(";
				textResult.setText(preLines + curLine);
				BracketLeft.setStyle("-fx-background-color: #A0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 

				break;
			case DIGIT0:
				curLine+=")";
				textResult.setText(preLines + curLine);
				BracketRight.setStyle("-fx-background-color: #A0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			}
		}
		
		else {
			KeyCode code=event.getCode();
			switch(code) {
			
			case DIGIT0:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="0";
				textResult.setText(preLines + curLine);
				Num0.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT1:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="1";
				textResult.setText(preLines + curLine);
				Num1.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT2:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="2";
				textResult.setText(preLines + curLine);
				Num2.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT3:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="3";
				textResult.setText(preLines + curLine);
				Num3.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT4:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="4";
				textResult.setText(preLines + curLine);
				Num4.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT5:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="5";
				textResult.setText(preLines + curLine);
				Num5.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT6:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="6";
				textResult.setText(preLines + curLine);
				Num6.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT7:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="7";
				textResult.setText(preLines + curLine);
				Num7.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT8:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="8";
				textResult.setText(preLines + curLine);
				Num8.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT9:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine+="9";
				textResult.setText(preLines + curLine);
				Num9.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case Q:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine += "!";
				textResult.setText(preLines + curLine);
				Factorial.setStyle("-fx-background-color: #A0A0A0; -fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case A:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine += "√";
				textResult.setText(preLines + curLine);
				Root.setStyle("-fx-background-color: #A0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case Z:
				if (curLine.equals("0")) {
					curLine = "";
				}
				curLine += "^";
				textResult.setText(preLines + curLine);
				Square.setStyle("-fx-background-color: #A0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case W:
				if (trim(curLine, '0').length() == 0)
					return;
				int lastNum = curLine.length() - 1;
				while (lastNum >= 0 && Character.isDigit(curLine.charAt(lastNum))) {
					lastNum--;
				}
				if(curLine.charAt(lastNum) == '.') {
					lastNum--;
					while (lastNum >= 0 && Character.isDigit(curLine.charAt(lastNum))) {
						lastNum--;
					}
				}
				StringBuilder cl = new StringBuilder(curLine);
				if (lastNum >= 0 && cl.charAt(lastNum) == '-' && (lastNum == 0 || !Character.isDigit(cl.charAt(lastNum - 1)))) {
					cl.deleteCharAt(lastNum);
				} else {
					cl.insert(lastNum + 1, '-');
				}
				curLine = new String(cl);
				textResult.setText(preLines + curLine);
				Negative.setStyle("-fx-background-color: #A0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case E:
				curLine = "0";
				textResult.setText(preLines + curLine);
				ClearError.setStyle("-fx-background-color: #A0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case R:
				preLines = "";
				curLine = "0";
				textResult.setText(preLines + curLine);
				Clear.setStyle("-fx-background-color: #D9D9D9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case BACK_SPACE:
				curLine = curLine.substring(0, curLine.length() - 1);
				if (curLine.length() == 0) {
					curLine = "0";
				}
				textResult.setText(preLines + curLine);
				Backspace.setStyle("-fx-background-color: #A0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;			
			case SLASH:
				curLine+="/";
				textResult.setText(preLines + curLine);
				Divide.setStyle("-fx-background-color: #F0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case MINUS:
				curLine+="-";
				textResult.setText(preLines + curLine);
				Minus.setStyle("-fx-background-color: #F0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case PERIOD:
				curLine+=".";
				textResult.setText(preLines + curLine);
				Dot.setStyle("-fx-background-color: #A0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case EQUALS:
				Equal.setStyle("-fx-background-color: #F0A0A0;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				evaluate result = new evaluate();
				String re= result.evaluateExp(curLine);
				
				if(!re.equals("Error")) {
					preLines += curLine + "\n";
					curLine = re;
					textResult.setText(preLines + curLine);// 运算结果
				}
				else {
					preLines += curLine + "\n";
					curLine = "0";
					textResult.setText(preLines + "Error");
				}
				scrollToBottom();
				result.printTree();
				break;
			}
		}
		
	}
	
	public void release(KeyEvent event) {
		if(event.isShiftDown()) {
			KeyCode code=event.getCode();
			switch(code) {
			case EQUALS:
				Plus.setStyle("-fx-background-color: #F08080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT8:
				Multiply.setStyle("-fx-background-color: #F08080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT9:
				BracketLeft.setStyle("-fx-background-color: #808080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			case DIGIT0:
				BracketRight.setStyle("-fx-background-color: #808080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
				break;
			}
		}
		
		KeyCode code=event.getCode();		
		switch(code) {
		case DIGIT0:
			Num0.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT1:
			Num1.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT2:
			Num2.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT3:
			Num3.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT4:
			Num4.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT5:
			Num5.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT6:
			Num6.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT7:
			Num7.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT8:
			Num8.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case DIGIT9:
			Num9.setStyle("-fx-background-color: #A9A9A9;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case Q:
			Factorial.setStyle("-fx-background-color: #808080; -fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case A:
			Root.setStyle("-fx-background-color: #808080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case Z:
			Square.setStyle("-fx-background-color: #808080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case W:
			Negative.setStyle("-fx-background-color: #808080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case E:
			ClearError.setStyle("-fx-background-color: #808080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case R:
			Clear.setStyle("-fx-background-color: #808080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case BACK_SPACE:
			Backspace.setStyle("-fx-background-color: #808080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;			
		case SLASH:
			Divide.setStyle("-fx-background-color: #F08080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case MINUS:
			Minus.setStyle("-fx-background-color: #F08080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case PERIOD:
			Dot.setStyle("-fx-background-color: #F08080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		case EQUALS:
			Equal.setStyle("-fx-background-color: #F08080;-fx-border-style: solid; -fx-border-color: #696969; -fx-border-width: 1px;"); 
			break;
		}
	}
}
