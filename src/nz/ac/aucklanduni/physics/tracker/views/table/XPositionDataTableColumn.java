/*
 * Copyright 2013-2014.
 * Distributed under the terms of the GPLv3 License.
 *
 * Authors:
 *      Clemens Zeidler <czei002@aucklanduni.ac.nz>
 */
package nz.ac.aucklanduni.physics.tracker.views.table;

import nz.ac.aucklanduni.physics.tracker.ExperimentAnalysis;
import nz.ac.aucklanduni.physics.tracker.MarkersDataModel;


public class XPositionDataTableColumn extends DataTableColumn {
    @Override
    public int size() {
        return markersDataModel.getMarkerCount();
    }

    @Override
    public Number getValue(int index) {
        return markersDataModel.getCalibratedMarkerPositionAt(index).x;
    }

    @Override
    public String getHeader() {
        return "x [" + experimentAnalysis.getXUnit() + "]";
    }
}