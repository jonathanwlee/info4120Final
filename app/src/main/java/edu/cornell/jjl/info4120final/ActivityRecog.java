package edu.cornell.jjl.info4120final;

/**
 * Created by jjl on 4/25/2016.
 */
public class ActivityRecog {
    protected int still;
    protected int foot;
    protected int walking;
    protected int running;
    protected int bicycle;
    protected int vehicle;
    protected int tilting;

    protected int unknown;

    public ActivityRecog(int still, int foot, int walking, int running, int bicycle, int vehicle, int tilting, int unknown) {
        this.still = still;
        this.foot = foot;
        this.walking = walking;
        this.running = running;
        this.bicycle = bicycle;
        this.tilting = tilting;
        this.vehicle = vehicle;
        this.unknown = unknown;
    }
}
