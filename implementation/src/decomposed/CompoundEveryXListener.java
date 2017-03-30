
package decomposed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author s132054
 */
public class CompoundEveryXListener implements Listener<Map<String, Integer>> {

    private Map<String, EveryXListener> listeners = new HashMap<>();

    @Override
    public void onEvent() {
        for (EveryXListener listener : listeners.values()) {
            listener.onEvent();
        }
    }
    
    public CompoundEveryXListener add(String s, EveryXListener listener) {
        listeners.put(s, listener);
        return this;
    }
    
    public CompoundEveryXListener remove(String s) {
        listeners.remove(s);
        return this;
    }

    @Override
    public Map<String, Integer> report() {
        return listeners.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().report()));
    }
}
