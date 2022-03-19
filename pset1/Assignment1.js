// JavaScript source code
'use strict';

var NUM = "NUM";
var FALSE = "FALSE";
var VR = "VAR";
var PLUS = "PLUS";
var TIMES = "TIMES";
var LT = "LT";
var AND = "AND";
var NOT = "NOT";
var ITE = "ITE";

var ALLOPS = [NUM, FALSE, VR, PLUS, TIMES, LT, AND, NOT, ITE];

function str(obj) { return JSON.stringify(obj); }

//Constructor definitions for the different AST nodes.


/**************************************************
 ***********  AST node definitions *****************
 ****************************************************/

class Node {
    toString() {
        throw new Error("Unimplemented method: toString()");
    }

    interpret() {
        throw new Error("Unimplemented method: interpret()");
    }
}

class False extends Node {
    toString() {
        return "false";
    }

    interpret(envt) {
        return false;
    }
}

class Var {
    constructor(name) {
        this.name = name;
    }
    
    toString() {
        return this.name;
    }

    interpret(envt) {
        return envt[this.name];
    }
}

class Num {
    constructor(val) {
        this.val = val;
    }
    
    toString() {
        return this.val.toString();
    }

    interpret(envt) {
        return this.val;
    }
}

class Plus {
    constructor(left, right) {
        this.left = left;
        this.right = right;
    }
    
    toString() {
        return "("+ this.left.toString() + "+" + this.right.toString()+")";
    }
    

    interpret(envt) {
        return this.left.interpret(envt) + this.right.interpret(envt);
    }
}

class Times {
    constructor(left, right) {
        this.left = left;
        this.right = right;
    }

    toString() {
        return "(" + this.left.toString() + "*" + this.right.toString() + ")";
    }

    interpret(envt) {
        return this.left.interpret(envt) * this.right.interpret(envt);
    }
}
    
class Lt {
    constructor(left, right) {
        this.left = left;
        this.right = right;
    }

    toString() {
        return "(" + this.left.toString() + "<" + this.right.toString() + ")";
    }

    interpret(envt) {
        return this.left.interpret(envt) < this.right.interpret(envt);
    }
}

class And {
    constructor(left, right) {
        this.left = left;
        this.right = right;
    }


    toString() {
        return "(" + this.left.toString() + "&&" + this.right.toString() + ")";
    }    

    interpret(envt) {
        return this.left.interpret(envt) && this.right.interpret(envt);
    }
}



class Not {
    constructor(left) {
        this.left = left;
    }
    
    toString() {
        return "(!" + this.left.toString()+ ")";
    }

    interpret(envt) {
        return !this.left.interpret(envt);
    }
}


class Ite {

    constructor(c, t, f) {
        this.cond = c;
        this.tcase = t;
        this.fcase = f;
    }
    
    toString() {
        return "(if " + this.cond.toString() + " then " + this.tcase.toString() + " else " + this.fcase.toString() + ")";
    }

    interpret(envt) {
        if (this.cond.interpret(envt)) {
            return this.tcase.interpret(envt);
        } else {
            return this.fcase.interpret(envt);
        }
    }

}
//Some functions you may find useful:

function randInt(lb, ub) {
    var rf = Math.random();
    rf = rf * (ub - lb) + lb;
    return Math.floor(rf);
}

function writeToConsole(text) {
    var csl = document.getElementById("console");
    if (typeof text == "string") {
        csl.value += text + "\n";
    } else {
        csl.value += text.toString() + "\n";
    }
}


