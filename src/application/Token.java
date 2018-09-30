package application;

public abstract class Token {
	
	public boolean isOperator() {
		return false;
	}
	
	public boolean isOperand() {
		return false;
	}
	
	public double getValue() {
		throw new RuntimeException("Not operand");
	}
	
	public String getText() {
		return "";
	}

}
