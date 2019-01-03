package cc.blynk.server.exp4j.tokenizer;

/**
 * Represents an argument separator in functions i.e: ','
 */
class ArgumentSeparatorToken extends Token {
    /**
     * Create a new instance
     */
    ArgumentSeparatorToken() {
        super(TOKEN_SEPARATOR);
    }
}
