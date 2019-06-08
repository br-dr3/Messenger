package com.github.brdr3.mavenmessenger.core.util;

public class Triplet<T, T0, T1> {
    T x;
    T0 y;
    T1 z;
    
    public Triplet(T x, T0 y, T1 z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public T getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x;
    }

    public T0 getY() {
        return y;
    }

    public void setY(T0 y) {
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "(" + x.toString() + ", " 
                   + y.toString() + ", "
                   + z.toString() + ")";
    }
}
