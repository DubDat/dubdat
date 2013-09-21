package data;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.demos.VideoImage;

public class VideoPlayer {
	public ConcurrentLinkedQueue<IPacket> queue = new ConcurrentLinkedQueue<IPacket>();
	public IStreamCoder videoCoder;
	public  IVideoResampler resampler;
	private AtomicBoolean running;
	
	private static VideoImage mScreen = null;

	private static long mSystemVideoClockStartTime;

	private static long mFirstVideoTimestampInStream;
	
	
	public void run() {
		running = new AtomicBoolean(true);
		while(running.get()) {
			IPacket packet = queue.poll();
			if(packet != null) {
				/*
		         * We allocate a new picture to get the data out of Xuggler
		         */
		        IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
		            videoCoder.getWidth(), videoCoder.getHeight());
		        
		        /*
		         * Now, we decode the video, checking for any errors.
		         * 
		         */
		        int bytesDecoded = videoCoder.decodeVideo(picture, packet, 0);
		        if (bytesDecoded < 0)
		          throw new RuntimeException("got error decoding audio in file");

		        /*
		         * Some decoders will consume data in a packet, but will not be able to construct
		         * a full video picture yet.  Therefore you should always check if you
		         * got a complete picture from the decoder
		         */
		        if (picture.isComplete())
		        {
		          IVideoPicture newPic = picture;
		          /*
		           * If the resampler is not null, that means we didn't get the video in BGR24 format and
		           * need to convert it into BGR24 format.
		           */
		          if (resampler != null)
		          {
		            // we must resample
		            newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
		            if (resampler.resample(newPic, picture) < 0)
		              throw new RuntimeException("could not resample video from file");
		          }
		          if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
		            throw new RuntimeException("could not decode video as BGR 24 bit data in file");

		          long delay = millisecondsUntilTimeToDisplay(newPic);
		          // if there is no audio stream; go ahead and hold up the main thread.  We'll end
		          // up caching fewer video pictures in memory that way.
		          try
		          {
		            if (delay > 0)
		              Thread.sleep(delay);
		          }
		          catch (InterruptedException e)
		          {
		            return;
		          }

		          // And finally, convert the picture to an image and display it

		          mScreen.setImage(Utils.videoPictureToImage(newPic));
		        }
			}
		}
	}
	
	public void stop() {
		running = new AtomicBoolean(false);
	}
	
	private static long millisecondsUntilTimeToDisplay(IVideoPicture picture)
	  {
	    /**
	     * We could just display the images as quickly as we decode them, but it turns
	     * out we can decode a lot faster than you think.
	     * 
	     * So instead, the following code does a poor-man's version of trying to
	     * match up the frame-rate requested for each IVideoPicture with the system
	     * clock time on your computer.
	     * 
	     * Remember that all Xuggler IAudioSamples and IVideoPicture objects always
	     * give timestamps in Microseconds, relative to the first decoded item.  If
	     * instead you used the packet timestamps, they can be in different units depending
	     * on your IContainer, and IStream and things can get hairy quickly.
	     */
	    long millisecondsToSleep = 0;
	    if (mFirstVideoTimestampInStream == Global.NO_PTS)
	    {
	      // This is our first time through
	      mFirstVideoTimestampInStream = picture.getTimeStamp();
	      // get the starting clock time so we can hold up frames
	      // until the right time.
	      mSystemVideoClockStartTime = System.currentTimeMillis();
	      millisecondsToSleep = 0;
	    } else {
	      long systemClockCurrentTime = System.currentTimeMillis();
	      long millisecondsClockTimeSinceStartofVideo = systemClockCurrentTime - mSystemVideoClockStartTime;
	      // compute how long for this frame since the first frame in the stream.
	      // remember that IVideoPicture and IAudioSamples timestamps are always in MICROSECONDS,
	      // so we divide by 1000 to get milliseconds.
	      long millisecondsStreamTimeSinceStartOfVideo = (picture.getTimeStamp() - mFirstVideoTimestampInStream)/1000;
	      final long millisecondsTolerance = 50; // and we give ourselfs 50 ms of tolerance
	      millisecondsToSleep = (millisecondsStreamTimeSinceStartOfVideo -
	          (millisecondsClockTimeSinceStartofVideo+millisecondsTolerance));
	    }
	    return millisecondsToSleep;
	  }
}
