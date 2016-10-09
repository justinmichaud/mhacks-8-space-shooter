package com.justinmichaud.libgdxcardboard.desktop;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.sarxos.webcam.Webcam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import boofcv.abst.fiducial.FiducialDetector;
import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.alg.geo.PerspectiveOps;
import boofcv.alg.geo.WorldToCameraToPixel;
import boofcv.factory.fiducial.ConfigFiducialBinary;
import boofcv.factory.fiducial.FactoryFiducial;
import boofcv.factory.filter.binary.ConfigThreshold;
import boofcv.factory.filter.binary.ThresholdType;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.webcamcapture.UtilWebcamCapture;
import boofcv.struct.calib.IntrinsicParameters;
import boofcv.struct.image.GrayU8;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point3D_F64;
import georegression.struct.se.Se3_F64;
import georegression.struct.shapes.Quadrilateral_F64;

public class BoofProcessor extends Thread {

    private Webcam camera;
    private FiducialDetector<GrayU8> detector;
    private IntrinsicParameters intrinsicParameters;
    private ImagePanel gui;
    private int trackerFrames = 0;
    private boolean isHeadVisible = false;

    private TrackerObjectQuad<GrayU8> tracker;
    private Quadrilateral_F64 location;

    private final Vector3 position = new Vector3();

