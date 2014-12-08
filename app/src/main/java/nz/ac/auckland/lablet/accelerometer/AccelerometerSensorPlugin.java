/*
 * Copyright 2014.
 * Distributed under the terms of the GPLv3 License.
 *
 * Authors:
 *      Clemens Zeidler <czei002@aucklanduni.ac.nz>
 */
package nz.ac.auckland.lablet.accelerometer;

import android.content.Context;
import android.os.Bundle;
import nz.ac.auckland.lablet.experiment.*;

import java.io.File;
import java.io.IOException;


public class AccelerometerSensorPlugin implements ISensorPlugin {

    @Override
    public String getSensorName() {
        return AccelerometerExperimentSensor.SENSOR_NAME;
    }

    @Override
    public IExperimentSensor createExperimentSensor() {
        return new AccelerometerExperimentSensor();
    }

    @Override
    public ISensorData loadSensorData(Context context, Bundle data, File storageDir) {
        ISensorData sensorData = new AccelerometerSensorData(context);
        try {
            sensorData.loadExperimentData(data, storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return sensorData;
    }
}

