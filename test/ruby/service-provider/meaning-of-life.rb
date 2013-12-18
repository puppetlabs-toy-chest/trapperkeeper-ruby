# This is a ruby script that implements a trapperkeeper service function.
# You can put arbitrary ruby code here, but the value of the last statement
# is what will be returned back to the clojure service wrapper
# as the return value for the service function.

for x in 0..10
    if x == 7
        puts "Computing the meaning of life..."
    end
end

# Numeric values will automatically get converted to the appropriate
# java object back in clojure-land
42