function bottomUp(globalBnd, intOps, boolOps, vars, consts, inputoutputs) {
    var pListInt = [];
    var pListBool = [];

    // Add consts to terminals
    if(intOps.includes(NUM)){
        for(let i = 0; i < consts.length; i++){
            pListInt.push(new Num(consts[i]));
        }
    }

    // Add variables to terminals
    if(intOps.includes(VR)){
        for(let i = 0; i < vars.length; i++){
            pListInt.push(new Var(vars[i]));
        }
    }

    // Add "false" to terminals
    if(boolOps.includes(FALSE)){
        pListBool.push(new False());
    }

    // Generate string for correct output
    var ans = [];
    for(let j = 0; j < inputoutputs.length; j++){
        ans.push(inputoutputs[j]._out);
    }
    ans = ans.toString();

    // Populate "visited" sets with initial terminal evaluations
    var outputsInt = new Set();
    var outputsBool = new Set();
    for(let i = 0; i < pListInt.length; i++){
        var attempt = [];
        for(let j = 0; j < inputoutputs.length; j++){
            attempt.push(pListInt[i].interpret(inputoutputs[j]));
        }
        if(attempt.toString() == ans){
            return pListInt[i];
        }
        outputsInt.add(attempt.toString());
    }
    for(let i = 0; i < pListBool.length; i++){
        var attempt = [];
        for(let j = 0; j < inputoutputs.length; j++){
            attempt.push(pListBool[i].interpret(inputoutputs[j]));
        }
        outputsBool.add(attempt.toString());
    }

    // Bottom up explicit search
    var size = 1;
    while(size < globalBnd){
        size++;

        var intSize = pListInt.length;
        var boolSize = pListBool.length;

        // Grow pList

        // intOps
        if(intOps.includes(PLUS)){
            for(let a = 0; a < intSize; a++){
                for(let b = a; b < intSize; b++){
                    var plus = new Plus(pListInt[a], pListInt[b]);
                    
                    var attempt = [];
                    for(let j = 0; j < inputoutputs.length; j++){
                        attempt.push(plus.interpret(inputoutputs[j]));
                    }

                    // Matches answer: return
                    if(attempt.toString() == ans){
                        return plus;
                    }

                    // Add to "visited" set and pList
                    if(!outputsInt.has(attempt.toString())){
                        pListInt.push(plus);
                        outputsInt.add(attempt.toString());
                    }
                    
                }
            }
        }

        if(intOps.includes(TIMES)){
            for(let a = 0; a < intSize; a++){
                for(let b = a; b < intSize; b++){
                    var times = new Times(pListInt[a], pListInt[b]);
                    
                    var attempt = [];
                    for(let j = 0; j < inputoutputs.length; j++){
                        attempt.push(times.interpret(inputoutputs[j]));
                    }

                    // Matches answer: return
                    if(attempt.toString() == ans){
                        return times;
                    }

                    // Add to "visited" set and pList
                    if(!outputsInt.has(attempt.toString())){
                        pListInt.push(times);
                        outputsInt.add(attempt.toString());
                    }
                    
                }
            }
        }

        // Bool ops
        if(boolOps.includes(LT)){
            for(let a = 0; a < intSize; a++){
                for(let b = 0; b < intSize; b++){
                    var lt = new Lt(pListInt[a], pListInt[b]);
                    
                    var attempt = [];
                    for(let j = 0; j < inputoutputs.length; j++){
                        attempt.push(lt.interpret(inputoutputs[j]));
                    }

                    // Add to "visited" set and pList
                    if(!outputsBool.has(attempt.toString())){
                        pListBool.push(lt);
                        outputsBool.add(attempt.toString());
                    }
                    
                }
            }
        }

        if(boolOps.includes(AND)){
            for(let a = 0; a < boolSize; a++){
                for(let b = 0; b < boolSize; b++){
                    var and = new And(pListBool[a], pListBool[b]);
                    
                    var attempt = [];
                    for(let j = 0; j < inputoutputs.length; j++){
                        attempt.push(and.interpret(inputoutputs[j]));
                    }

                    // Add to "visited" set and pList
                    if(!outputsBool.has(attempt.toString())){
                        pListBool.push(and);
                        outputsBool.add(attempt.toString());
                    }
                }
            }
        }

        if(boolOps.includes(NOT)){
            for(let a = 0; a < boolSize; a++){
                var not = new Not(pListBool[a]);
                
                var attempt = [];
                for(let j = 0; j < inputoutputs.length; j++){
                    attempt.push(not.interpret(inputoutputs[j]));
                }

                // Add to "visited" set and pList
                if(!outputsBool.has(attempt.toString())){
                    pListBool.push(not);
                    outputsBool.add(attempt.toString());
                }
            }
        }

        // if statements
        if(intOps.includes(ITE)){
            for(let i = 0; i < boolSize; i++){
                for(let a = 0; a < intSize; a++){
                    for(let b = 0; b < intSize; b++){
                        var ite = new Ite(pListBool[i], pListInt[a], pListInt[b]);
                        
                        var attempt = [];
                        for(let j = 0; j < inputoutputs.length; j++){
                            attempt.push(ite.interpret(inputoutputs[j]));
                        }
    
                        // Matches answer: return
                        if(attempt.toString() == ans){
                            return ite;
                        }
    
                        // Add to "visited" set and pList
                        if(!outputsInt.has(attempt.toString())){
                            pListInt.push(ite);
                            outputsInt.add(attempt.toString());
                        }
                    }
                }
            }
        }
    }

	return "FAIL";
}


