package org.yunghegel.gdx.gizmo.graph;

import com.badlogic.gdx.math.Matrix4;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Spatial extends SpatialNode<Spatial> implements Iterable<Spatial>{

    Spatial child;

    public Spatial(Matrix4 transform) {
        super(transform);
    }

    @Override
    public Iterator<Spatial> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super Spatial> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Spatial> spliterator() {
        return Iterable.super.spliterator();
    }
}
