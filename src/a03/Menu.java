package a03;

import javax.swing.*;

import com.sun.jna.Native;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

public class Menu extends JFrame implements ActionListener{
	
	//Get user's screen dimension
	private static final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	private static final int _screenHeight = (int)d.getHeight();
	private static final int _screenWidth = (int)d.getWidth();
	//Main frame size
	private static final int _menuHeight = 900;
	private static final int _menuWidth = 1200;

	private String _mediaPath = "";
	private File _mediaFile;
	
	private MainPanel container;
	private EmbeddedMediaPlayerComponent ourMediaPlayer;
	private EmbeddedMediaPlayer currentVideo;
	
	public Menu() {
		
		//Frame setup
		setTitle("VAMIX Draft");
		setSize(_menuWidth,_menuHeight);
		setLocation(_screenWidth/2 - _menuWidth/2, _screenHeight/2 - _menuHeight/2);
		setResizable(false);
		setLayout(null);
		WindowListener exitListener = new WindowAdapter() {
			//Before the frame is closed set volume to default, and not mute if muted
			@Override
			public void windowClosing(WindowEvent e) {
				if (currentVideo != null) {
					currentVideo.setVolume(60);
					if (currentVideo.isMute()) {
						currentVideo.mute();
					}
				}
				System.exit(0);
			}
			
		};
		addWindowListener(exitListener);
		
		//-----------------------------------------------------------------------
		
		//Container SETUP
		
		//embedded media player setup
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		ourMediaPlayer = new EmbeddedMediaPlayerComponent();
		int videoPlayerWidth = this.getWidth()-10;
		int videoPlayerHeight = 700;
		ourMediaPlayer.setSize(videoPlayerWidth,videoPlayerHeight);
		ourMediaPlayer.setLocation((_menuWidth-videoPlayerWidth)/2,70);
		
		//instantiate main panel passing in Media Player
		container = new MainPanel(ourMediaPlayer);
		
		//a shortcut open button for the interface set up
		JButton openButton = new JButton();
		openButton.setSize(30,30);
		openButton.setLocation(815,20);
		openButton.setActionCommand("Open File");
		openButton.addActionListener(this);
		container.setOpenButton(openButton);
		
		//a shortcut download button for the interface set up
		JButton dlButton = new JButton("Download");
		dlButton.setSize(150,40);
		dlButton.setLocation(50,15);
		dlButton.setActionCommand("Download File");
		dlButton.addActionListener(this);
		container.setDownloadButton(dlButton);
		
		setContentPane(container);
		
		//------------------------------------------------------------------------
		
		//Menu bar added
		setJMenuBar(setUpMenuBar());
	}
	
	//Method to set up menu bar to be used in the frame
	private JMenuBar setUpMenuBar() {
		//create object for all menu bar, menus and items
		JMenu file, edit, help, _space, _space2;
		JMenuItem _save, _open, _close, _exit, _dl, _addText;
		JMenuBar menuBar = new JMenuBar();
		
		//set the graphics (color) for the Menu bar
		menuBar.setBackground(Color.DARK_GRAY);
		menuBar.setBorderPainted(true);
		
		//empty spaces to put in between menu options (spacings)
		_space = new JMenu();
		_space.setEnabled(false);
		_space2 = new JMenu();
		_space2.setEnabled(false);
		
		//setup of menu 'file' 
		file = new JMenu("File");
		file.setForeground(Color.LIGHT_GRAY);
		file.setMnemonic(KeyEvent.VK_F);
		//setup of all the items belonging to the 'file' menu
		_save = new JMenuItem("Save");
		_open = new JMenuItem("Open");
		_dl = new JMenuItem("Download");
		_open.setActionCommand("Open File");
		_open.addActionListener(this);
		_dl.setActionCommand("Download File");
		_dl.addActionListener(this);
		_close = new JMenuItem("Close");
		_exit = new JMenuItem("Exit");
		file.add(_save);
		file.add(_open);
		file.add(_dl);
		file.add(_close);
		file.add(_exit);
		menuBar.add(file);
		menuBar.add(_space);
		
		//setup of menu 'edit'
		edit = new JMenu("Edit");
		edit.setForeground(Color.LIGHT_GRAY);
		edit.setMnemonic(KeyEvent.VK_E);
		menuBar.add(edit);
		menuBar.add(_space2);
		
		//setup of menu 'help'
		help = new JMenu("Help");
		help.setForeground(Color.LIGHT_GRAY);
		help.setMnemonic(KeyEvent.VK_H);
		menuBar.add(help);
		
		//final menu bar is returned at the end of the setup 
		return menuBar;
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Menu().setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Open File")) {
			//when item is selected, a File chooser opens to select a file
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = fileChooser.showOpenDialog(this);
			//for when cancel/exit is pressed in the file chooser
			if (result == JFileChooser.CANCEL_OPTION) {
				//nothing is to be done
			//for when a file is selected
			} else if (result == JFileChooser.OPEN_DIALOG) {
				//the selected file is the video and path is retrieved
				_mediaFile = fileChooser.getSelectedFile();
				_mediaPath = _mediaFile.getAbsolutePath();
				//booleans to decide whether selected file is of a media file
				boolean isVideo = false;
				boolean isAudio = false;
			
				//bash command to 'grep' to verify file as media
				String audCmd = "file " + _mediaFile.getAbsolutePath() + " | grep -i audio";
				String vidCmd = "file " + _mediaFile.getAbsolutePath() + " | grep -i media";
				
				ProcessBuilder audCheckBuilder = new ProcessBuilder("/bin/bash","-c",audCmd);
				ProcessBuilder vidCheckBuilder = new ProcessBuilder("/bin/bash","-c",vidCmd);
				try {
					//process run
					Process audCheck = audCheckBuilder.start();
					int audTerm = audCheck.waitFor();
					Process vidCheck = vidCheckBuilder.start();
					int vidTerm = vidCheck.waitFor();
					//a correct termination indicates it is a media file
					if (audTerm == 0) {
						isAudio = true;
					} 
					if (vidTerm == 0){
						isVideo = true;
					}
				} catch (Exception ex) {
					//if exception occurs nothing extra happens
				}
				//when media file is selected
				if (isVideo || isAudio) {
					//current video is instantiated and paused immediately when it starts playing
					currentVideo = ourMediaPlayer.getMediaPlayer();
					currentVideo.playMedia(_mediaPath);
					while (true) {
						if (currentVideo.isPlaying()) {
							currentVideo.pause();
							break;
						}
					}
					//video is set in the main panel
					container.setCurrentVid(currentVideo,_mediaFile);
					
					if (isVideo) {
						//media buttons (play, fast-forward, etc) are enabled
						container.setMediaButtonOn();
					} else if (isAudio) {
						//audio buttons (play, etc) are enabled
						container.setAudioButtonOn();
					}
				//warning message if file is not media
				} else {
					JOptionPane.showMessageDialog(this, "File is not an audio or video type!");
				}
			}
		} else if (e.getActionCommand().equals("Download File")){
			String dlURL;
			//option pane that will take in the URL of download
			dlURL = JOptionPane.showInputDialog(this, "Please Enter URL:", "Download", 
					JOptionPane.DEFAULT_OPTION);
			//if cancelled do nothing
			if (dlURL == null) {
				//download cancelled before beginning
			} else {
				//download frame opened and download commences
				DownloadFrame downloadFrame = new DownloadFrame(dlURL);
				downloadFrame.startDownload();
			}
		}
	}
}
