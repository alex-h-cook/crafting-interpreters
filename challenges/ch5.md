## Q1.
```
expr -> expr ( "(" ( expr ("," expr)* )? ")" | "." IDENTIFIER )+
    | IDENTIFIER
    | NUMBER
```
    
Grammar that matches the same language but does not use any of the notational sugar "*" and "|".

Without "?", we need to write another production for the rule where the optional thing is included / not included.
Without "|", we have to repeat the rule name each time we add another production for it.
Without "*", we have to use recursion to represent potentially infinitely repeating productions (zero is allowed).
Without "+", we have to use recursion to represent (at least 1 required).

```
expr -> expr ( "(" ")" )
expr -> expr ( "(" ( expr ) ")" )
expr -> expr ( "(" ( expr ("," expr)* )? ")" ) #stuck
expr -> expr ( "." IDENTIFIER )+

```

### Questions
Stuck on how to use recursion to represent the asterisk.