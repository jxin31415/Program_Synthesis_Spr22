package verifier;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;
import org.sosy_lab.java_smt.api.SolverException;

import verifier.ast.*;
import verifier.parser.*;
import verifier.verifier.*;


public class verifyAndSolve {
    
    public static Map<String, IntegerFormula> variables;
    public static BooleanFormulaManager bmgr;
    public static IntegerFormulaManager imgr;
    
    public static BooleanFormula buildBooleanFormula(Expression exp) {
        if(exp instanceof Not) {
            Not not = (Not) exp;
            return bmgr.not(buildBooleanFormula(not.exp));
        }
        
        if(exp instanceof True) {
            return bmgr.makeTrue();
        }
        
        if(exp instanceof And) {
            And and = (And) exp;
            return bmgr.and(buildBooleanFormula(and.left), buildBooleanFormula(and.right));
        }
        
        if(exp instanceof Or) {
            Or or = (Or) exp;
            return bmgr.or(buildBooleanFormula(or.left), buildBooleanFormula(or.right));
        }
        
        if(exp instanceof Equal) {
            Equal eq = (Equal) exp;
            return imgr.equal(buildIntegerFormula(eq.left), buildIntegerFormula(eq.right));
        }
        
        if(exp instanceof LessThan) {
            LessThan lt = (LessThan) exp;
            return imgr.lessThan(buildIntegerFormula(lt.left), buildIntegerFormula(lt.right));
        }
        
        throw new IllegalStateException("Integers detected in boolean formula");
    }
        
    public static IntegerFormula buildIntegerFormula(Expression exp) {
        if(exp instanceof Var) {
            Var var = (Var) exp;
            if(!variables.containsKey(var.name)) {
                variables.put(var.name, imgr.makeVariable(var.name));
            }
            return variables.get(var.name);
        }
        
        if(exp instanceof Num) {
            Num num = (Num) exp;
            return imgr.makeNumber(num.val);
        }
        
        if(exp instanceof Multiply) {
            Multiply multiply = (Multiply) exp;
            return imgr.multiply(buildIntegerFormula(multiply.left), buildIntegerFormula(multiply.right));
        }

        if(exp instanceof Plus) {
            Plus plus = (Plus) exp;
            return imgr.add(buildIntegerFormula(plus.left), buildIntegerFormula(plus.right));
        }
        
        if(exp instanceof Negate) {
            Negate neg = (Negate) exp;
            return imgr.negate(buildIntegerFormula(neg.exp));
        }
        
        throw new IllegalStateException("Booleans detected in integer formula");
    }
    
    public static void main(String[] args) throws InvalidConfigurationException, SolverException, InterruptedException, IOException {
        
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
        try {
            out = new PrintWriter("result.out");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        List<Function> funcs;
        
        try {
            funcs = parser.functionParser("in.fun");
        } catch (IOException e){
            System.err.println("Error reading from file.");
            e.printStackTrace();
            return;
        }
        
        Configuration config = Configuration.defaultConfiguration();
        LogManager logger = BasicLogManager.create(config);
        ShutdownNotifier notifier = ShutdownNotifier.createDummy();

        Solvers solver = Solvers.SMTINTERPOL;
        for(Function each: funcs){
           Expression exp = verifier.generateVerification(each); 
           
           try (SolverContext context =  SolverContextFactory.createSolverContext(config, logger, notifier, solver)) {
               
               FormulaManager fmgr = context.getFormulaManager();

               bmgr = fmgr.getBooleanFormulaManager();
               imgr = fmgr.getIntegerFormulaManager();

               variables = new HashMap<>();
               
               BooleanFormula constraint = buildBooleanFormula(exp); // This checks satisfiability
               
               System.out.println("Checking satisfiability...");
               try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
                   prover.addConstraint(constraint);
                   boolean isUnsat = prover.isUnsat();
                   if (!isUnsat) {
                       Model model = prover.getModel();
                       System.out.println("Function is satisfiable.");
                       System.out.println("Model output:");
                       for(String s: variables.keySet()) {
                           System.out.println(s + " := " + model.evaluate(variables.get(s)));
                       }
                   } else {
                       System.out.println("Function is not satisfiable.");
                   }
               }
               
               BooleanFormula valid = bmgr.not(buildBooleanFormula(exp)); // This checks validity
               System.out.println("\n\nChecking validity...");
               
               try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
                   prover.addConstraint(valid);
                   boolean isUnsat = prover.isUnsat();
                   if (isUnsat) {
                       System.out.println("Function is valid and verified!!");
                       out.println("Function is valid and verified!!");
                   } else {
                       System.out.println("Function unable to be verified :(\nThis may mean the function is invalid, or it may mean that you need to strengthen its preconditions or any loop invariants.");
                       out.println("Function unable to be verified :(\nThis may mean the function is invalid, or it may mean that you need to strengthen its preconditions or any loop invariants.");
                   }
                 }
               
           } catch (InvalidConfigurationException | UnsatisfiedLinkError e) {
               // on some machines we support only some solvers,
               // thus we can ignore these errors.
               System.out.println("Solver " + solver + " is not available.");
           } catch (UnsupportedOperationException e) {
               System.out.println("Error");
           }
        }
        
        out.close();
    }
    
    
}
