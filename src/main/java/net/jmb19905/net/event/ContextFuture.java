package net.jmb19905.net.event;

import java.util.ArrayList;
import java.util.List;

public class ContextFuture<C> {
    private final List<Listener<C>> listeners = new ArrayList<>();
    private final C context;

    public ContextFuture(C context) {
        this.context = context;
    }

    public void addListener(Listener<C> listener) {
        listeners.add(listener);
    }

    public void perform() {
        for(Listener<C> l : listeners) l.perform(context);
    }

    public interface Listener<C> {
        void perform(C context);
    }

}