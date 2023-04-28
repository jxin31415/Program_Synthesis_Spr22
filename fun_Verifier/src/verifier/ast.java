package verifier;

public class ast {

    public static class Function {
        Expression precond;
        Statement start;

        public Function(Expression precond, Statement start){
            if(precond == null)
                this.precond = new True();
            else
                this.precond = precond;
            this.start = start;
        }

        public String toString() {
            if(precond == null){
                return "fun {\n" + this.start.toString() + "}\n";
            }
            return "fun [" + this.precond.toString() + "] {\n" + this.start.toString() + "}\n";
        }

        public int interpret(int[] envt) {
            throw new Error("Unimplemented method: interpret()");
        }
    }
   
    public static class Expression {
        public String toString() {
            throw new Error("Unimplemented method: toString()");
        }

        public int interpret(int[] envt) {
            throw new Error("Unimplemented method: interpret()");
        }
    }

    public static abstract class BinaryOperator extends Expression {
        Expression left, right;

        public BinaryOperator(Expression left, Expression right){
            this.left = left;
            this.right = right;
        }

        public BinaryOperator copy(){
            return this.copy();
        }

        public String toString() {
            throw new Error("Unimplemented method: toString()");
        }

        public int interpret(int[] envt) {
            throw new Error("Unimplemented method: interpret()");
        }
    }

    public static class UnaryOperator extends Expression {
        Expression exp;

        public UnaryOperator(Expression exp){
            this.exp = exp;
        }

        public UnaryOperator copy(){
            return this.copy();
        }

        public String toString() {
            throw new Error("Unimplemented method: toString()");
        }

        public int interpret(int[] envt) {
            throw new Error("Unimplemented method: interpret()");
        }
    }

    public static class Var extends Expression {
        String name;

        public Var(String name) {
            this.name = name;
        }
        
        public String toString() {
            return this.name;
        }

        // public int interpret(int[] envt) {
        //     return envt[this.name];
        // }
    }

    public static class Num extends Expression {
        int val;

        public Num(int val) {
            this.val = val;
        }
        
        public String toString() {
            return "" + this.val;
        }

        // public int interpret(int[] envt) {
        //     return this.val;
        // }
    }

    public static class True extends Expression {
        public True(){}

        public String toString() {
            return "T";
        }

        // public int interpret(int[] envt) {
        //     return 1;
        // }
    }

    public static class Plus extends BinaryOperator {

        public Plus(Expression left, Expression right) {
            super(left, right);
        }

        public Plus copy(){
            return new Plus(this.left, this.right);
        }
        
        public String toString() {
            return "("+ this.left.toString() + "+" + this.right.toString()+")";
        }
        
        // public int interpret(int[] envt) {
        //     return this.left.interpret(envt) + this.right.interpret(envt);
        // }
    }

    public static class Multiply extends BinaryOperator {

        public Multiply(Expression left, Expression right) {
            super(left, right);
        }

        public Multiply copy(){
            return new Multiply(this.left, this.right);
        }
        
        public String toString() {
            return "("+ this.left.toString() + "*" + this.right.toString()+")";
        }
        
        // public int interpret(int[] envt) {
        //     return this.left.interpret(envt) * this.right.interpret(envt);
        // }
    }
    
    public static class Equal extends BinaryOperator {

        public Equal(Expression left, Expression right) {
            super(left, right);
        }

        public Equal copy(){
            return new Equal(this.left, this.right);
        }

        public String toString() {
            return "(" + this.left.toString() + "==" + this.right.toString() + ")";
        }

        // public int interpret(int[] envt) {
        //     return this.left.interpret(envt) < this.right.interpret(envt) ? 1 : 0;
        // }
    }

    public static class LessThan extends BinaryOperator {

        public LessThan(Expression left, Expression right) {
            super(left, right);
        }

        public LessThan copy(){
            return new LessThan(this.left, this.right);
        }

        public String toString() {
            return "(" + this.left.toString() + "<" + this.right.toString() + ")";
        }

        // public int interpret(int[] envt) {
        //     return this.left.interpret(envt) < this.right.interpret(envt) ? 1 : 0;
        // }
    }

    public static class And extends BinaryOperator {

        public And(Expression left, Expression right) {
            super(left, right);
        }

        public And copy(){
            return new And(this.left, this.right);
        }

        public String toString() {
            return "(" + this.left.toString() + "&&" + this.right.toString() + ")";
        }    

        // public int interpret(int[] envt) {
        //     return (this.left.interpret(envt) != 0 && this.right.interpret(envt) != 0) ? 1 : 0;
        // }
    }

    public static class Or extends BinaryOperator {

        public Or(Expression left, Expression right) {
            super(left, right);
        }

        public Or copy(){
            return new Or(this.left, this.right);
        }

        public String toString() {
            return "(" + this.left.toString() + "||" + this.right.toString() + ")";
        }    

        // public int interpret(int[] envt) {
        //     return (this.left.interpret(envt) != 0 && this.right.interpret(envt) != 0) ? 1 : 0;
        // }
    }

    public static class Not extends UnaryOperator {

        public Not(Expression exp) {
            super(exp);
        }

        public Not copy(){
            return new Not(this.exp);
        }
        
        public String toString() {
            return "!" + this.exp.toString();
        }

        // public int interpret(int[] envt) {
        //     return this.exp.interpret(envt) == 0 ? 1 : 0;
        // }
    }

    public static class Negate extends UnaryOperator {

        public Negate(Expression exp) {
            super(exp);
        }

        public Negate copy(){
            return new Negate(this.exp);
        }
        
        public String toString() {
            return "-" + this.exp.toString();
        }

        // public int interpret(int[] envt) {
        //     return -this.exp.interpret(envt);
        // }
    }


    public static class Statement {
        public String toString() {
            throw new Error("Unimplemented method: toString()");
        }
    }

    public static class Statements extends Statement {
        Statement s1, s2;
        
        public Statements(Statement s1, Statement s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        public String toString() {
            return this.s1.toString() + this.s2.toString();
        }
    }

    public static class Ite extends Statement {
        Expression cond;
        Statement s1, s2;

        public Ite(Expression cond, Statement s1, Statement s2) {
            this.cond = cond;
            this.s1 = s1;
            this.s2 = s2;
        }
        
        public String toString() {
            return "if (" + this.cond.toString() + ") {\n" + this.s1.toString() + "} else {\n" + this.s2.toString() + "}\n";
        }
    }

    public static class Assign extends Statement {
        Var var;
        Expression exp;

        public Assign(Var var, Expression exp) {
            this.var = var;
            this.exp = exp;
        }
        
        public String toString() {
            return this.var.toString() + " = " + this.exp.toString() + "\n";
        }
    } 

    public static class While extends Statement {
        Expression cond;
        Expression invar;
        Statement action;

        public While(Expression cond, Statement action, Expression invar) {
            this.cond = cond;
            this.action = action;
            this.invar = invar;
        }
        
        public String toString() {
            if(this.invar == null)
                return "while (" + this.cond.toString() + ") {\n" + this.action.toString() + "}\n";
            return "while (" + this.cond.toString() + ") [" + this.invar.toString() + "] {\n" + this.action.toString() + "}\n";
        }
    }

    public static class Assert extends Statement {
        Expression exp;

        public Assert(Expression exp){
            this.exp = exp;
        }

        public String toString() {
            return "assert (" + this.exp.toString() + ")\n";
        }
    }

    public static class NoOp extends Statement {

        public NoOp(){
        }

        public String toString(){
            return "";
        }
    }
}
