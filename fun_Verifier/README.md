# fun_verifier
A basic program verification tool for the fun language as used in CS 429H.

## Features ##



## Future Additions ##


## Using the Fun Verifier ##
### Dependencies ###
This verifier relies on the JavaSMT API to solve first-order logic formulas. The JavaSMT API is retrieved and installed as a dependency through Apache Ivy at runtime.

### Installation Instructions ###
To use the fun verifier:
1. Install ant
2. Install apache ivy

### Usage ###
1. Copy the target fun file into in.fun in the working directory.
2. Run `ant` in the console to read the fun program, generate verification conditions, and feed them into the JavaSMT solver. Console outputs whether the program was verified or not.
2b. Optionally, run `ant run_nosolver` in the console to only generate verification conditions (doesn't feed them into an SMT solver).