# Example of calling a method on a ruby object
# (without defining that method inside a class)


def fibonacci(n)
    if n == 0 or n == 1
        1
    else
        fibonacci(n - 1) + fibonacci(n-2)
    end
end