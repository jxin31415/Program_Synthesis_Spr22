package fun_Verifier;

import java.io.*;
import java.util.*;

import fun_Verifier.ast.*;

public class parser {
    
    public static List<Statement> functionParser(String file) throws IOException {
        FastReader scan = new FastReader(file);

        // fun program should be syntactically valid
        StringBuilder prog = new StringBuilder("");
        String next = scan.nextLine();
        while(next != null){
            int end = (next.indexOf("//") == -1) ? next.length() : next.indexOf("//");
            prog.append(next.substring(0, end));
            next = scan.nextLine();
        }

        // Remove comments
        while(prog.indexOf("/*") != -1){
            prog.delete(prog.indexOf("/*"), prog.indexOf("*/") + 2);
        }

        System.out.println(prog);

        // Find every function
        List<String> functions = new ArrayList<>();
        for(int i = 2; i < prog.length(); i++){
            if(prog.substring(i-2, i+1).equals("fun")){
                int j = i+1;
                while(prog.charAt(j) != '{'){
                    j++;
                }

                int bracket = 1; j++;

                int start = j;
                while(bracket != 0){
                    if(prog.charAt(j) == '{')
                        bracket++;
                    else if (prog.charAt(j) == '}')
                        bracket--;
                    
                    j++;
                }

                functions.add(prog.substring(start, j-1));
            }
        }

        System.out.println(functions);

        List<Statement> funcs = new ArrayList<>();

        for(String each: functions){
            // Parse functions into an AST
            Statement func = null;
            index = 0;
            current = each + "x=0"; // Insert no-op

            while(true){
                try {
                    if(func == null) {
                        func = statement();
                    } else {
                        func = new Statements(func, statement());
                    }
                } catch(Exception e){
                    // End of function reached
                    break;
                }
            }

            funcs.add(func);
        }

        return funcs;
    }

    static int index;
    static String current;

    public static Statement statement(){
        if(consume("print")){

            // Handle print statement
            expression();
            return statement();

        } else if(consume("if")){
            // Handle if statement
            Expression cond = expression();
            Statement ifBlock = null;
            Statement elseBlock = null;
            if(consume("{")){
                // Handle if statements with curly braces
                while(!consume("}")){
                    if(ifBlock == null){
                        ifBlock = statement();
                    } else {
                        ifBlock = new Statements(ifBlock, statement());
                    }
                }
            } else {
                // Handle one-statement if statement
                ifBlock = statement();
            }
    
            if(consume("else")){
                if(consume("{")){
                    // Handle else blocks with curly braces
                    while(!consume("}")){
                        if(elseBlock == null){
                            elseBlock = statement();
                        } else {
                            elseBlock = new Statements(elseBlock, statement());
                        }
                    }
                } else {
                    // Handle one-statement else block
                    elseBlock = statement();
                }
            } else {
                elseBlock = new NoOp();
            }
    
            return new Ite(cond, ifBlock, elseBlock);
        } else if(consume("while")){
            // Handle loops
            Expression cond = expression();
            Expression invar = null;
            Statement block = null;

            if(consume("[")){
                invar = expression();
                consume("]");
            }

            if(consume("{")){
                // Handle loops with curly braces
                while(!consume("}")){
                    if(block == null){
                        block = statement();
                    } else {
                        block = new Statements(block, statement());
                    }
                }
            } else {
                // Handle one-statement loop
                block = statement();
            }

            return new While(cond, block, invar);

        } else if(consume("assume")){
            return new Assume(expression());
        } else if(consume("assert")){
            return new Assert(expression());
        } else {
            // Handle variable assignment
            Var var = consumeVar();
            if(consume("=")){
                Expression exp = expression();
                return new Assign(var, exp);
            } else {
                throw new IllegalStateException();
            }
        }
    }  

    // () [] . -> ...
    public static Expression e1(){
        Var var = consumeVar();
        if(var != null){
            return var;
        }
        
        Num num = consumeNum();
        if(num != null){
            return num;
        }
        
        if(consume("(")){
            Expression exp = expression();
            consume(")");
            return exp;
        }

        throw new IllegalStateException();
    }

    // - (negation)
    public static Expression e2(){   
        if(consume("-")){
            return new Negate(e1());
        } else {
            return e1();
        }
    }

    // (Left) + -
    public static Expression e3(){
        Expression v = e2();

        while(true){
            if(consume("+")){
                v = new Plus(v, e2());
            } else if(consume("-")){
                v = new Plus(v, new Negate(e2()));
            } else {
                return v;
            }
        }
    }

    // < <= > >=
    public static Expression e4(){
        Expression v = e3();

        while(true){
            if(consume("<=")){
                Expression v2 = e3();
                v = new Or(new LessThan(v, v2), new Equal(v, v2));
            } else if(consume(">=")){
                Expression v2 = e3();
                v = new Or(new LessThan(v2, v), new Equal(v2, v));
            } else if(consume("<")){
                v = new LessThan(v, e3());
            } else if(consume(">")){
                v = new LessThan(e3(), v);
            } else {
                return v;
            }
        }
    }

    // == !=
    public static Expression e5(){
        Expression v = e4();

        while(true){
            if(consume("==")){
                v = new Equal(v, e4());
            } else if(consume("!=")){
                v = new Not(new Equal(v, e4()));
            } else {
                return v;
            }
        }
    }

    // &&
    public static Expression e6(){
        Expression v = e5();

        while(true){
            if(consume("&&")){
                v = new And(v, e5());
            } else {
                return v;
            }
        }
    }

    // &&
    public static Expression e7(){
        Expression v = e6();

        while(true){
            if(consume("||")){
                v = new Or(v, e6());
            } else {
                return v;
            }
        }
    }

    // ! (complement)
    public static Expression e8(){        
        if(consume("!")){
            return new Not(e7());
        } else {
            return e7();
        }
    }

    public static Expression expression(){
        return e8();
    }

    public static Var consumeVar(){
        skip();

        if(Character.isLetter(current.charAt(index))){
            int i = index;
            while(Character.isLetterOrDigit(current.charAt(i))){
                i++;
            }

            Var var = new Var(current.substring(index, i));

            index = i;
            return var;
        }

        return null;
    }

    public static Num consumeNum(){
        skip();

        if(Character.isDigit(current.charAt(index))){
            int i = 0;
            while(Character.isDigit(current.charAt(index))){
                i = i * 10 + (current.charAt(index) - 48);
                index++;
            }

            return new Num(i);
        }
        
        return null;
    }

    public static boolean consume(String target){
        skip();

        int i = 0;
        while(i < target.length()){
            char found = current.charAt(index + i);
            char expected = target.charAt(i);
            
            if(expected != found){
                return false;
            }
            i++;
        }

        index += i;
        return true;
    }

    public static void skip(){
        while(Character.isWhitespace(current.charAt(index)))
            index++;
    }

    static class FastReader {
        BufferedReader br;
        StringTokenizer st;

        public FastReader() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        public FastReader(String s) throws FileNotFoundException {
            br = new BufferedReader(new FileReader(new File(s)));
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }

        String nextLine() {
            String str = "";
            try {
                str = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }
}