function bottomUpFaster(globalBnd, intOps, boolOps, vars, consts, inputoutputs){
    var pListInt = [];
    var pListBool = [];

    // Add consts to terminals
    if(intOps.includes(NUM)){
        for(let i = 0; i < consts.length; i++){
            pListInt.push(new Num(consts[i]));
        }
    }

    // Add variables to terminals
    if(intOps.includes(VR)){
        for(let i = 0; i < vars.length; i++){
            pListInt.push(new Var(vars[i]));
        }
    }

    // Add "false" to terminals
    if(boolOps.includes(FALSE)){
        pListBool.push(new False());
    }

    // Generate string for correct output
    var ans = [];
    for(let j = 0; j < inputoutputs.length; j++){
        ans.push(inputoutputs[j]._out);
    }
    ans = ans.toString();

    // Populate "visited" sets with initial terminal evaluations
    var outputsInt = new Set();
    var outputsBool = new Set();
    for(let i = 0; i < pListInt.length; i++){
        var attempt = [];
        for(let j = 0; j < inputoutputs.length; j++){
            attempt.push(pListInt[i].interpret(inputoutputs[j]));
        }
        if(attempt.toString() == ans){
            return pListInt[i];
        }
        outputsInt.add(attempt.toString());
    }
    for(let i = 0; i < pListBool.length; i++){
        var attempt = [];
        for(let j = 0; j < inputoutputs.length; j++){
            attempt.push(pListBool[i].interpret(inputoutputs[j]));
        }
        outputsBool.add(attempt.toString());
    }

    // Bottom up explicit search
    var size = 1;
    while(size < globalBnd){
        size++;

        var intSize = pListInt.length;
        var boolSize = pListBool.length;

        // Grow pList

        // if statements
        if(intOps.includes(ITE)){
            for(let i = 0; i < boolSize; i++){
                for(let a = 0; a < intSize; a++){
                    for(let b = 0; b < intSize; b++){
                        var ite = new Ite(pListBool[i], pListInt[a], pListInt[b]);
                        
                        var attempt = [];
                        for(let j = 0; j < inputoutputs.length; j++){
                            attempt.push(ite.interpret(inputoutputs[j]));
                        }
    
                        // Matches answer: return
                        if(attempt.toString() == ans){
                            return ite;
                        }
    
                        // Add to "visited" set and pList
                        if(!outputsInt.has(attempt.toString())){
                            pListInt.push(ite);
                            outputsInt.add(attempt.toString());
                        }
                    }
                }
            }
        }

        // intOps
        if(intOps.includes(PLUS)){
            for(let a = 0; a < intSize; a++){
                for(let b = a; b < intSize; b++){
                    var plus = new Plus(pListInt[a], pListInt[b]);
                    
                    var attempt = [];
                    for(let j = 0; j < inputoutputs.length; j++){
                        attempt.push(plus.interpret(inputoutputs[j]));
                    }

                    // Matches answer: return
                    if(attempt.toString() == ans){
                        return plus;
                    }

                    // Add to "visited" set and pList
                    if(!outputsInt.has(attempt.toString())){
                        pListInt.push(plus);
                        outputsInt.add(attempt.toString());
                    }
                    
                }
            }
        }

        if(size <= 2 && intOps.includes(TIMES)){ // Bound multiplication to only constant-variable, constant-constant, or variable-variable
            for(let a = 0; a < intSize; a++){
                for(let b = consts.length; b < intSize; b++){ // Bound multiplication to only constant-variable or variable-variable
                    var times = new Times(pListInt[a], pListInt[b]);
                    
                    var attempt = [];
                    for(let j = 0; j < inputoutputs.length; j++){
                        attempt.push(times.interpret(inputoutputs[j]));
                    }

                    // Matches answer: return
                    if(attempt.toString() == ans){
                        return times;
                    }

                    // Add to "visited" set and pList
                    if(!outputsInt.has(attempt.toString())){
                        pListInt.push(times);
                        outputsInt.add(attempt.toString());
                    }
                    
                }
            }
        }

        // Bool ops
        if(size <= 2 && boolOps.includes(LT)){ // Bound comparisons to only constants and variables
            for(let a = 0; a < intSize; a++){
                for(let b = 0; b < intSize; b++){
                    var lt = new Lt(pListInt[a], pListInt[b]);
                    
                    var attempt = [];
                    for(let j = 0; j < inputoutputs.length; j++){
                        attempt.push(lt.interpret(inputoutputs[j]));
                    }

                    // Add to "visited" set and pList
                    if(!outputsBool.has(attempt.toString())){
                        pListBool.push(lt);
                        outputsBool.add(attempt.toString());
                    }
                    
                }
            }
        }

        if(boolOps.includes(AND)){
            for(let a = 0; a < boolSize; a++){
                for(let b = 0; b < boolSize; b++){
                    var and = new And(pListBool[a], pListBool[b]);
                    
                    var attempt = [];
                    for(let j = 0; j < inputoutputs.length; j++){
                        attempt.push(and.interpret(inputoutputs[j]));
                    }

                    // Add to "visited" set and pList
                    if(!outputsBool.has(attempt.toString())){
                        pListBool.push(and);
                        outputsBool.add(attempt.toString());
                    }
                }
            }
        }

        if(boolOps.includes(NOT)){
            for(let a = 0; a < boolSize; a++){
                var not = new Not(pListBool[a]);
                
                var attempt = [];
                for(let j = 0; j < inputoutputs.length; j++){
                    attempt.push(not.interpret(inputoutputs[j]));
                }

                // Add to "visited" set and pList
                if(!outputsBool.has(attempt.toString())){
                    pListBool.push(not);
                    outputsBool.add(attempt.toString());
                }
            }
        }
    }

	return "FAIL";
}


