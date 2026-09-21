// BlobDetection.java - Basic Color Blob Detection Pipeline for FTC
// This file sets up a basic OpenCV pipeline to look for a specific color blob
// Didnt take too long to do, this is just simple psuedo code with a mix of regular code that we can tweak once we have out real camera.


 (e.g., Team Prop).

package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class BlobDetection extends OpenCvPipeline {
    // Define the HSV threshold bounds for your target color (Tweak these later)
    // Currently set as a placeholder range
    public Scalar lowerHSV = new Scalar(0, 100, 100); 
    public Scalar upperHSV = new Scalar(10, 255, 255);

    // Matrix variables to hold image states processing safely
    private Mat hsvMat = new Mat();
    private Mat maskMat = new Mat();
    
    // Outputs you can read from your OpMode
    public double blobX = 0;
    public double blobY = 0;
    public boolean blobDetected = false;

    @Override
    public Mat processFrame(Mat input) {
        // 1. Convert the image from RGB to HSV color space
        Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);

        // 2. Filter the image to only show colors within your defined threshold bounds
        Core.inRange(hsvMat, lowerHSV, upperHSV, maskMat);

        // 3. Find the contours (shapes/blobs) in the filtered mask
        // Note: For basic starter code, we will find the largest bounding region.
        // In full implementation, use Imgproc.findContours to get precise coordinates.
        
        // Placeholder logic: Let's assume you found a blob if the mask has enough white pixels
        double whitePixels = Core.sumElems(maskMat).val[0];
        if (whitePixels > 50000) { // Threshold for minimum blob size
            blobDetected = true;
            // Tweak this to find the exact bounding box center (X, Y) later
            blobX = input.cols() / 2.0; 
            blobY = input.rows() / 2.0;
        } else {
            blobDetected = false;
        }

        // Return the mask or the original frame to the camera stream viewport
        return maskMat; 
    }
}
