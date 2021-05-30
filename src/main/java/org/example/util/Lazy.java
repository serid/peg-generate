package org.example.util;

/**
 * Represents a value that will be accessible later.
 * Main use case for this class is implementation of cyclic pure data structures. (see {@link LazyExample})
 * In general creating a cyclic data structure requires mutability,
 * this class allows to encapsulate mutable field into an object and keep the field "final".
 * <p>
 * Program should be structured as to not access a lazy value before it is set.
 *
 * @param <T> inner value type
 */
public final class Lazy<T> {
    private T value;

    public Lazy() {
        this.value = null;
    }

    public Lazy(T value) {
        this.value = value;
    }

    public T get() {
        assert value != null;
        return value;
    }

    public void resolve(T value) {
        assert this.value == null;
        this.value = value;
    }

    private static class LazyExample {
        private LazyExample() {
        }

        public static void test() {
            var lazy = new Lazy<Directory>();

            // cannot refer to "root" variable while it is being initialized hence the use of "Lazy"
            var root = new Directory("root", null,
                    new Directory("a", lazy, null, null),
                    new Directory("b", lazy, null, null));
            lazy.resolve(root);

            System.out.println(root.name);
            System.out.println(root.child1.name);
            System.out.println(root.child2.name);
            System.out.println(root.child1.parent.get().name);
            System.out.println(root.child2.parent.get().name);
        }

        // Immutable directory tree node (note all fields are final)
        private static class Directory {
            public final String name;
            public final Lazy<Directory> parent;
            public final Directory child1;
            public final Directory child2;

            public Directory(String name, Lazy<Directory> parent, Directory child1, Directory child2) {
                this.name = name;
                this.parent = parent;
                this.child1 = child1;
                this.child2 = child2;
            }
        }
    }
}
