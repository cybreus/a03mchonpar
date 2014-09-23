package a03;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class PlaybackPanel extends JPanel implements MouseListener{
	//all media play back buttons
	private JButton playButton, ffButton, fbButton;
	//current media for play back
	private EmbeddedMediaPlayer currentVideo;
	//all swing workers used for the functions
	private FastForwarder ff;
	private FastBackwarder fb;
	//all button icons normally
	private String iconPath = "./icons";
	private ImageIcon iconPlay = new ImageIcon(iconPath + "/PlayButtonN.png");
	private ImageIcon iconPause = new ImageIcon(iconPath + "/PauseButtonN.png");
	private ImageIcon iconFF = new ImageIcon(iconPath + "/FFButtonN.png");
	private ImageIcon iconFB = new ImageIcon(iconPath + "/FBButtonN.png");
	//all button icons when highlighted
	private ImageIcon iconPlayH = new ImageIcon(iconPath + "/PlayButtonH.png");
	private ImageIcon iconPauseH = new ImageIcon(iconPath + "/PauseButtonH.png");
	private ImageIcon iconFFH = new ImageIcon(iconPath + "/FFButtonH.png");
	private ImageIcon iconFBH = new ImageIcon(iconPath + "/FBButtonH.png");
	//all booleans for current play back status
	private boolean isPaused = true;
	private boolean isFastForwarding = false;
	private boolean isFastBackwarding = false;
	private boolean firstPlay = true;
	
	public PlaybackPanel() {
		//set up of the play back panel
		setOpaque(false);
		setSize(460,60);
		setLocation(370,795);
		setLayout(null);
		
		//Play/Pause button setup
		playButton = setImageButton(iconPlay);
		playButton.setSize(200, 50);
		playButton.setLocation(130, 5);
		playButton.addMouseListener(this);
		//when the play/pause button is pressed...
		playButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				//for when there is no video yet
				if (currentVideo == null ) {
					//warning message to be popped up
					String msg = "Please open a media file before trying to play it!";
					JOptionPane.showMessageDialog(null, msg);
					//for when a valid video is there
				} else {
					//when video is paused
					if (isPaused == true) {
						//when there is an instance of fast forward or backward call stop on the 
						//swing worker to stop it
						if (ff != null) {
							ff.stop();
							isFastForwarding = false;
						} 
						if (fb != null) {
							fb.stop();
							isFastBackwarding = false;
						}
						//it starts playing, and button changes to pause button
						currentVideo.play();
						setToPause();
						//media player is glitchy? and saves previous sessions settings
						//so volume and mute reset for the first play of media
						if (firstPlay) {
							/**VOLUME MUTE REFRESH*/
							//only for first play when program opened
							firstPlay = false;
						}
					//when video is playing
					} else {
						//video is paused, and button changes to play button
						currentVideo.pause();
						setToPlay();
					}
				}
			}	
		});
		//cannot click the button until it is set to 
		playButton.setEnabled(false);
		add(playButton);
			
		//Fast Backward button setup
		fbButton = setImageButton(iconFB);
		fbButton.setSize(100, 50);
		fbButton.setLocation(5, 5);
		fbButton.addMouseListener(this);
		//for when rewind is clicked
		fbButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if it is rewinding and clicked
				if (isFastBackwarding) {
					setToPause();
					//rewind stops and video is played normally
					fb.stop();
					isFastBackwarding = false;
				//if it is not rewinding and clicked
				} else {
					if (isFastForwarding) {
						ff.stop();
						isFastForwarding = false;
					}
					//for when its clicked while video is playing, video is paused first
					if (isPaused == false) {
						currentVideo.pause();
					}
					setToPlay();
					//rewinding swing worker is instantiated and executed
					fb = new FastBackwarder(currentVideo);
					fb.execute();
					//hence rewind becomes true
					isFastBackwarding = true;
				}
			}	
		});
		//cannot click button until set to
		fbButton.setEnabled(false);
		add(fbButton);
						
		//Fast Forward button setup
		ffButton = setImageButton(iconFF);
		ffButton.setSize(100, 50);
		ffButton.setLocation(355, 5);
		ffButton.addMouseListener(this);
		//when fast forward button is clicked
		ffButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isFastForwarding) {
					setToPause();
					ff.stop();
					isFastForwarding = false;
				} else {
					if (isFastBackwarding) {
						fb.stop();
						isFastBackwarding = false;
					}
					if (isPaused == false) {
						currentVideo.pause();
					}
					setToPlay();
					ff = new FastForwarder(currentVideo);
					ff.execute();
					isFastForwarding = true;
				}
			}	
		});
		//cannot click button until set to
		ffButton.setEnabled(false);
		add(ffButton);	
	}
	
	public void setCurrentVideo(EmbeddedMediaPlayer m){
		currentVideo = m;
	}
	public void audioOn(){
		playButton.setEnabled(true);
		setToPlay();
		ffButton.setEnabled(false);
		fbButton.setEnabled(false);
	}
	public void videoOn(){
		playButton.setEnabled(true);
		setToPlay();
		ffButton.setEnabled(true);
		fbButton.setEnabled(true);
	}
	//method to set play button for "Play"
	private void setToPlay() {
		playButton.setIcon(iconPlay);
		isPaused = true;
	}
	//method to set play button for "Pause"
	private void setToPause() {
		playButton.setIcon(iconPause);
		isPaused = false;
	}
	//set up and return a button using only an image icon
	private JButton setImageButton(ImageIcon img) {
		JButton imgButton = new JButton(img);
		imgButton.setOpaque(false);
		imgButton.setContentAreaFilled(false);
		imgButton.setBorderPainted(false);
		imgButton.setFocusPainted(false);
		return imgButton;
	}
	
	@Override
	public void mouseClicked(MouseEvent m) {
	}
	@Override
	public void mouseEntered(MouseEvent m) {
		JButton s = (JButton)m.getSource();
		if (s.isEnabled()) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			if (s.equals(playButton)) {
				if (isPaused == true) {
					playButton.setIcon(iconPlayH);
				} else {
					playButton.setIcon(iconPauseH);
				}
			} else if (s.equals(fbButton)) {
				fbButton.setIcon(iconFBH);
			} else if (s.equals(ffButton)) {
				ffButton.setIcon(iconFFH);
			}
		}
	}
	@Override
	public void mouseExited(MouseEvent m) {	
		JButton s = (JButton)m.getSource();
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		if (s.equals(playButton)) {
			if (isPaused == true) {
				playButton.setIcon(iconPlay);
			} else {
				playButton.setIcon(iconPause);
			}
		} else if (s.equals(fbButton)) {
			fbButton.setIcon(iconFB);
		} else if (s.equals(ffButton)) {
			ffButton.setIcon(iconFF);
		}
	}
	@Override
	public void mousePressed(MouseEvent m) {	
	}
	@Override
	public void mouseReleased(MouseEvent m) {	
	}
}
