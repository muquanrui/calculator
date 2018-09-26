package application;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;
import javax.script.*;  

public class calculator{
	
	/**输入处理思路：
	 * 将输入根据是否有"="分为赋值表达式，输出表达式分别处理
	 * 1.联系上下文，将bindings所包含的变量及其对应值导入，如没有，则新建
	 * 2.对于赋值表达式，检查变量名规范，分割为变量名和计算表达式两部分
	 * 3.计算当前变量名对应的算数表达式，put这一对键值
	 * 4.对于输出表达式，分为表达式输出和变量输出，表达式输出则进行表达式计算，变量输出则输出变量对应的值*/
	
	/**异常处理思路：
	 * 1.表达式规范：以分号结尾，否则出现Error:expression with no semicolon.
	 * 2.变量名规范：由数字和字母组成，且以字母开头，否则出现Error:incorrect variable name.
	 * 3.计算规范：如果算术表达式包括/0操作，出现Error:Wrong calculation with "/0".
	 * 4.计算规范：如果算术表达式包括还未定义的变量，出现Error:undefined identifier.*/ 	
	
	int line=0;//记录行数
  	int position=0;//记录位置
  	String varPa="[a-zA-Z$][a-zA-Z0-9$]*";//变量名规范
	String expPa=".*;";//表达式规范
  
    
	
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
            if (s.charAt(i) == '(' || s.charAt(i) == ')' || s.charAt(i) == '+' || s.charAt(i) == '-'|| s.charAt(i) == '*' || s.charAt(i) == '/')
                result += " " + s.charAt(i) + " ";
            else
                result += s.charAt(i);
        }
        return result;
    }

    //操作数、操作符入栈
    public double evaluateExp(String expression) {
        Stack<Double> operandStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();
        expression = insetBlanks(expression);
        String[] tokens = expression.split(" ");
        for (String token : tokens) {
            if (token.length() == 0)   //如果是空格就继续循环
                continue;
            //当前运算符是加减,无论操作符栈是什么运算符都要运算
            else if (token.charAt(0) == '+' || token.charAt(0) == '-') {
                //当栈不为空，并且栈中最上面的一个元素是加减乘除的任意一个
                while (!operatorStack.isEmpty()&&(operatorStack.peek() == '-' || operatorStack.peek() == '+' || operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
                    processAnOperator(operandStack, operatorStack);   //开始运算
                }
                operatorStack.push(token.charAt(0));   //当前运算符入栈
            }
            //当前运算符是乘除，判断最上面的是否是乘除，如果是乘除就运算，不是就直接入栈
            else if (token.charAt(0) == '*' || token.charAt(0) == '/') {
                while (!operatorStack.isEmpty()&&(operatorStack.peek() == '/' || operatorStack.peek() == '*')) {
                    processAnOperator(operandStack, operatorStack);
                }
                operatorStack.push(token.charAt(0));   //当前运算符入栈
            }
            //当前运算符是左括号，直接入栈
            else if (token.trim().charAt(0) == '(') {
                operatorStack.push('(');
            }
            //当前运算符是右括号的，清除栈中的运算符直至左括号
            else if (token.trim().charAt(0) == ')') {
                while (operatorStack.peek() != '(') {
                    processAnOperator(operandStack, operatorStack);//开始运算
                }
                operatorStack.pop();//清除左括号
            }
            //当前是数字的话，直接入数据栈
            else {
                operandStack.push(Double.parseDouble(token));//数字字符串转换成数字，压入栈中
            }
        }
        //当栈中不是空的时候继续运算
        while (!operatorStack.isEmpty()) {
            processAnOperator(operandStack, operatorStack);
        }
        return operandStack.pop();//运算结果
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
    
}
