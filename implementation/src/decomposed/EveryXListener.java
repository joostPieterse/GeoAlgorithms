
package decomposed;

import java.util.function.Consumer;


/**
 *
 * @author s132054
 */
public class EveryXListener implements Listener<Integer> {
    
    private final int x;
    private int i = 0;
    protected Consumer<Integer> consumer;
    
    public EveryXListener(int x, Consumer<Integer> consumer) {
        this.x = x;
        this.consumer = consumer;
    }
    
    @Override
    public void onEvent() {
        if (++i % x == 0) {
            consumer.accept(i / x);
        }
    }

    @Override
    public Integer report() {
        return i;
    }
}
