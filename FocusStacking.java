

import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * A simple focus stacking algorithm using java and openCV
 * 
 * Steps : 
 * https://stackoverflow.com/questions/15911783/what-are-some-common-focus-stacking-algorithms
 * 
 * Thanks to Charles McGuinness (python exemple)
 * 
 * @author Lucas Lelaidier
 */
public class FocusStacking
{
	/**
	 * List of images to merge together
	 */
	private ArrayList<Mat> inputs = new ArrayList<Mat>();
	
	/**
	 * Path to the folder which contains the images
	 */
	private String path;
	
	public FocusStacking(String path)
	{		
		this.path = path.replace("\\", "/");
	}

	public FocusStacking(ArrayList<Mat> inputs)
	{
		this.inputs = inputs;
	}
	
	public void setInputs(ArrayList<Mat> inputs)
	{
		this.inputs = inputs;
	}
	
	/**
	 * Compute the gradient map of the image
	 * @param image image to transform
	 * @return image image transformed
	 */
	public Mat laplacien(Mat image)
	{
		int kernel_size = 5;
		double blur_size = 5;
		
		Mat gray = new Mat();
		Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
		
		Mat gauss = new Mat();
		Imgproc.GaussianBlur(gray, gauss, new Size(blur_size, blur_size), 0);
		
		Mat laplace = new Mat();
		Imgproc.Laplacian(gauss, laplace, CvType.CV_64F, kernel_size, 1, 0);
		
		Mat absolute = new Mat();
		Core.convertScaleAbs(laplace, absolute);

		return absolute;
	}
	
	/**
	 * apply focus stacking on inputs
	 */
	public void focus_stack()
	{
		if(inputs.size() == 0)
		{
			System.out.println("please select some inputs");
		}
		else
		{
			System.out.println("Computing the laplacian of the blurred images");
			Mat[] laps = new Mat[inputs.size()];
			
			for (int i = 0 ; i < inputs.size() ; i++)
			{
				System.out.println("image "+i);
				laps[i] = laplacien(inputs.get(i));
			}
			
			Mat vide = Mat.zeros(laps[0].size(), inputs.get(0).type());
			
			for(int y = 0 ; y < laps[0].cols() ; y++)
			{
				for(int x = 0 ; x < laps[0].rows() ; x++)
				{
					int index = -1;
					double indexValue = -1;
					for (int i = 0 ; i < laps.length ; i++)
					{
						if(indexValue == -1 || laps[i].get(x,y)[0] > indexValue)
						{
							indexValue = laps[i].get(x,y)[0];
							index = i;
						}
					}
					vide.put(x, y, inputs.get(index).get(x, y));
				}
			}
			System.out.println("Success !");
			Imgcodecs.imwrite(path + "merged.png", vide);
		}
	}
	
	
	/**
	 * Fill inputs list using the path 
	 */
	public void fill()
	{
		// Ouvre un repertoire
		File repertoire = new File(path);
		
		if(!repertoire.exists())
		{
			System.out.println("directory : " + path + " doesn't exist");
		}
		else
		{
			// Liste les fichiers du repertorie
			File[] files = repertoire.listFiles();
			
			for(int i = 0 ; i < files.length ; i++)
			{
				String nom = files[i].getName();
				inputs.add(Imgcodecs.imread(path + nom)); 
				System.out.println(Imgcodecs.imread(path + nom).size());
			}
		}
	}
	
	public static void main(String[] args)
	{
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
		
		FocusStacking stack = new FocusStacking("/home/lucas/Images/testStacking/");
		stack.fill();
		stack.focus_stack();
	}
}






