package towson.cosc519.group6.ui;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Static class with some common utilities
 */
public class Utils {
    // Static only class
    private Utils() {}

    /**
     * Finds the first descendant which matches a specific class
     *
     * @param ancestor          Parent element
     * @param descendantType    Node to find
     * @return
     */
    public static <N extends Node> N findFirst(Parent ancestor, Class<N> descendantType) {
       return findFirst(ancestor, ancestor, descendantType);
    }

    @SuppressWarnings("unchecked")
    private static <N extends Node> N findFirst(Parent ancestor, Node cur, Class<N> descendantType) {
        if (ancestor != cur && descendantType.isAssignableFrom(cur.getClass())) {
            return (N) cur;
        }

        if (Parent.class.isAssignableFrom(cur.getClass())) {
            for (Node child : ((Parent) cur).getChildrenUnmodifiable()) {
                Node node = findFirst(ancestor, child, descendantType);
                if (node != null) {
                    return (N) node;
                }
            }

            return null;
        }

        return null;
    }
}
