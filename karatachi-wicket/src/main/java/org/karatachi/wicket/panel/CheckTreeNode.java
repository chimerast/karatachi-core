package org.karatachi.wicket.panel;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.karatachi.collections.TreeNode;

public class CheckTreeNode<T extends Serializable & Comparable<? super T>>
        extends TreeNode<T> {
    private static final long serialVersionUID = 1L;

    private boolean check = false;
    private boolean open = false;
    private boolean visible = true;
    private boolean leaf = false;

    public CheckTreeNode(T value) {
        super(value);
    }

    @Override
    public String toString() {
        return "CheckTreeNode(value='" + getValue() + "')";
    }

    public boolean isCheck() {
        return check;
    }

    public CheckTreeNode<T> setCheck(boolean check) {
        checkModifiable();
        this.check = check;
        return this;
    }

    public boolean isOpen() {
        return open;
    }

    public CheckTreeNode<T> setOpen(boolean open) {
        checkModifiable();
        this.open = open;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public CheckTreeNode<T> setVisible(boolean visible) {
        checkModifiable();
        this.visible = visible;
        return this;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public CheckTreeNode<T> setLeaf(boolean leaf) {
        checkModifiable();
        this.leaf = leaf;
        return this;
    }

    public boolean hasVisibleChild() {
        for (TreeNode<T> child : getChildren()) {
            if (((CheckTreeNode<T>) child).isVisible()) {
                return true;
            }
        }
        return false;
    }

    public void select(final Selector<T> selector) {
        if (selector == null) {
            accept(new Visitor<T>() {
                @Override
                public boolean visit(CheckTreeNode<T> node) {
                    node.setOpen(false);
                    node.setVisible(true);
                    return true;
                }
            });
        } else {
            accept(new Visitor<T>() {
                @Override
                public boolean visit(CheckTreeNode<T> node) {
                    node.setOpen(false);
                    node.setVisible(false);
                    return true;
                }
            });

            accept(new Visitor<T>() {
                @Override
                public boolean visit(CheckTreeNode<T> node) {
                    if (selector.select(node)) {
                        open(node);
                        node.accept(new Visitor<T>() {
                            @Override
                            public boolean visit(CheckTreeNode<T> node) {
                                node.setOpen(true);
                                node.setVisible(true);
                                return true;
                            }
                        });
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public void down() {
                    selector.down();
                }

                @Override
                public void up() {
                    selector.up();
                }

                private void open(CheckTreeNode<T> node) {
                    node.setOpen(true);
                    node.setVisible(true);
                    for (TreeNode<T> parent : node.getParents()) {
                        open((CheckTreeNode<T>) parent);
                    }
                }
            });
        }
    }

    public void accept(Visitor<T> visitor) {
        visitor.down();
        if (visitor.visit(this)) {
            for (TreeNode<T> child : getChildren()) {
                ((CheckTreeNode<T>) child).accept(visitor);
            }
        }
        visitor.up();
    }

    public static abstract class Visitor<S extends Serializable & Comparable<? super S>> {
        public abstract boolean visit(CheckTreeNode<S> node);

        public void down() {
        }

        public void up() {
        }
    }

    public static abstract class Selector<S extends Serializable & Comparable<? super S>> {
        public abstract boolean select(CheckTreeNode<S> node);

        public void down() {
        }

        public void up() {
        }
    }

    public static abstract class Acceptor<S extends Serializable & Comparable<? super S>, R>
            extends Selector<S> {
        private R[] criterias;
        private Set<Integer> sets = new TreeSet<Integer>();
        private LinkedList<List<Integer>> acceptor =
                new LinkedList<List<Integer>>();

        public Acceptor(R[] criterias) {
            this.criterias = criterias;

            for (int i = 0; i < criterias.length; ++i) {
                sets.add(i);
            }
        }

        public abstract boolean accept(S value, R criteria);

        @Override
        public final boolean select(CheckTreeNode<S> node) {
            acceptor.get(0).clear();
            for (int i = 0; i < criterias.length; ++i) {
                if (accept(node.getValue(), criterias[i])) {
                    acceptor.get(0).add(i);
                }
            }
            return accepted();
        }

        private boolean accepted() {
            List<Integer> all = new ArrayList<Integer>();
            for (List<Integer> l : acceptor) {
                all.addAll(l);
            }
            return all.containsAll(sets);
        }

        @Override
        public final void down() {
            acceptor.push(new ArrayList<Integer>());
        }

        @Override
        public final void up() {
            acceptor.pop();
        }
    }

    /**
     * ツリーのshallow copyを生成する
     */
    public static <S extends Serializable & Comparable<? super S>> CheckTreeNode<S> duplicateTree(
            TreeNode<S> node) {
        return duplicateTree(node, new NodeFactory<S, CheckTreeNode<S>>() {
            public CheckTreeNode<S> newInstance(S value) {
                return new CheckTreeNode<S>(value);
            };
        });
    }

    /**
     * ツリーのdeep copyを生成する
     */
    public static <S extends Serializable & Comparable<? super S>> CheckTreeNode<S> duplicateTreeDeep(
            TreeNode<S> node) {
        return duplicateTree(node, new NodeFactory<S, CheckTreeNode<S>>() {
            public CheckTreeNode<S> newInstance(S value) {
                return new CheckTreeNode<S>(cloneNodeValue(value));
            };
        });
    }

    /**
     * ツリーのshallow copyを生成する（ノードの重複を許す）
     */
    public static <S extends Serializable & Comparable<? super S>> CheckTreeNode<S> duplicateTreeFlat(
            TreeNode<S> node) {
        return duplicateTreeFlat(node, new NodeFactory<S, CheckTreeNode<S>>() {
            public CheckTreeNode<S> newInstance(S value) {
                return new CheckTreeNode<S>(value);
            };
        });
    }

    /**
     * ノードの値のclone()を行う
     */
    @SuppressWarnings("unchecked")
    private static <S extends Serializable & Comparable<? super S>> S cloneNodeValue(
            S value) {
        try {
            Method clone = value.getClass().getDeclaredMethod("clone");
            clone.setAccessible(true);
            return (S) clone.invoke(value);
        } catch (Exception e) {
            throw new IllegalStateException("cannot clone value.");
        }
    }
}
