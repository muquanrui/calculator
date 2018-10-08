package application;

import java.io.IOException;
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

	private static String numPattern = "^(\\-|\\+)?\\d+(\\.\\d+)?$";
	private static String operandPattern = "\\(|\\)|\\*|/|\\+|-";

	private boolean confirm = true;

	private static ArrayList<Token> myTokens = new ArrayList<Token>();
	private static ArrayList<String> orator = new ArrayList<String>();
	private static ArrayList<String> orand = new ArrayList<String>();

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
        for(int i=0; i<textAll.length();i++){
            if(textAll.charAt(i) == '\n'){
                currentLine = i + 1;
            }
        }
        preLines = textAll.substring(0, currentLine);
        if(currentLine == textAll.length()){
            curLine = "";
        }else{
            curLine = textAll.substring(currentLine, textAll.length());
        }

	}

	// 使用空格分割字符串
	public String insetBlanks(String s) {
		String result = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '(' || s.charAt(i) == ')' || s.charAt(i) == '+' || s.charAt(i) == '*'
					|| s.charAt(i) == '/')
				result += " " + s.charAt(i) + " ";
			else if (s.charAt(i) == '-') {
				int front = i - 1;
				while (front > 0 && s.charAt(front) == ' ') {
					front--;
				}
				if (front > 0 && (s.charAt(front) == ')' || (s.charAt(front) >= '0' && s.charAt(front) <= '9'))) {
					result += " " + s.charAt(i) + " ";
				} else {
					result += s.charAt(i);
				}
			} else
				result += s.charAt(i);
		}

		return result;
	}

	public void checkChar() {

	}

	public ArrayList<String> analyzeExp() {
	    confirm = true;
	    myTokens.clear();
	    orand.clear();
	    orator.clear();
		String newExpression = insetBlanks(curLine);
		String[] tokens1 = newExpression.split(" ");

		ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(tokens1));
		
		Stack<Character> brackets = new Stack<>();
		Stack<String> orandS = new Stack<>();
		Stack<String> oratorS = new Stack<>();
		
		String newLine=curLine;

		
		for (int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			if (token.length() == 0) {
				tokens.remove(i);
				i--;
			} else if (Pattern.matches(numPattern, token)) {
				orator.add(token);
				oratorS.push(token);
				
				double d=Double.parseDouble(token);
								
				if(d<0) {
					newLine=newLine.replaceFirst("-", "@");
				}
				
			} else if (Pattern.matches(operandPattern, token)) {
				orand.add(token);		
				if (token.equals("(")) {
					brackets.push(token.charAt(0));
				} else if (token.equals(")")) {
					try {
						brackets.pop();
					} catch (Exception e) {
						confirm = false;
						
						System.err.println("错误! 位置：" + newLine.indexOf(token) + " 原因：输入括号未闭合");
						
						if (orand.indexOf("(") != -1) {
							orand.remove(orand.lastIndexOf("("));
						}
						orand.remove(orand.lastIndexOf(")"));
						tokens.remove(i);
						i--;
					}
				} else {
					orandS.push(token);			
					try {
						oratorS.pop();
					} catch(Exception e) {
						confirm = false;
						System.err.println("错误! 位置：" + newLine.indexOf(token) + " 原因：操作符使用错误");
						orand.remove(orand.lastIndexOf(token));
						tokens.remove(i);
						i--;
					
					}
				}
				
				if(token.charAt(0)!='(') {
					if(token.charAt(0)!='-') {
						newLine=newLine.replaceFirst("\\" +token, "@");
					}
					else {
						newLine=newLine.replaceFirst(token, "@");
					}	
				}
				
				
			} else {
				if (tokens.indexOf(token) + 1 < tokens.size()) {
					tokens.remove(i + 1);
				}
				
				tokens.remove(i);
				i--;
				
				confirm = false;
				
				int pos=0;
				for(int j=0;j<token.length();j++) {
					char c=token.charAt(j);
					if(c=='-'||c=='.'||(c>='0' && c<='9')) {
						continue;
					}
					pos=j;
					break;
				}			
				confirm = false;		
				System.err.println("错误! 位置：" + (newLine.indexOf(token)+pos) + " 原因：输入包含无效字符");
				
				char b[] = new char[token.length()];
			    Arrays.fill(b, 'c');
			    String re = new String(b);
				newLine=newLine.replaceFirst(token, re);
			}		
		}
		
		
		
		while(!brackets.isEmpty()) {	
			System.err.println("错误! 位置：" + newLine.indexOf("(") + " 原因：括号未闭合");
			confirm = false;
			newLine=newLine.replaceFirst("\\(", "#");
			orand.remove(orand.lastIndexOf("("));
			tokens.remove(tokens.lastIndexOf("("));
			brackets.pop();		
		}
		
		System.out.println("原始表达式：" + curLine);
		System.out.println("操作数：" + orator);
		System.out.println("操作符：" + orand);
		System.out.println("总token：" + tokens);

		return tokens;

	}

    @FXML
    public void pressNum(ActionEvent event){
        Object btnNum=event.getSource();
        Button btnCur = (Button) btnNum;
        if(curLine.equals("0")){
            curLine = "";
        }
        curLine += btnCur.getText();
        textResult.setText(preLines + curLine);
    }

    @FXML
    public void pressOperand(ActionEvent event){
        Object btnNum=event.getSource();
        Button btnCur = (Button) btnNum;
        String operandPress = btnCur.getText();
        if(operandPress.equals("＋")){
            curLine += "+";
        }else if(operandPress.equals("－")){
            curLine += "-";
        }else if(operandPress.equals("×")){
            curLine += "*";
        }else if(operandPress.equals("÷")){
            curLine += "/";
        }else{
            curLine += operandPress;
        }

        textResult.setText(preLines + curLine);
    }

    @FXML
    public void pressBackspace(ActionEvent event){
        curLine = curLine.substring(0, curLine.length() - 1);
        if(curLine.length() == 0){
            curLine = "0";
        }
        textResult.setText(preLines + curLine);
    }

    @FXML
    public void clearAll(ActionEvent event){
	    preLines = "";
	    curLine = "0";
        textResult.setText(preLines + curLine);
    }

    @FXML
    public void clearError(ActionEvent event){
        curLine = "0";
        textResult.setText(preLines + curLine);
    }

    @FXML
    public void toNegative(ActionEvent event){
	    if(trim(curLine, '0').length() == 0) return;
	    int lastNum = curLine.length() - 1;
	    while(lastNum >= 0 && Character.isDigit(curLine.charAt(lastNum))){
	        lastNum--;
        }
        StringBuilder cl = new StringBuilder(curLine);
	    if(lastNum >= 0 && cl.charAt(lastNum) == '-' && !Character.isDigit(cl.charAt(lastNum - 1))){
	        cl.deleteCharAt(lastNum);
        }else{
            cl.insert(lastNum + 1, '-');
        }
	    curLine = new String(cl);
        textResult.setText(preLines + curLine);
    }

	// 操作数、操作符入栈
	@FXML
	public void evaluateExp(ActionEvent event) {

		ArrayList<String> tokens = analyzeExp();

		if (confirm) {
			Stack<Double> operandStack = new Stack<>();
			Stack<Character> operatorStack = new Stack<>();

			for (String token : tokens) {
				if (token.length() == 0) // 如果是空格就继续循环
					continue;
				// 当前运算符是加减,无论操作符栈是什么运算符都要运算
				else if (token.length() == 1 && (token.charAt(0) == '+' || token.charAt(0) == '-')) {
					// 当栈不为空，并且栈中最上面的一个元素是加减乘除的任意一个
					while (!operatorStack.isEmpty() && (operatorStack.peek() == '-' || operatorStack.peek() == '+'
							|| operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
						processAnOperator(operandStack, operatorStack); // 开始运算
					}
					operatorStack.push(token.charAt(0)); // 当前运算符入栈
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				}
				// 当前运算符是乘除，判断最上面的是否是乘除，如果是乘除就运算，不是就直接入栈
				else if (token.charAt(0) == '*' || token.charAt(0) == '/') {
					while (!operatorStack.isEmpty() && (operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
						processAnOperator(operandStack, operatorStack);
					}
					operatorStack.push(token.charAt(0)); // 当前运算符入栈
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				}
				// 当前运算符是左括号，直接入栈
				else if (token.trim().charAt(0) == '(') {
					operatorStack.push('(');
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
				}
				// 当前运算符是右括号的，清除栈中的运算符直至左括号
				else if (token.trim().charAt(0) == ')') {
					OperatorToken newToken = new OperatorToken(token);
					myTokens.add(newToken);
					while (operatorStack.peek() != '(') {
						processAnOperator(operandStack, operatorStack);// 开始运算
					}
					operatorStack.pop();// 清除左括号
				}
				// 当前是数字的话，直接入数据栈
				else {
					boolean isNegative = false;
					double value = 0.0;
					if (token.charAt(0) == '-') {
						isNegative = true;
						token = token.substring(1, token.length());
					}
					value = Double.parseDouble(token);
					if (isNegative)
						value = -value;
					operandStack.push(value);// 数字字符串转换成数字，压入栈中
					OperandToken newToken = new OperandToken(value);
					myTokens.add(newToken);
				}
			}

			// 当栈中不是空的时候继续运算
			while (!operatorStack.isEmpty()) {
				processAnOperator(operandStack, operatorStack);
			}

			printOutTokens();

			DecimalFormat df = new DecimalFormat("0.000000000000");
			String res = df.format(operandStack.pop());
			res = trim(res, '0');
			if(res.charAt(res.length() - 1) == '.'){
			    res = res.substring(0, res.length() - 1);
            }

			preLines += curLine + "\n";
            curLine = res;
			textResult.setText(preLines + curLine);// 运算结果
		}
		else {
            preLines += curLine + "\n";
            curLine = "0";
            textResult.setText(preLines + "Error");
		}
		scrollToBottom();
	}

	public void scrollToBottom(){
	    scrollResult.setVvalue(1);
    }

	public void printOutTokens() {
		if (myTokens.size() == 0) {
			System.out.println("No tokens");
			return;
		}

		for (Token token : myTokens) {
			if (token.isOperand()) {
				System.out.println("Operand: " + token.getText());
			} else if (token.isOperator()) {
				System.out.println("Operator: " + token.getText());
			}
		}

		System.out.print("Token sequence: ");
		for (Token token : myTokens) {
			System.out.print(" " + token.getText() + " ");
		}
		System.out.println();

	}

	public String trim(String source, char trimChar) {
		int tail = source.length() - 1;

		while (tail > 0 && source.charAt(tail) == '0') {
			source = source.substring(0, source.length() - 1);
			tail--;
		}

		return source;
	}

	// 处理栈中的两个数据，将栈中的两个数据运算之后的结果存储在栈中
	public void processAnOperator(Stack<Double> operandStack, Stack<Character> operatorStack) {
		char op = operatorStack.pop(); // 弹出一个操作符
		double op1 = operandStack.pop(); // 弹出连个两个数用来和操作符op进行运算
		double op2 = operandStack.pop();
		if (op == '+')
			operandStack.push(op1 + op2);
		else if (op == '-')
			operandStack.push(op2 - op1);
		else if (op == '*')
			operandStack.push(op1 * op2);
		else if (op == '/')
			operandStack.push(op2 / op1);
	}

	protected static class OperandToken extends Token {
		private double value;

		protected OperandToken(double d) {
			value = d;
		}

		public boolean isOperand() {
			return true;
		}

		public double getValue() {
			return value;
		}

		public String getText() {
			return Double.toString(value);
		}
	}

	protected static class OperatorToken extends Token {
		private String ope;

		protected OperatorToken(String s) {
			ope = s;
		}

		public boolean isOperator() {
			return true;
		}

		public String getText() {
			return ope;
		}
	}

}
