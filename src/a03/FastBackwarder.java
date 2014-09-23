package a03;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class FastBackwarder extends SwingWorker<Void,Void>{
	private EmbeddedMediaPlayer currentVideo;
	//boolean to command rewinding
	private boolean fastBackward = true;
	
	//constructor to set up video to rewind
	public FastBackwarder(EmbeddedMediaPlayer vid){
		currentVideo = vid;
	}
	
	//method to stop/ complete swing worker action of rewinding
	public void stop() {
		fastBackward = false;
	}
	
	//-------------------When Executed--------------------------//
	
	@Override
	protected Void doInBackground() throws Exception {
		//while commanded to rewind, rewinds until set to stop
		while (fastBackward) {
			Thread.sleep(50);
			currentVideo.skip(-100);
		}
		return null;
	}

	@Override
	protected void done() {
		//once rewinding completes video plays normally
		currentVideo.play();
	}
}