    public BoofProcessor() {
        camera = UtilWebcamCapture.openDefault(1920, 1080);

        intrinsicParameters = new IntrinsicParameters();
        intrinsicParameters.setCx(camera.getViewSize().getWidth()/2f);
        intrinsicParameters.setCy(camera.getViewSize().getHeight()/2f);
        intrinsicParameters.setFx(1);
        intrinsicParameters.setFy(1);
        intrinsicParameters.setWidth((int)camera.getViewSize().getWidth());
        intrinsicParameters.setHeight((int)camera.getViewSize().getHeight());

        detector = FactoryFiducial.squareBinary(
                new ConfigFiducialBinary(1),
                ConfigThreshold.local(ThresholdType.LOCAL_SQUARE, 10),
                //ConfigThreshold.fixed(100),
                GrayU8.class);
        detector.setIntrinsic(intrinsicParameters);

        tracker =
                FactoryTrackerObjectQuad.circulant(null, GrayU8.class);
//				FactoryTrackerObjectQuad.sparseFlow(null,GrayU8.class,null);
//				FactoryTrackerObjectQuad.tld(null,GrayU8.class);
//				FactoryTrackerObjectQuad.meanShiftComaniciu2003(new ConfigComaniciu2003(), ImageType.pl(3, GrayU8.class));
//				FactoryTrackerObjectQuad.meanShiftComaniciu2003(new ConfigComaniciu2003(true),ImageType.pl(3,GrayU8.class));
// 				FactoryTrackerObjectQuad.meanShiftLikelihood(30,5,255, MeanShiftLikelihoodType.HISTOGRAM,ImageType.pl(3,GrayU8.class));

        location = new Quadrilateral_F64();

        gui = new ImagePanel();
        gui.setPreferredSize(camera.getViewSize());
        ShowImages.showWindow(gui,"Head Position Tracker",true);
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        while (true) {
            BufferedImage image = camera.getImage();
            GrayU8 input = ConvertBufferedImage.convertFrom(image, (GrayU8) null);
            Graphics2D g2 = image.createGraphics();
            g2.setColor(Color.RED);

            WorldToCameraToPixel transform;
            float xAvg = 0, yAvg = 0, zAvg = 0;
            int samples = 0;

            try {
                location.a.set(0,0);
                location.b.set(0,0);
                location.c.set(0,0);
                location.d.set(0,0);

                detector.detect(input);

                Se3_F64 targetToSensor = new Se3_F64();
                for (int i = 0; i < detector.totalFound(); i++) {
                    if (detector.getId(i) != 284 && detector.getId(i) != 643) {
                        System.out.println("Skipping unknown id " + detector.getId(i));
                        continue;
                    }

                    detector.getFiducialToCamera(i, targetToSensor);

                    transform = PerspectiveOps.createWorldToPixel(intrinsicParameters, targetToSensor);
                    Point2D_F64 width = transform.transform(
                            new Point3D_F64(detector.getWidth(i), detector.getWidth(i), 0));
                    Point2D_F64 centre = transform.transform(
                            new Point3D_F64(0, 0, 0));

                    double w = Math.abs(width.getX() - centre.getX());
                    double h = Math.abs(width.getY() - centre.getY());
                    int tlx = (int) (centre.getX() - w / 2.0);
                    int tly = (int) (centre.getY() - h / 2.0);

                    g2.drawRect(tlx, tly, (int) w, (int) h);

                    location.a.x+=tlx;
                    location.a.y+=tly;
                    location.b.x+=tlx+w;
                    location.b.y+=tly;
                    location.c.x+=tlx+w;
                    location.c.y+=tly+w;
                    location.d.x+=tlx;
                    location.d.y+=tly+w;

                    xAvg += (float) centre.getX();
                    yAvg += (float) centre.getY();
                    zAvg += (float) w;
                    samples += 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (samples > 0) {
                location.a.x /= samples;
                location.a.y /= samples;
                location.b.x /= samples;
                location.b.y /= samples;
                location.c.x /= samples;
                location.c.y /= samples;
                location.d.x /= samples;
                location.d.y /= samples;
                tracker.initialize(input, location);
                trackerFrames = 0;

                xAvg /= samples;
                yAvg /= samples;
                zAvg /= samples;

                xAvg = (xAvg / camera.getViewSize().width - 0.5f) * 5;
                yAvg = -(yAvg / camera.getViewSize().height - 0.5f) * 5;
                zAvg = (zAvg/(camera.getViewSize().width/6f)) * 5;
                isHeadVisible = true;
            }
            else {
                boolean visible = tracker.process(input,location);

                if (visible && trackerFrames < 60 &&
                        !Double.isNaN(location.a.x) &&
                        !Double.isNaN(location.a.y) &&
                        !Double.isNaN(location.b.x) &&
                        !Double.isNaN(location.b.y) &&
                        !Double.isNaN(location.c.x) &&
                        !Double.isNaN(location.c.y) &&
                        !Double.isNaN(location.d.x) &&
                        !Double.isNaN(location.d.y)) {
                    g2.setColor(Color.GREEN);
                    g2.drawRect((int) location.a.x,
                            (int) location.a.y,
                            (int) (location.c.x - location.a.x),
                            (int) (location.c.y - location.a.y));

                    xAvg += location.a.x;
                    xAvg += location.b.x;
                    xAvg += location.c.x;
                    xAvg += location.d.x;
                    yAvg += location.a.y;
                    yAvg += location.b.y;
                    yAvg += location.c.y;
                    yAvg += location.d.y;
                    samples = 4;
                    xAvg /= samples;
                    yAvg /= samples;
                    zAvg = (float) (location.c.x - location.a.x);
                    xAvg = (xAvg / camera.getViewSize().width - 0.5f) * 5;
                    yAvg = -(yAvg / camera.getViewSize().height - 0.5f) * 5;
                    zAvg = (zAvg/(camera.getViewSize().width/6f)) * 5;

                    trackerFrames++;
                    isHeadVisible = true;
                }
                else {
                    xAvg = position.x;
                    yAvg = position.y;
                    zAvg = position.z;
                    isHeadVisible = false;
                }
            }

            xAvg = MathUtils.clamp(xAvg, -2.5f, 2.5f);
            yAvg = MathUtils.clamp(yAvg, -2.5f, 2.5f);
            zAvg = MathUtils.clamp(zAvg, 2f, 10f)-10;

            synchronized (position) {
                position.x = 0.2f * position.x + xAvg * 0.8f;
                position.y = 0.2f * position.y + yAvg * 0.8f;
                position.z = 0.2f * position.z + zAvg * 0.8f;
            }

            gui.setBufferedImage(image);
        }
    }

    public void getPosition(Vector3 target) {
        synchronized (position) {
            target.set(position);
        }
    }

    public boolean isHeadVisible() {
        return isHeadVisible;
    }

}
