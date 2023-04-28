# fun_verifier
A basic program verification tool for the fun# programming language.

## Features ##
Verifies partial correctness of the fun# programming language, an extension of the fun imperative programming language as used in CS 429H, computer architecture at UT Austin.
Utilizes backwards analysis (based on Floyd-Hoare logic). In particular, the fun# verifier:
* Parses a given fun# program into an abstract syntax tree;
* Computes the approximate weakest precondition of a set of assertions for a function;
* Generates the verification conditions associated with said function;
* Feeds the conditions into an SMT solver (provided by the JavaSMT API);
* Outputs whether the program is verified (valid) or not.

## Additions to fun ##
The fun# programming language has some additional constructs in addition to the usual assignments, conditional statements, and loops provided by the fun programming language:
* Function preconditions, denoted by square brackets []
* Loop invariants, denoted by square brackets []
* A typing system with booleans and integers (WIP)

## Future Additions ##
* Automatic loop invariant finding
* Implement Theory of Rationals
* Implement Theory of Arrays
* Allow for function calls and handle side effects
* Handle types better

## Using the Fun Verifier ##
### Dependencies ###
This verifier relies on the JavaSMT API to solve first-order logic formulas. The JavaSMT API is retrieved and installed as a dependency through Apache Ivy at runtime.

### Installation Instructions ###
To use the fun verifier:
1. Install ant
2. Install apache ivy

### Usage ###
1. Copy the target fun file into in.fun in the working directory.
2. Run `ant` in the console to read the fun program, generate verification conditions, and feed them into the JavaSMT solver. Console outputs whether the program was verified or not. Optionally, run `ant run_nosolver` in the console to only generate verification conditions (doesn't feed them into an SMT solver).
3. Program trace is printed to console; the final result will be written to `result.out`. Functions will either be verified (valid on all inputs) or unverified (cannot be determined). Unverified means that either:
		1. the function is invalid
	    2. the function preconditions are not strong enough
	    3. the loop invariants are not strong enough