package nz.ac.auckland.lablet.experiment;

import android.graphics.PointF;

/**
 * Created by jdip004 on 25/08/2015.
 */
public class PointData extends MarkerData {
    private PointF positionReal = new PointF();

    public PointData(int frameId) {
        super(frameId);
    }

    public PointF getPosition() {
        return positionReal;
    }

    public void setPosition(PointF positionReal) {
        this.positionReal.set(positionReal);
    }
}