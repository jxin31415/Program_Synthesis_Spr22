package verifier;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import java.io.*;
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
    
    public static void main(String[] args) throws InvalidConfigurationException, SolverException, InterruptedException, IOException {
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

        for(Function each: funcs){
           Expression exp = verifier.generateVerification(each);  
        }
    }
}
