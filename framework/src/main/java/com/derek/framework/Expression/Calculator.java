package com.derek.framework.Expression;

import java.util.Stack;

/**
 * 处理解释相关的业务
 */
public class Calculator {
    private Stack<ArithmeticExpression> mExpStack = new Stack<>();

    public Calculator(String express) {
        ArithmeticExpression exp1,exp2;
        String[] elements = express.split(" ");

        for (int i = 0;i<elements.length;i++){
            switch (elements[i].charAt(0)){
                case '+':
                    exp1 = mExpStack.pop();
                    exp2 = new NumExpression(Integer.valueOf(elements[++i]));
                    mExpStack.push(new AdditionExpression(exp1,exp2));
                    break;
                default:
                    mExpStack.push(new NumExpression(Integer.valueOf(elements[i])));
                    break;
            }
        }
    }

    public int calculate(){
        return mExpStack.pop().interpret();
    }
}
