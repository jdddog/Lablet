/*
 * Copyright 2013-2014.
 * Distributed under the terms of the GPLv3 License.
 *
 * Authors:
 *      Clemens Zeidler <czei002@aucklanduni.ac.nz>
 */
package nz.ac.auckland.lablet.views.table;


/**
 * Table column for the marker data table adapter. Provides the x-acceleration.
 */
public class XAccelerationDataTableColumn extends DataTableColumn {
    @Override
    public int size() {
        return dataModel.getMarkerCount() - 2;
    }

    @Override
    public Number getValue(int index) {
        float speed0 = XSpeedDataTableColumn.getSpeed(index, dataModel, timeCalibration).floatValue();
        float speed1 = XSpeedDataTableColumn.getSpeed(index + 1, dataModel, timeCalibration).floatValue();
        float delta = speed1 - speed0;

        float deltaT = (timeCalibration.getTimeFromRaw(index + 2) - timeCalibration.getTimeFromRaw(index)) / 2;
        if (timeCalibration.getUnit().getPrefix().equals("m"))
            deltaT /= 1000;

        return delta / deltaT;
    }

    @Override
    public String getHeader() {
        return "acceleration [" + dataModel.getCalibrationXY().getXUnit().getUnit() + "/"
                + timeCalibration.getUnit().getBase() + "^2]";
    }
}
