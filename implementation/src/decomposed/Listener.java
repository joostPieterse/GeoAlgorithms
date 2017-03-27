/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decomposed;

/**
 *
 * @author s132054
 */
public interface Listener<T> {
    
    public void onEvent();
    
    public T report();
    
}
