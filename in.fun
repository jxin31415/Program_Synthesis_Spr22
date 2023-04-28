// Satisfiable, unverified
f = fun [out < 10] { // Brackets denote an assumed precondition
   out = 0
   while (it >= 0) [out >= 0] { // Brackets denote an estimated loop invariant
       out = out + it
       it = it - 1
   }

   assert out >= 0 // Asserts serve as the postcondition
   assert it == -1
}


// Satisfiable, unverified
g = fun [x > 0] {
    x = x + 3
    y = 2

    if y >= 2
        x = x + 2
    else
        x = x - 2
    
    assert x > 500
}

// Satisfiable and valid
h = fun {
    i = 1
    sum = 0
    while i <= it [sum >= 0] {
        j = 1
        while j <= i [sum >= 0 && j >= 0] {
            sum = sum + j
            j = j + 1
        }
        i = i + 1
    }

    assert sum >= 0
}
