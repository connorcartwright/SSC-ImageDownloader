import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class is used to check whether an image format is correct, manipulate a list of images from a website,
 * and download images from a website.
 * 
 * @author Connor
 *
 */
public class GetImages {

	private String folderPath; 		// the place to store the images
	private String websiteURL; 		// the website to download the images from
	private Document document; 		// cached version of the web page? not entirely sure
	
	public GetImages() {
	}
	
	/**
	 * Checks that the images are the format specified by the user,
	 * or if image format was not specified, does nothing.
	 * 
	 * @param extensions	The extensions that the user can specify they want downloaded.
	 * @param imageURL		The image in question - we are checking if it matches the extension(s) specified by the user.
	 * @return 				Whether or not the image is in the correct format, based on the extensions specified by the user.
	 */
	public Boolean imageFormatCorrect(String[] extensions, String imageURL) {
		if (extensions.length > 0) { 						// if extensions were specified by the user
			for (String extension : extensions) { 			// for each extension specified (this may only be one)
				if (imageURL.endsWith(extension)) { 		// if the image url ends with one of the extensions
					return true; 							// then return true, i.e. the image is the correct format
				}
			}
		}
		else {												// else, if no extensions were specified then return true
			return true;
		}
		return false;										// none of the extensions matched the imageURL, so return false, i.e. the image is the wrong format
	}

	/**
	 * This method returns a list of images from the website url.
	 * 
	 * @return a list of images from the website url.
	 */
	public Elements listImages() {
		try {
			document = Jsoup.connect(websiteURL).get();
			return document.select("img");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method is used to download a single image at a time.
	 * 
	 * @param image 		the image to be downloaded.
	 * @param webpage 		the webpage to download the image from
	 * @throws IOException	can potentially throw an IOException
	 */
	public void downloadSingleImage(Element image, String webpage) throws IOException {
		String url = image.attr("src"); 										// get the source of the image
		String fileName = url.substring(url.lastIndexOf('/') + 1);				// get the filename of the image, e.g. pens.jpeg
		String URLofImage = image.absUrl("src");								// build the url of the image
		URL imageURL = new URL(URLofImage);
		writeImageToFile(imageURL, fileName);
	}
	
	/**
	 * This method is used to write an image from a webpage to a file.
	 * 
	 * @param imageURL			The url of the image that will be written to file.
	 * @param fileName			What we want to call the file we will save.
	 * @throws IOException		Can potentially throw an IO exception.
	 */
	private void writeImageToFile(URL imageURL, String fileName) throws IOException {
		InputStream in = imageURL.openStream();						// Code from UOB SSC Lectures
		OutputStream out = new BufferedOutputStream(				// Code from UOB SSC Lectures
				new FileOutputStream(folderPath + "\\"				// Code from UOB SSC Lectures
						+ fileName));								// Code from UOB SSC Lectures
		for (int b; (b = in.read()) != -1;) {						// Code from UOB SSC Lectures
			out.write(b);											// Code from UOB SSC Lectures
		}															// Code from UOB SSC Lectures
		out.close();												// Code from UOB SSC Lectures
		in.close();													// Code from UOB SSC Lectures
	}
	
	/**
	 * This method sets the folder path that we want to download images to.
	 * 
	 * @param folderPath the folder path that we want to download images to.
	 */
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	
	/**
	 * This method sets the website that we want to download images from.
	 * 
	 * @param websiteURL the website that we want to download images from.
	 */
	public void setWebsite(String websiteURL) {
		this.websiteURL = websiteURL;
	}	
	
}