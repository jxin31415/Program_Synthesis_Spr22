game = fun[(a==1 || a==2 || a==3)] {
    sum = 0
    iter = 0

    while sum < 21 [(a<(sum-a)) && (a==1 || a==2 || a==3)] {
        b = (4-a)

        sum = sum + a

        assert sum >= 21 || sum + b < 21
        
        sum = sum + b
        iter = iter + 1
    }
}
