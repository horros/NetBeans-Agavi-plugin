/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package php.agavi.builder;

/**
 *
 * @author mle
 */
public interface TokenReplacer {
    
    public String replaceToken(String input, String token, String replace);

}
