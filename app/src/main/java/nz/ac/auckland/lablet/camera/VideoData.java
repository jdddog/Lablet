/*
 * Copyright 2013-2014.
 * Distributed under the terms of the GPLv3 License.
 *
 * Authors:
 *      Clemens Zeidler <czei002@aucklanduni.ac.nz>
 */
package nz.ac.auckland.lablet.camera;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import nz.ac.auckland.lablet.camera.decoder.CodecOutputSurface;
import nz.ac.auckland.lablet.camera.decoder.FrameRenderer;
import nz.ac.auckland.lablet.camera.decoder.SeekToFrameExtractor;
import nz.ac.auckland.lablet.experiment.AbstractSensorData;
import nz.ac.auckland.lablet.experiment.FrameDataModel;
import nz.ac.auckland.lablet.experiment.IExperimentSensor;
import wseemann.media.FFmpegMediaMetadataRetriever;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import android.opengl.GLSurfaceView;

/**
 * Holds all important data for the camera experiment.
 */
public class VideoData extends AbstractSensorData {
    private String videoFileName;

    // milli seconds
    private long videoDuration;
    private int videoWidth;
    private int videoHeight;
    private int videoFrameRate;
//    private final SeekToFrameExtractor.IListener frameListenerStrongRef = new SeekToFrameExtractor.IListener() {
//        @Override
//        public void onFrameExtracted() {
//            int i = 1;
//            //seekFrameLatch.countDown();
//        }
//    };

    private float recordingFrameRate;
    SeekToFrameExtractor extractor;
    CodecOutputSurface outputSurface;
    //private final CountDownLatch seekFrameLatch = new CountDownLatch(1);


    static final public String DATA_TYPE = "Video";

    public VideoData(IExperimentSensor sourceSensor) {
        super(sourceSensor);
//        setWillNotDraw(false);
//        setEGLContextClientVersion(2);
//        setPreserveEGLContextOnPause(true);
    }

    @Override
    public String getDataType() {
        return "Video";
    }

    public VideoData() {
        super();
    }

    public float getMaxRawX() {
        return 100.f;
    }

    public float getMaxRawY() {
        float xToYRatio = (float)videoWidth / videoHeight;
        float xMax = getMaxRawX();
        return xMax / xToYRatio;
    }

    /*
    *   Gets Bitmap of video frame
     */

    public Bitmap getVideoFrame(long timeMicroSeconds)  {
        extractor.seekToFrameSync(timeMicroSeconds);
        return outputSurface.getBitmap();
    }

    public void saveFrame(Bitmap bmp)
    {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/screen_2.png");
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Converts coordinates in marker space into coordinates in video space.
     *
     * @param markerPos: Point in marker space
     * @return Coordinates in video space
     */

    public Point markerToVideoPos(PointF markerPos)
    {
        Point videoPos = new Point();
        int videoX = (int)(markerPos.x / this.getMaxRawX() * this.getVideoWidth());
        float ySwapedDir = this.getMaxRawY() - markerPos.y;
        int height = this.getVideoHeight();
        int videoY = (int)( ySwapedDir / this.getMaxRawY() * this.getVideoHeight());
        videoPos.set(videoX, videoY);
        return videoPos;
    }

    /**
     * Converts coordinates in video space into coordinates in marker space.
     *
     * @param videoPos: Point in video space
     * @return Coordinates in marker space
     */

    public PointF videoToMarkerPos(Point videoPos)
    {
        PointF markerPos = new PointF();
        float markerX = ((float)videoPos.x / (float)this.getVideoWidth()) * this.getMaxRawX();
        float markerY = this.getMaxRawY() - (((float)videoPos.y / (float)this.getVideoHeight()) * this.getMaxRawY());
        markerPos.set(markerX, markerY);
        return markerPos;
    }

    @Override
    public boolean loadExperimentData(Bundle bundle, File storageDir) {
        if (!super.loadExperimentData(bundle, storageDir))
            return false;

        setVideoFileName(storageDir, bundle.getString("videoName"));
        outputSurface = new CodecOutputSurface(this.getVideoWidth(), this.getVideoHeight());

        try {
            extractor = new SeekToFrameExtractor(this.getVideoFile(), outputSurface.getSurface());
//            extractor.setListener(new SeekToFrameExtractor.IListener() {
//                @Override
//                public void onFrameExtracted() {
//                    Bitmap bmp = outputSurface.getBitmap();
//                    saveFrame(bmp);
//                }
//            });
//            frameListenerStrongRef = new SeekToFrameExtractor.IListener() {
//                @Override
//                public void onFrameExtracted() {
//                    seekFrameLatch.countDown();
//                }
//            };

//            extractor.setListener(frameListenerStrongRef);


//            FrameRenderer frameRenderer = new FrameRenderer(outputSurface, 0);
//            setRenderer(frameRenderer);
//            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordingFrameRate = bundle.getFloat("recordingFrameRate", -1);
        return true;
    }

    @Override
    protected Bundle experimentDataToBundle() {
        Bundle bundle = super.experimentDataToBundle();

        bundle.putString("videoName", videoFileName);

        if (recordingFrameRate > 0)
            bundle.putFloat("recordingFrameRate", recordingFrameRate);

        return bundle;
    }

    /**
     * Set the file name of the taken video.
     *
     * @param storageDir directory where the video file is stored
     * @param fileName path of the taken video
     */
    public void setVideoFileName(File storageDir, String fileName) {
        this.videoFileName = fileName;

        String videoFilePath = new File(storageDir, fileName).getPath();
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(videoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < extractor.getTrackCount(); i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);

            if (mime.startsWith("video/")) {
                extractor.selectTrack(i);

                videoDuration = format.getLong(MediaFormat.KEY_DURATION) / 1000;
                videoWidth = format.getInteger(MediaFormat.KEY_WIDTH);
                videoHeight = format.getInteger(MediaFormat.KEY_HEIGHT);
                if (format.containsKey(MediaFormat.KEY_FRAME_RATE))
                    videoFrameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE);
                if (videoFrameRate == 0)
                    videoFrameRate = 30;
                break;
            }
        }
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    /**
     * Gets the complete path of the video file.
     *
     * @return the path of the taken video
     */
    public File getVideoFile() {
        return new File(getStorageDir(), getVideoFileName());
    }

    /**
     * The complete duration of the recorded video.
     *
     * @return the duration of the recorded video
     */
    public long getVideoDuration() {
        return videoDuration;
    }

    final static int LOW_FRAME_RATE = 10;

    public boolean isRecordedAtReducedFrameRate() {
        return recordingFrameRate < LOW_FRAME_RATE;
    }

    public void setRecordingFrameRate(float recordingFrameRate) {
        this.recordingFrameRate = recordingFrameRate;
    }

    public float getRecordingFrameRate() {
        return recordingFrameRate;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public int getVideoFrameRate() {
        return videoFrameRate;
    }
}