function run1a1(){
	
	var rv = bottomUp(3, [VR, NUM, PLUS, TIMES, ITE], [AND, NOT, LT, FALSE], ["x", "y"], [4, 5], [{x:5,y:10, _out:5},{x:8,y:3, _out:3}]);
	writeToConsole("RESULT: " + rv.toString());
	
}


function run1a2(){
	
	var rv = bottomUp(3, [VR, NUM, PLUS, TIMES, ITE], [AND, NOT, LT, FALSE], ["x", "y"], [-1, 5], [
		{x:10, y:7, _out:17},
		{x:4, y:7, _out:-7},
		{x:10, y:3, _out:13},
		{x:1, y:-7, _out:-6},
		{x:1, y:8, _out:-8}		
		]);
	writeToConsole("RESULT: " + rv.toString());
	
}

function run1a3(){
	
	var rv = bottomUp(4, [VR, NUM, PLUS, TIMES, ITE], [AND, NOT, LT, FALSE], ["x", "y", "z"], [-1, 5], [
		{x:10, y:7, z: 7, _out:-1},
		{x:4, y:7, z: 7, _out:-1},
		{x:10, y:3, z: 3, _out:13},
		{x:1, y:-7, z: -2, _out:-6},
		{x:1, y:8, z: 0, _out:-8}		
		]);
	writeToConsole("RESULT: " + rv.toString());
	
}

