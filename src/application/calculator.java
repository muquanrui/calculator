package application;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.script.*;  


/**可能出现的错误：
 * 1.输入非法字符
 * 2.括号不闭合
 * 3.连续的操作符
 * 4.*/


public class calculator{
	
	private static ArrayList<Token> myTokens = new ArrayList<Token>();
	
    /**算数表达式计算处理思路：
     * 程序从左到右的扫描表达式，提取出操作数，操作符，以及括号
     * 1.如果是操作数，直接将其压入operandStack栈中
     * 2.如果是+，-运算符，处理operatorStack栈定中的所有运算符，处理完之后将提取出的运算符压入栈中
     * 3.如果是*,/运算符，处理栈顶的所有*,/运算符，如果此时的栈顶的运算符是+，-那么直接入栈，处理完之后将提取出的运算符入栈
     * 4.如果是(,那么直接压入operatorStack栈中
     * 5.如果是),重复处理来自operatorStack栈顶的运算符，知道看到栈顶的(
     */
    
	//使用空格分割字符串
    public String insetBlanks(String s) {
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(' || s.charAt(i) == ')' || s.charAt(i) == '+' || s.charAt(i) == '*' || s.charAt(i) == '/')
                result += " " + s.charAt(i) + " ";
            else if(s.charAt(i) == '-') {
            	int front = i - 1;
            	while(front > 0 && s.charAt(front) == ' ') {
            		front--;
            	}
            	if(front > 0 && (s.charAt(front) == ')' || (s.charAt(front) >= '0' && s.charAt(front) <= '9'))) {
            		result += " " + s.charAt(i) + " ";
            	}else {
                    result += s.charAt(i);
            	}
            }
            else
                result += s.charAt(i);
        }
        return result;
    }

    //操作数、操作符入栈
    public String evaluateExp(String expression) {
        Stack<Double> operandStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();
        expression = insetBlanks(expression);
        String[] tokens = expression.split(" ");
        for (String token : tokens) {
            if (token.length() == 0)   //如果是空格就继续循环
                continue;
            //当前运算符是加减,无论操作符栈是什么运算符都要运算
            else if (token.length() == 1 && (token.charAt(0) == '+' || token.charAt(0) == '-')) {
                //当栈不为空，并且栈中最上面的一个元素是加减乘除的任意一个
                while (!operatorStack.isEmpty()&&(operatorStack.peek() == '-' || operatorStack.peek() == '+' || operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
                    processAnOperator(operandStack, operatorStack);   //开始运算
                }
                operatorStack.push(token.charAt(0));   //当前运算符入栈
                OperatorToken newToken = new OperatorToken(token);
                myTokens.add(newToken);
            }
            //当前运算符是乘除，判断最上面的是否是乘除，如果是乘除就运算，不是就直接入栈
            else if (token.charAt(0) == '*' || token.charAt(0) == '/') {
                while (!operatorStack.isEmpty()&&(operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
                    processAnOperator(operandStack, operatorStack);
                }
                operatorStack.push(token.charAt(0));   //当前运算符入栈
                OperatorToken newToken = new OperatorToken(token);
                myTokens.add(newToken);
            }
            //当前运算符是左括号，直接入栈
            else if (token.trim().charAt(0) == '(') {
                operatorStack.push('(');
                OperatorToken newToken = new OperatorToken(token);
                myTokens.add(newToken);
            }
            //当前运算符是右括号的，清除栈中的运算符直至左括号
            else if (token.trim().charAt(0) == ')') {
                OperatorToken newToken = new OperatorToken(token);
                myTokens.add(newToken);
                while (operatorStack.peek() != '(') {
                    processAnOperator(operandStack, operatorStack);//开始运算
                }
                operatorStack.pop();//清除左括号
            }
            //当前是数字的话，直接入数据栈
            else {
            	boolean isNegative = false;
            	double value = 0.0;
            	if(token.charAt(0) == '-') {
            		isNegative = true;
            		token = token.substring(1, token.length());
            	}
            	value = Double.parseDouble(token);
            	if(isNegative) value = -value;
                operandStack.push(value);//数字字符串转换成数字，压入栈中
                OperandToken newToken = new OperandToken(value);
                myTokens.add(newToken);
            }
        }
        //当栈中不是空的时候继续运算
        while (!operatorStack.isEmpty()) {
            processAnOperator(operandStack, operatorStack);
        }
        
        printOutTokens();
        
        DecimalFormat df = new DecimalFormat("0.00000000000000000");
        String res = df.format(operandStack.pop());
        res = trim(res, '0');
        
        return res;//运算结果
    }
    
    public void printOutTokens() {
    	if(myTokens.size() == 0) {
    		System.out.println("No tokens");
    		return;
    	}
    	
    	for(Token token : myTokens) {
    		if(token.isOperand()) {
        		System.out.println("Operand: " + token.getText());
    		}else if(token.isOperator()) {
        		System.out.println("Operator: " + token.getText());
    		}
    	}
    	
    	System.out.print("Token sequence: ");
    	for(Token token : myTokens) {
    		System.out.print( " " + token.getText() + " ");
    	}
    	System.out.println();
    	
    }
    
    public String trim(String source, char trimChar) {
    	int tail = source.length() - 1;
    	
    		while(tail > 0 && source.charAt(tail) == '0') {
    			source = source.substring(0, source.length() - 1);
    			tail--;
    		}
    		
    	return source;
    }

    
    //处理栈中的两个数据，将栈中的两个数据运算之后的结果存储在栈中
    public void processAnOperator(Stack<Double> operandStack, Stack<Character> operatorStack) {
        char op = operatorStack.pop(); //弹出一个操作符
        double op1 = operandStack.pop(); //弹出连个两个数用来和操作符op进行运算
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
    
    protected static class OperandToken extends Token{
    	private double value;
    	
    	protected OperandToken(double d) {
    		value = d;
    	}
    	
    	public boolean isOperand(){
    		return true;
    	}
    	
    	public double getValue() {
    		return value;
    	}
    	
    	public String getText() {
    		return Double.toString(value);
    	}
    }
    
    protected static class OperatorToken extends Token{
    	private String ope;
    	
    	protected OperatorToken(String s) {
    		ope = s;
    	}
    	
    	public boolean isOperator(){
    		return true;
    	}
    	
    	public String getText() {
    		return ope;
    	}
    }
    
}



