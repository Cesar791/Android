package server;


import org.bytedeco.javacpp.Loader;
import static org.bytedeco.javacpp.opencv_objdetect.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import org.bytedeco.javacpp.opencv_objdetect;


public class FaceDetection {
	// scale factor to resize the input image to a smaller size
	private static final int SCALE = 2;

	// cascade definition for facial recognition
	private static final String CASCADE_FILE = "haarcascade_frontalface_alt.xml";

	// resized image in grayscale
	private IplImage smallImg;

	// original image
	public IplImage originalImg;

	public FaceDetection(IplImage img) {

		// preload opencv_objdetect module to avoid a known bug
		Loader.load(opencv_objdetect.class);

		originalImg = img;

		// convert image to grayscale and resize it for speed.
		convertToGray(img);

		// creates a temporary storage to be used during object indentification
		CvMemStorage storage = CvMemStorage.create();

		// identify faces
		CvSeq faces = identify(smallImg, storage);

		// empty temporary storage
		cvClearMemStorage(storage);

		// draw rectangles (and save image for debugging)
		drawAndSave(faces, originalImg);

	}

	///////////////////////////////////////////////////

	// load a image from file (for debugging)
	public IplImage loadImage(String img) {
		System.out.println("Loading image from " + img);
		return cvLoadImage(img);
	}

	public void convertToGray(IplImage origImg) {
		// convert to grayscale
		IplImage graImg = cvCreateImage(cvGetSize(origImg), IPL_DEPTH_8U, 1);
		cvCvtColor(origImg, graImg, CV_BGR2GRAY);

		// resize the gray image to increase the speed of the facial recognition process
		smallImg = IplImage.create(graImg.width() / SCALE, graImg.height() / SCALE, IPL_DEPTH_8U, 1);
		cvResize(graImg, smallImg, CV_INTER_LINEAR);

		// equalize the resized grayscale image
		cvEqualizeHist(smallImg, smallImg);

	}

	public CvSeq identify(IplImage smallImg, CvMemStorage storage) {
		// instansiate a cascade classifier for facial recognition
		CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));
		return cvHaarDetectObjects(smallImg, cascade, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
	}

	@SuppressWarnings("resource")
	public void drawAndSave(CvSeq faces, IplImage origImg) {
		
		// iterate over the identified faces and draw green rectangles around them
		int total = faces.total();

		for (int i = 0; i < total; i++) {
			CvRect r = new CvRect(cvGetSeqElem(faces, i));
			cvRectangle(origImg, cvPoint(r.x() * SCALE, r.y() * SCALE), // removes
																		// scale
																		// factor
					cvPoint((r.x() + r.width()) * SCALE, (r.y() + r.height()) * SCALE), CvScalar.GREEN, 6, CV_AA, 0);
		}

		// save the image for debugging
		// if (total > 0) {

		// System.out.println("Saving marked faces version in " + img + "
		// in " + OUT_FILE);
		// cvSaveImage(OUT_FILE, origImg);
		// }
		// return origImg;
	}

}
