/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package php.agavi.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author mle
 */
public class TokenReplacingPrintStream extends PrintStream implements TokenReplacer {
    
    private HashMap<String, String> tokens;

    public TokenReplacingPrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    public TokenReplacingPrintStream(File file) throws FileNotFoundException {
        super(file);
    }

    public TokenReplacingPrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public TokenReplacingPrintStream(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public TokenReplacingPrintStream(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public TokenReplacingPrintStream(OutputStream out) {
        super(out);
    }

    public void setTokens(HashMap<String, String> xmlTokens) {
        this.tokens = xmlTokens;
    }

    @Override
    public void println(String x) {
        
        Set<String> tokenKeys = this.tokens.keySet();
        System.out.println("INPUT:" + x);
        
        for (String key : tokenKeys) {
            x = replaceToken(x, key, tokens.get(key));
        }
        
        System.out.println("OUTPUT:" + x);
        
        super.println(x);
    }

    @Override
    public String replaceToken(String input, String token, String replace) {

        return input.replace(token, replace);
    
    }
    
    

}
