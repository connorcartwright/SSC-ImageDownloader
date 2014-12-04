import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.JLabel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



import org.jsoup.nodes.Element;

/**
 * This class is used to display the GUI that will allow the user to download
 * images from a webpage of their choosing.
 * 
 * @author Connor
 *
 */
public class GUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private GetImages getImages;
	private JPanel contentPane;
	private JTextField websiteInput;
	private JTextField locationInput;
	private JTextField extensionsInput;
	private JTextField threadsInput;
	private JTable imageDetailsTable;
	private Component errorFrame; // used when producing the error message

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame that will allow the user to specify the website to download from,
	 * the download location, the number of threads to use, and the extensions to download.
	 * 
	 * Also includes a progress bar, which shows download progress.
	 */
	public GUI() {
		getImages = new GetImages();	
		setBackground(SystemColor.textHighlight);
		setTitle("Image Downloader");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 750, 450);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.activeCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitApp(); // run the exitApp method if the window 'x' button is pressed
			}
		});
		
		JLabel websiteLabel = new JLabel("Website:");
		websiteLabel.setForeground(new Color(0, 0, 128));
		websiteLabel.setBackground(Color.WHITE);
		websiteLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		websiteLabel.setBounds(10, 11, 65, 14);
		contentPane.add(websiteLabel);
		
		websiteInput = new JTextField();
		websiteInput.setText("www.google.com");
		websiteInput.setToolTipText("The website that you want to download the images from, e.g. www.microsoft.co.uk");
		websiteInput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		websiteInput.setBackground(SystemColor.inactiveCaption);
		websiteInput.setBounds(72, 8, 236, 20);
		contentPane.add(websiteInput);
		websiteInput.setColumns(10);		
		
		JLabel locationLabel = new JLabel("Download Location:");
		locationLabel.setForeground(new Color(0, 0, 128));
		locationLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		locationLabel.setBounds(330, 11, 108, 14);
		contentPane.add(locationLabel);
		
		locationInput = new JTextField();
		locationInput.setText("C:\\Users\\Connor\\Desktop");
		locationInput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		locationInput.setToolTipText("Where you want to download the images to, e.g. your Desktop.");
		locationInput.setBackground(SystemColor.inactiveCaption);
		locationInput.setBounds(448, 9, 235, 20);
		contentPane.add(locationInput);
		locationInput.setColumns(10);
		
		JButton locationButton = new JButton("New button");
		locationButton.setToolTipText("Used to manually select a location through a file explorer.");
		locationButton.setBounds(693, 11, 31, 17);
		contentPane.add(locationButton);
		
		locationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser locationChooser = new JFileChooser();
				locationChooser.setCurrentDirectory(new java.io.File("."));
				locationChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				locationChooser.setAcceptAllFileFilterUsed(false);
				int returnVal = locationChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					locationInput.setText(locationChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		JLabel extensionsLabel = new JLabel("Extensions:");
		extensionsLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		extensionsLabel.setForeground(new Color(0, 0, 128));
		extensionsLabel.setBounds(10, 47, 65, 14);
		contentPane.add(extensionsLabel);
		
		extensionsInput = new JTextField();
		extensionsInput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		extensionsInput.setToolTipText("The extensions of the images you want to download, e.g. .png");
		extensionsInput.setBackground(SystemColor.inactiveCaption);
		extensionsInput.setBounds(82, 45, 226, 20);
		contentPane.add(extensionsInput);
		extensionsInput.setColumns(10);
		
		JLabel threadsLabel = new JLabel("Threads:");
		threadsLabel.setForeground(new Color(0, 0, 128));
		threadsLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		threadsLabel.setBounds(383, 47, 55, 14);
		contentPane.add(threadsLabel);
		
		threadsInput = new JTextField();
		threadsInput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		threadsInput.setToolTipText("The number of threads that you want to be available.");
		threadsInput.setBackground(SystemColor.inactiveCaption);
		threadsInput.setBounds(448, 45, 84, 20);
		contentPane.add(threadsInput);
		threadsInput.setColumns(10);
		
		String[] columns = { "File name", "File URL", "Extension", "Resolution" };
		DefaultTableModel defaultTable = new DefaultTableModel(columns, 0);
		
		imageDetailsTable = new JTable(defaultTable);
		imageDetailsTable.setFont(new Font("Tahoma", Font.PLAIN, 12));
		imageDetailsTable.setBackground(SystemColor.inactiveCaption);
		imageDetailsTable.setBounds(10, 81, 714, 289);
		contentPane.add(imageDetailsTable);
		
		JButton searchButton = new JButton("Search for Images");
		searchButton.setToolTipText("Search for images at the specified website, with the specified extensions.");
		searchButton.setBounds(570, 44, 154, 21);
		contentPane.add(searchButton);
		
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (websiteInput.getText().length() < 10) {
					JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
							"The website name entered is invalid!",
							"Invalid website!", JOptionPane.ERROR_MESSAGE);	
				}
				else if (! (websiteInput.getText().startsWith("http://"))) {
					if (websiteInput.getText().startsWith("www")) {
						websiteInput.setText("http://" + websiteInput.getText());
						JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
								"The website URL was changed to include http:// :)",
								"Website URL fixed", JOptionPane.INFORMATION_MESSAGE);	
					}
					else {
						JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
								"Invalid website url entered! Please try to include http://www.",
								"Invalid url!", JOptionPane.ERROR_MESSAGE);	
					}
				}
				else if (threadsInput.getText().length() > 0) {
					if (Integer.parseInt(threadsInput.getText()) > 8 || Integer.parseInt(threadsInput.getText()) < 1) {
						JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
								"The number of threads must be between 1 and 8!",
								"Invalid number of threads!", JOptionPane.ERROR_MESSAGE);	
					}		
					else {
						String resolution = "";
						getImages.setFolderPath(locationInput.getText());
						getImages.setWebsite(websiteInput.getText());
						defaultTable.setRowCount(0);
						imageDetailsTable.updateUI();
						for(Element i : getImages.listImages()) {
							String filename = i.attr("src");
							try {
								URL url = new URL(i.absUrl("src"));
								BufferedImage bi = ImageIO.read(url);
								if (bi != null) {
									resolution = bi.getWidth() + " x " + bi.getHeight();
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							
							Object[] object = { filename.substring(filename.lastIndexOf('/') + 1, filename.length()), filename, 
												filename.substring(filename.lastIndexOf('.'), filename.length()), resolution  };
							defaultTable.insertRow(imageDetailsTable.getRowCount(), object);
							imageDetailsTable.updateUI();
						}
					}
				}
				else {
					JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
							"The number of threads must be between 1 and 8 and not null!",
							"Invalid number of threads!", JOptionPane.ERROR_MESSAGE);		
				}
			}
		});
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(91, 381, 528, 20);
		contentPane.add(progressBar);
		
		JScrollPane scrollPane = new JScrollPane(imageDetailsTable);
		scrollPane.setBounds(10, 81, 714, 289);
		contentPane.add(scrollPane);
		
		JButton downloadButton = new JButton("Download");
		downloadButton.setBounds(629, 381, 95, 20);
		contentPane.add(downloadButton);
		
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (websiteInput.getText().length() < 10) {
					System.out.println("nope");
					JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
							"The website name entered is invalid!",
							"Invalid website!", JOptionPane.ERROR_MESSAGE);	
				}
				else if (! (websiteInput.getText().startsWith("http://"))) {
					if (websiteInput.getText().startsWith("www")) {
						websiteInput.setText("http://" + websiteInput.getText());
						JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
								"The website URL was changed to include http:// :)",
								"Website URL fixed", JOptionPane.INFORMATION_MESSAGE);	
					}
					else {
						JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
								"Invalid website url entered! Please try to include http://www.",
								"Invalid url!", JOptionPane.ERROR_MESSAGE);	
					}
				}
				else if (threadsInput.getText().length() > 0) {
					if (Integer.parseInt(threadsInput.getText()) > 8 || Integer.parseInt(threadsInput.getText()) < 1) {
						JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
								"The number of threads must be between 1 and 8!",
								"Invalid number of threads!", JOptionPane.ERROR_MESSAGE);	
					}
					else {
						progressBar.setValue(0);
						
						String[] extensionsArray = extensionsInput.getText().replaceAll("\\s+","").split(";");
						ArrayList<Download> download = new ArrayList<Download>();
						ExecutorService pool = Executors.newFixedThreadPool(Integer.parseInt(threadsInput.getText()));
						getImages.setWebsite(websiteInput.getText());
						getImages.setFolderPath(locationInput.getText());
						SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
							@Override
							protected Void doInBackground() throws Exception {
								progressBar.setMaximum(getImages.listImages().size());
								for (Element e : getImages.listImages()) {							
									if (getImages.imageFormatCorrect(extensionsArray, e.attr("src"))) {
 									Download downloader = new Download(websiteInput.getText(), e, locationInput.getText());
									download.add(downloader);
									}
								}
								for (Download d : download) {
									pool.execute(d);
									progressBar.setValue(progressBar.getValue() + 1);
								}
								return null;
							}			
						};
						worker.execute();
					}
				}
				else {
					JOptionPane.showMessageDialog(errorFrame, // then show an informative error message
							"The number of threads must be between 1 and 8 and not null!",
							"Invalid number of threads!", JOptionPane.ERROR_MESSAGE);		
				}
			}
		});
		
		JButton closeButton = new JButton("Close");
		closeButton.setBounds(10, 381, 71, 20);
		contentPane.add(closeButton);
		
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitApp();
			}
		});

		
	}
	
	/**
	 * Helper method to ensure consistency in leaving application.
	 * With credit to Aston University Computer Science.
	 */
	private void exitApp() {
		// Display confirmation dialog before exiting application
		int response = JOptionPane.showConfirmDialog(contentPane,
				"Do you really want to quit?", "Quit?",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
		// Don't quit
	}
}
