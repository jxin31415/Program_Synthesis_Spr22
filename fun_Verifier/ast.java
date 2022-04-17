package fun_Verifier;

public class ast {
   
    public static class Expression {
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

    public static class Plus extends Expression {
        Expression left, right;

        public Plus(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }
        
        public String toString() {
            return "("+ this.left.toString() + "+" + this.right.toString()+")";
        }
        
        // public int interpret(int[] envt) {
        //     return this.left.interpret(envt) + this.right.interpret(envt);
        // }
    }
    
    public static class Equal extends Expression {
        Expression left, right;

        public Equal(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return "(" + this.left.toString() + "==" + this.right.toString() + ")";
        }

        // public int interpret(int[] envt) {
        //     return this.left.interpret(envt) < this.right.interpret(envt) ? 1 : 0;
        // }
    }

    public static class LessThan extends Expression {
        Expression left, right;

        public LessThan(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return "(" + this.left.toString() + "<" + this.right.toString() + ")";
        }

        // public int interpret(int[] envt) {
        //     return this.left.interpret(envt) < this.right.interpret(envt) ? 1 : 0;
        // }
    }

    public static class And extends Expression{
        Expression left, right;

        public And(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return "(" + this.left.toString() + "&&" + this.right.toString() + ")";
        }    

        // public int interpret(int[] envt) {
        //     return (this.left.interpret(envt) != 0 && this.right.interpret(envt) != 0) ? 1 : 0;
        // }
    }

    public static class Or extends Expression{
        Expression left, right;

        public Or(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return "(" + this.left.toString() + "||" + this.right.toString() + ")";
        }    

        // public int interpret(int[] envt) {
        //     return (this.left.interpret(envt) != 0 && this.right.interpret(envt) != 0) ? 1 : 0;
        // }
    }

    public static class Not extends Expression {
        Expression exp;

        public Not(Expression exp) {
            this.exp = exp;
        }
        
        public String toString() {
            return "(!" + this.exp.toString()+ ")";
        }

        // public int interpret(int[] envt) {
        //     return this.exp.interpret(envt) == 0 ? 1 : 0;
        // }
    }

    public static class Negate extends Expression {
        Expression exp;

        public Negate(Expression exp) {
            this.exp = exp;
        }
        
        public String toString() {
            return "(-" + this.exp.toString()+ ")";
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
        Statement action;

        public While(Expression cond, Statement action) {
            this.cond = cond;
            this.action = action;
        }
        
        public String toString() {
            return "while (" + this.cond.toString() + ") {\n" + this.action.toString() + "}\n";
        }
    }

    public static class Assume extends Statement {
        Expression exp;

        public Assume(Expression exp){
            this.exp = exp;
        }

        public String toString() {
            return "assume (" + this.exp.toString() + ")\n";
        }
    }

    public static class Assert extends Statement {
        Expression exp;

        public Assert(Expression exp){
            this.exp = exp;
        }

        public String toString() {
            return "assume (" + this.exp.toString() + ")\n";
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
