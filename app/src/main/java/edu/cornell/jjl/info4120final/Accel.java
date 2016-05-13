package edu.cornell.jjl.info4120final;

/**
 * Accelerometer object to hold accelerometer values from smartphone output.
 */
public class Accel {
    protected float x;
    protected float y;
    protected float z;

    /**
     * Constructor to create an accel object from accelerometer values.
     * @param x
     * @param y
     * @param z
     */
    public Accel(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
