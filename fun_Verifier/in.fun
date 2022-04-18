fact = fun {
   out = 0
   while (it >= 0) [out > 0] {
       out = out + it
       it = it - 1
   }

   assert out == 5
}

g = fun {
    x = x + 3
    y = 2

    if y >= 2
        x = x + 2
    else
        x = x - 2
    
    assert x > 5
}
/*
f = fun {
    //assume(x == 3)
    print(it + it-10 + it-100)
    if it&&1
        if it&&2
            x = 2
        else
            y = 3

    x = it == 100
    x = it >= 100
    x = it != 100
    x = it <= 100
    x = it > 100
    x = it < 100
    x = a || b
    x = a && b
    x = !a
    x = -3

    assert(x == 2)
}

/*
g = fun {
  print(it + it*10 + it*100)
  if it&1 {
    if it&2
      print 2
  } else
      print 3
}

while 0
    print 666

x = 5
while x > 0 {
    print x
    x = x - 1
}


a = f(0)
a = f(1)
a = f(2)
a = f(3)

a = g(0)
a = g(1)
a = g(2)
a = g(3)
*/