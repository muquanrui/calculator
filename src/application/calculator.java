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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

/**
 * 可能出现的错误： 1.输入非法字符 2.括号不闭合 3.连续的操作符 4.
 */

public class calculator implements Initializable {

	@FXML
	private Label textResult;

	@FXML
	private ScrollPane scrollResult;

	
	private static String preLines;
	private static String curLine;

	/**
	 * 算数表达式计算处理思路： 程序从左到右的扫描表达式，提取出操作数，操作符，以及括号 1.如果是操作数，直接将其压入operandStack栈中
	 * 2.如果是+，-运算符，处理operatorStack栈定中的所有运算符，处理完之后将提取出的运算符压入栈中
	 * 3.如果是*,/运算符，处理栈顶的所有*,/运算符，如果此时的栈顶的运算符是+，-那么直接入栈，处理完之后将提取出的运算符入栈
	 * 4.如果是(,那么直接压入operatorStack栈中 5.如果是),重复处理来自operatorStack栈顶的运算符，知道看到栈顶的(
	 */

	
	public String getCurLine() {
		return curLine;
	}
	
	public void setCurLine(String cur) {
		curLine=cur;
	}
	
	public String getPreLines() {
		return preLines;
	}
	
	public void setPreLines(String cur) {
		preLines=cur;
	}
	
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
		StringBuilder cl = new StringBuilder(curLine);
		if (lastNum >= 0 && cl.charAt(lastNum) == '-' && !Character.isDigit(cl.charAt(lastNum - 1))) {
			cl.deleteCharAt(lastNum);
		} else {
			cl.insert(lastNum + 1, '-');
		}
		curLine = new String(cl);
		textResult.setText(preLines + curLine);
	}

	@FXML
	public void toFact(ActionEvent event) {
		curLine += "!";
		textResult.setText(curLine);
	}

	@FXML
	public void toRoot(ActionEvent event) {
		curLine += "√";
		textResult.setText(curLine);
	}

	@FXML
	public void toSquare(ActionEvent event) {
		curLine += "^";
		textResult.setText(curLine);
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
		}
		else {
			preLines += curLine + "\n";
			curLine = "0";
			textResult.setText(preLines + "Error");
		}
		scrollToBottom();
	}
	
	public String trim(String source, char trimChar) {
		int tail = source.length() - 1;

		while (tail > 0 && source.charAt(tail) == '0') {
			source = source.substring(0, source.length() - 1);
			tail--;
		}

		return source;
	}

}
