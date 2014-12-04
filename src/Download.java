import java.io.IOException;

import org.jsoup.nodes.Element;

/**
 * This class creates a runnable object to download an image.
 * 
 * @author Connor
 *
 */
public class Download implements Runnable {

	GetImages getImages;
	Element image;
	String webpage;
	
	/**
	 * Create a new download object that we can use to download an image.
	 * 
	 * @param webpage		the webpage we want to download from
	 * @param image			the image that we want to download
	 * @param location		where we want to put the downloaded images
	 */
	public Download(String webpage, Element image, String location) {
		getImages = new GetImages();
		this.image = image;
		this.webpage = webpage;	
		getImages.setFolderPath(location);
		getImages.setWebsite(webpage);
	}

	/**
	 * Overrides the Run method, so that we can download an image.
	 */
	@Override
	public void run() {
		try {
			getImages.downloadSingleImage(image, webpage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
