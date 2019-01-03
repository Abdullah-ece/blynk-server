package cc.blynk.server.exp4j.tokenizer;

/**
 * represents closed parentheses
 */
class CloseParenthesesToken extends Token {

    /**
     * Create a new instance
     */
    CloseParenthesesToken() {
        super(TOKEN_PARENTHESES_CLOSE);
    }
}
