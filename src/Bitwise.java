import java.util.ArrayList;
import java.util.Stack;

public class Bitwise {

	public static int evaluate(String ex) throws Exception {
		String[] infix = tokenize(ex.toCharArray());
		if (!isBalancedParenthesis(infix)) throw new Exception();
		String[] postfix = infixToPostfix(infix);
		return postfixEvaluate(postfix);
	}

	private static int postfixEvaluate(String[] postfix) throws Exception {
		Stack<String> stack = new Stack<>();
		for (int i = 0; i < postfix.length; ++i){
			if (postfix[i].matches("^[0-9]+$")){
				stack.push(postfix[i]);
			}
			else if (postfix[i].equals("~")){
				int operand = Integer.parseInt(stack.pop());
				int result = ~ operand;
				stack.push(String.valueOf(result));
			}
			else if (postfix[i].matches("[&^|]")){
				int operand2 = Integer.parseInt(stack.pop());
				int operand1 = Integer.parseInt(stack.pop());
				int result = operate(postfix[i],operand1,operand2);
				stack.push(String.valueOf(result));
			}
		}

		if (stack.size() != 1){
			throw new Exception();
		}

		return Integer.parseInt(stack.pop());
	}

	private static int operate(String op, int op1, int op2){
		switch (op) {
			case "&":
				return op1 & op2;

			case "^":
				return op1 ^ op2;
				
			case "|":
				return op1 | op2;
		
			default:
				return 0;
		}
	}

	private static boolean isBalancedParenthesis(String[] infix) {
		Stack<String> stack = new Stack<>();
		for (int i = 0; i < infix.length; ++i){
			if (isOpenParenthesis(infix[i])){
				stack.push(infix[i]);
			}
			else if (isClosingParenthesis(infix[i])){
				if (stack.isEmpty()) return false;
				stack.pop();
			}
		}

		return stack.isEmpty();
	}

	private static String[] tokenize(char[] ex){
		ArrayList<String> token = new ArrayList<>();
		for (int i = 0; i < ex.length; ++i){
			if (ex[i] >= '0' && ex[i] <= '9'){
				StringBuilder numBuilder = new StringBuilder();
				while (i < ex.length && ex[i] >= '0' && ex[i] <= '9'){
					numBuilder.append(ex[i++]);
				}
				token.add(numBuilder.toString());
				--i;
			}
			else {
				token.add(String.valueOf(ex[i]));
			}
		}
		return token.toArray(new String[]{});
	}

	private static String[] infixToPostfix(String[] infix) throws Exception {
		ArrayList<String> postfix = new ArrayList<>();
		Stack<String> stack = new Stack<>();
		for (int i = 0; i < infix.length; ++i){

			if (infix[i].equals("~") && (
				(i+1) == infix.length || 
				!isOperand(infix[i+1]) &&
				!isOpenParenthesis(infix[i+1]))){
				throw new Exception();
			}

			if (isOperand(infix[i])){
				postfix.add(infix[i]);
			}
			else if (isOperator(infix[i])){
				while (!stack.isEmpty() && 
				!isOpenParenthesis(stack.peek()) && 
				hasHigherPrecedence(stack.peek(),infix[i])){
					postfix.add(stack.pop());
				}
				stack.push(infix[i]);
			}
			else if (isOpenParenthesis(infix[i])){
				stack.push(infix[i]);
			}
			else if (isClosingParenthesis(infix[i])){
				while (!stack.isEmpty() && !isOpenParenthesis(stack.peek())){
					postfix.add(stack.pop());
				}
				stack.pop();
			}
		}

		while (!stack.isEmpty()){
			postfix.add(stack.pop());
		}

		return postfix.toArray(new String[]{});
	}

	private static boolean isOperand(String token){
		return token.matches("^[0-9]+$");
	}

	private static boolean isOperator(String token){
		return token.matches("[~&^|]");
	}

	private static boolean isOpenParenthesis(String token){
		return token.equals("(");
	}

	private static boolean isClosingParenthesis(String token){
		return token.equals(")");
	}

	private static boolean hasHigherPrecedence(String stackOp, String currentOp){
		return precedence(stackOp) > precedence(currentOp);
	}

	private static int precedence(String operator){
		switch (operator) {
			case "~":
				return 4;
			
			case "&":
				return 3;

			case "^":
				return 2;

			case "|":
				return 1;
			
			default:
				return 0;
		}
	}
}