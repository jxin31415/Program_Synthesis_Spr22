package fun_Verifier;

import java.util.*;
import java.io.*;
import fun_Verifier.ast.*;
import fun_Verifier.parser;

public class verifier {

    public static Expression awp(Statement s, Expression Q){
        if(s instanceof Statements){
            Statements statements = (Statements) s;
            Expression Qp = awp(statements.s2, Q);
            return awp(statements.s1, Qp);
        }

        if(s instanceof Ite){
            Ite ite = (Ite) s;
            Expression Qp1 = awp(ite.s1, Q);
            Expression Qp2 = awp(ite.s2, Q);

            Expression clause1 = new Or(new Not(ite.cond), Qp1);
            Expression clause2 = new Or(ite.cond, Qp2);

            return new And(clause1, clause2);
        }

        if(s instanceof Assign){
            Assign assign = (Assign) s;
            return replace(Q, assign.var, assign.exp);
        }

        if(s instanceof While){
            While loop = (While) s;
            return loop.invar;
        }

        if(s instanceof NoOp){
            return Q;
        }

        if(s instanceof Assert){
            Assert cond = (Assert) s;
            if(Q == null){
                return cond.exp;
            }
            return new And(Q, cond.exp);
        }

        throw new IllegalStateException("Invalid statement detected");
    }

    public static Expression replace(Expression node, Var target, Expression replacement){
        if(node == null)
            return node;
        
        if(node instanceof Var){
            Var var = (Var) node;
            if(var.name.equals(target.name)){
                return replacement;
            } else {
                return node;
            }
        }

        if(node instanceof Num){
            return node;
        }

        if(node instanceof True){
            return node;
        }

        if(node instanceof BinaryOperator){
            BinaryOperator bop = ((BinaryOperator) node).copy();
            bop.left = replace(bop.left, target, replacement);
            bop.right = replace(bop.right, target, replacement);
            return bop;
        }

        if(node instanceof UnaryOperator){
            UnaryOperator uop = ((UnaryOperator) node).copy();
            uop.exp = replace(uop.exp, target, replacement);
            return uop;
        }

        throw new IllegalStateException("Invalid expression detected");
    }

    public static Expression VC(Statement s, Expression Q){
        if(s instanceof Statements){
            Statements statements = (Statements) s;

            return new And(VC(statements.s2, Q), VC(statements.s1, awp(statements.s2, Q)));
        }

        if(s instanceof Ite){
            Ite ite = (Ite) s;

            return new And(VC(ite.s1, Q), VC(ite.s2, Q));
        }

        if(s instanceof Assign){
            return new True();
        }

        if(s instanceof While){
            While loop = (While) s;

            Expression clause1 = new And(loop.invar, loop.cond);
            Expression clause2 = new And(awp(loop.action, loop.invar), VC(loop.action, loop.invar));
            Expression clause3 = new Or(new Not(new And(loop.invar, new Not(loop.cond))), Q);

            return new And(new Or(new Not(clause1), clause2), clause3);
        }

        if(s instanceof NoOp){
            return new True();
        }

        if(s instanceof Assert){
            return new True();
        }

        throw new IllegalStateException("Invalid statement detected");
    }
    
    public static void main(String[] args) {
        List<Function> funcs;
        
        try {
            funcs = parser.functionParser("fun_Verifier/in.fun");
        } catch (IOException e){
            System.err.println("Error reading from file.");
            e.printStackTrace();
            return;
        }

        for(Function each: funcs){
            System.out.println("Function detected: ");
            System.out.print(each);

            System.out.println();
            System.out.println("Now computing: approximate weakest preconditions required to satisfy asserts: ");
            Expression awp = awp(each.start, null);
            System.out.println(awp);
            System.out.println();

            System.out.println("Now computing: verification conditions: ");
            Expression vc = VC(each.start, new True());
            System.out.println(vc);
            System.out.println();

            System.out.println("Now verifying the conditions:");
            System.out.println(vc + "\n&&\n" + each.precond + " => " + awp + "?");
        }
    }
}
