## Q1.
```
expr -> expr ( "(" ( expr ("," expr)* )? ")" | "." IDENTIFIER )+
    | IDENTIFIER
    | NUMBER
```
    
Produce a grammar that matches the same language but does not use any of the notational sugar "*" and "|".

Without "?", we need to write another production for the rule where the optional thing is included / not included.
Without "|", we have to repeat the rule name each time we add another production for it.
Without "*", we have to use recursion to represent potentially infinitely repeating productions (zero is allowed).
Without "+", we have to use recursion to represent repeating productions (at least 1 required).

```
# The challenge is in defining these rules
expr -> expr ( "(" ( expr ("," expr)* )? ")" )+
    expr -> expr "(" ")"
    expr -> expr "(" expr ")"
    expr -> expr "(" expr "," expr ")"
    expr -> expr "(" expr "," expr "," expr ")"
    ...
    expr -> expr "(" ")" "(" ")"
    expr -> expr "(" expr ")" "(" ")"
    ...
 
expr -> expr ( "." IDENTIFIER )+
    expr -> expr "." IDENTIFIER
    expr -> expr "." IDENTIFIER "." IDENTIFIER
    expr -> expr "." IDENTIFIER "." IDENTIFIER "." IDENTIFIER
    ...

# Stuck: how do we ensure we don't get left with hanging commas? expr-_list -> "" is not compatible with expr_list "," expr_list
# Solution: I added extend expr_list separately

# Another stuck: realised I didn't account for mixed chaining of methods and attributes. Solved below.


# My overall solution:

extend_expr_list -> ""
extend_expr_list -> "," expr_list 
expr_list -> ""
expr_list -> expr
expr_list -> expr extend_expr_list

extend_method_chain -> ""
extend_method_chain -> method_chain
method_chain -> "(" expr_list ")" extend_method_chain

extend_attribute_chain -> ""
extend_attribute_chain -> "." attribute_chain
attribute_chain -> "." IDENTIFIER extend_attribute_chain

extend_member_chain -> ""
extend_member_chain -> method_chain extend_member_chain
extend_member_chain -> attribute_chain extend_member_chain
member_chain -> method_chain extend_member_chain
member_chain -> attribute_chain extend_member_chain

expr -> expr member_chain 
expr -> IDENTIFER
expr -> NUMBER

```

### Bonus: What kind of expression does this bit of grammar encode?
It seems to encode chained function calling and attribute/property accessing. 

I have a little confusion around:
- It doesn't seem to support mixing attribute accessing with method calling, it seems to only allow 

**I have failed to realise that you can mix and match the attribute access and method calling. They are both inside the same '+', so one can follow the other.**

Solution: I added member chain which is a combination of the method and attribute chains 

So overall the grammar encodes methods and attributes. For methods the only allowed arguments are numbers or identifiers.

### Issues

This allows leaving trailing commas in function calls
This allows double dots in attribute/property access
Member chain is maybe not accurate naming. Call it function / property chain

### Questions
Stuck on how to use recursion to represent the asterisk and + without writing an infinite number of productions.