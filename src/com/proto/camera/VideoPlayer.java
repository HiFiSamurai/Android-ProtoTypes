package com.proto.camera;

import java.io.File;

import com.proto.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video_playback_layout);

		String path = getIntent().getExtras().getString(VideoList.VIDEO_EXTRA);
		File video = new File(path);
		if(!(video.exists()))
			throw new RuntimeException("Video Does not exist: " + path);

		VideoView videoView = (VideoView)findViewById(R.id.video_spot);
		MediaController mediaController = new MediaController(this);
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);
		videoView.setVideoPath(video.getAbsolutePath());
	}
}
