package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0; // Points to the first character of the lexeme being scanned
    private int current = 0; // Points to the current character being scanned 
    private int line = 1; // Allows us to produce tokens that know their location
    private static final Map<String, TokenType> keywords;

    static {
        // Static block executes once - when the class is loaded into memory
        keywords = new HashMap<>();
        keywords.put("and",     AND);
        keywords.put("class",   CLASS);
        keywords.put("else",    ELSE);
        keywords.put("false",   FALSE);
        keywords.put("for",     FOR);
        keywords.put("fun",     FUN);
        keywords.put("if",      IF);
        keywords.put("nil",     NIL);
        keywords.put("or",      OR);
        keywords.put("print",   PRINT);
        keywords.put("return",  RETURN);
        keywords.put("super",   SUPER);
        keywords.put("this",    THIS);
        keywords.put("true",    TRUE);
        keywords.put("var",     VAR);
        keywords.put("while",   WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': 
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance(); // For comments, keep consuming characters until we recah the line end
                } else if (match('*')) {
                    int comment_layer = 1; // Block comments with support for nesting
                    while (comment_layer > 0 && !isAtEnd()) {
                        if (peek() == '*' && peekNext() == '/') {
                            --comment_layer;
                            advance();
                            advance();
                        } else if (peek() == '/' && peekNext() == '*') {
                            ++comment_layer;
                            advance();
                            advance();
                        } else {
                            if (peek() == '\n') line++;
                            advance();
                        }
                    }
                    if (isAtEnd() && comment_layer > 0) {
                        Lox.error(line, "Unterminated block comment.");
                    }
                    // Below is my implementation for block comments without nesting support (this is wrong because it forgets to handle new lines.)
                    // while !((peek() == '*' && peekNext() == '/') || isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                // This is why we use peek() to look for newline ending a comment instead of match(), we need to be able to come here and increment `line`
                // QUESTION: when we break from comment scanning, the '\n'character gets skipped by advance(), no?
                // ANSWER: it does not, peek() looks at the current character without consuming it. So we will next correctly come here to increment line.
                line++;
                break;
            
            case '"': string(); break;
                
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        // ^ Identifiers can start with only alpha characters, but afterwards can contain numbers
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER; // Check against keywords (identifiers reserved by the language)
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();
        // Look for fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER,
            Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        // Consume characters until we hit the " that ends the string
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance(); // Move past the closing ".
    
        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        // Works like a conditional advance - only consumes the character if it's what we're looking for
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        //QUESTION: how is this lookahead if we look at the current char, and not current+1
        //ANSWER: java has two types of incrementation operations, i++ and ++i
        //i++ increments the value only AFTER evaluating the expression.
        if (isAtEnd()) return '\0';
        return source.charAt(current); //Lookahead: like advance(), but doesn't consume the character
    }

    private char peekNext() {
        // Our scanner looks ahead at most two characters
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char advance() {
        return source.charAt(current++); //Increments `current` AFTER evaluating it
    }

    private void addToken(TokenType type) {
        // Java doesn't have optional parameters, so we have to use overloading
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
