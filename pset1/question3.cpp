#include <cstdio>
#include <assert.h>
#include <iostream>
using namespace std;
#include "vops.h"
#include "question3.h"
namespace ANONYMOUS{

void experiment2__Wrapper() {
  experiment2();
}
void experiment2__WrapperNospec() {}
void experiment1__Wrapper() {
  experiment1();
}
void experiment1__WrapperNospec() {}
void experiment2() {
  int  _out_s2=0;
  wrapper2(10, 7, _out_s2);
  assert ((_out_s2) == (17));;
  int  _out_s4=0;
  wrapper2(4, 7, _out_s4);
  assert ((_out_s4) == (-7));;
  int  _out_s6=0;
  wrapper2(10, 3, _out_s6);
  assert ((_out_s6) == (13));;
  int  _out_s8=0;
  wrapper2(1, -7, _out_s8);
  assert ((_out_s8) == (-6));;
  int  _out_s10=0;
  wrapper2(1, 8, _out_s10);
  assert ((_out_s10) == (-8));;
}
void experiment1() {
  int  _out_s29=0;
  wrapper1(5, 5, _out_s29);
  assert ((_out_s29) == (15));;
  int  _out_s31=0;
  wrapper1(8, 3, _out_s31);
  assert ((_out_s31) == (14));;
  int  _out_s33=0;
  wrapper1(1234, 227, _out_s33);
  assert ((_out_s33) == (1688));;
}
void wrapper2(int x, int y, int& _out) {
  int  a_s14=0;
  if ((x) < (y)) {
    a_s14 = -1;
  } else {
    a_s14 = 1;
  }
  int  b_s16=0;
  if ((y) < (x)) {
    b_s16 = x + y;
  } else {
    b_s16 = y;
  }
  _out = a_s14 * b_s16;
  return;
}
void wrapper1(int x, int y, int& _out) {
  int  b_s16=y + (x + y);
  _out = b_s16;
  return;
}

}