function run1a4(){
	
	var rv = bottomUp(3, [VR, NUM, PLUS, TIMES, ITE], [AND, NOT, LT, FALSE], ["x", "y", "z"], [-1, 5], [
		{x:10, y:7, z: 7, _out:17},
		{x:4, y:7, z: 7, _out:-7},
		{x:10, y:3, z: 3, _out:90},
		{x:1, y:-7, z: -2, _out:14},
		{x:1, y:8, z: 0, _out:0}	
		]);
	writeToConsole("RESULT: " + rv.toString());
	
}

function run1b1(){
	
	var rv = bottomUpFaster(3, [VR, NUM, PLUS, TIMES, ITE], [AND, NOT, LT, FALSE], ["x", "y"], [4, 5], [{x:5,y:10, _out:5},{x:8,y:3, _out:3}]);
	writeToConsole("RESULT: " + rv.toString());
	
}


function run1b2(){
	
	var rv = bottomUpFaster(3, [VR, NUM, PLUS, TIMES, ITE], [AND, NOT, LT, FALSE], ["x", "y"], [-1, 5], [
		{x:10, y:7, _out:17},
		{x:4, y:7, _out:-7},
		{x:10, y:3, _out:13},
		{x:1, y:-7, _out:-6},
		{x:1, y:8, _out:-8}		
		]);
	writeToConsole("RESULT: " + rv.toString());
	
}

function run1b3(){
	
	var rv = bottomUpFaster(4, [VR, NUM, PLUS, TIMES, ITE], [AND, NOT, LT, FALSE], ["x", "y", "z"], [-1, 5], [
		{x:10, y:7, z: 7, _out:-10},
		{x:4, y:7, z: 7, _out:-4},
		{x:10, y:7, z: 3, _out:10},
		{x:1, y:-7, z: 8, _out:1},
		{x:1, y:-2, z: 0, _out:1}		
		]);
	writeToConsole("RESULT: " + rv.toString());
	
}

function run1b4(){
	
	var rv = bottomUpFaster(3, [VR, NUM, PLUS, TIMES, ITE], [AND, NOT, LT, FALSE], ["x", "y", "z"], [-1, 5], [
		{x:10, y:7, z: 7, _out:17},
		{x:4, y:7, z: 7, _out:-7},
		{x:10, y:3, z: 3, _out:90},
		{x:1, y:-7, z: -2, _out:14},
		{x:1, y:8, z: 0, _out:0}	
		]);
	writeToConsole("RESULT: " + rv.toString());
	
}




//Useful functions for exercise 2. 
//Not so much starter code, though.

function structured(inputoutputs){
	return "NYI";
}


function run2() {
    var inpt = JSON.parse(document.getElementById("input2").value);
    //This is the data from which you will synthesize.
    writeToConsole("You need to implement this");    
}


function genData() {
    //If you write a block of code in program1 that writes its output to a variable out,
    //and reads from variable x, this function will feed random inputs to that block of code
    //and write the input/output pairs to input2.
    var program = document.getElementById("program1").value
    function gd(x) {
        var out;
        eval(program);
        return out;
    }
    var textToIn = document.getElementById("input2");
    const BOUND = 500;
    const N = 10;
    textToIn.value = "[";
    for(var i=0; i<N; ++i){
        if(i!=0){ textToIn.textContent += ", "; }
        var inpt = randInt(0, BOUND);
        textToIn.value += "[" + inpt + ", " + gd(inpt) + "]";
        if(i!=(N-1)){
        	textToIn.value += ",";
        }
    }
    textToIn.value += "]";
}
