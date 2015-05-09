/*
 * Copyright (c) 2015 Ahomé Innovation Technologies. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ait.toolkit.videoplayer.client;

import static com.google.gwt.core.client.GWT.getModuleBaseURL;

import java.util.ArrayList;
import java.util.List;

import com.ait.toolkit.core.client.JsoHelper;
import com.ait.toolkit.videoplayer.client.resources.VideoPlayerResources;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.dom.client.SourceElement;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.user.client.ui.Widget;

public class VideoPlayer extends Widget {
    public static final String VERSION = "v4126"; // 4.12.6

    private static final String FALLBACK_SWF = getModuleBaseURL() + "/videojs/" + VERSION + "/video-js.swf";
    private static final String DEFAULT_PRELOAD = MediaElement.PRELOAD_NONE;

    private final int width;
    private final int height;

    private String skinName = "vjs-default-skin";
    private boolean controls = true;
    private String preload = DEFAULT_PRELOAD;
    private String poster = null;
    private boolean loop = false;
    private boolean autoPlay = false;
    private int startPosition = 0;

    private List<String> sources = new ArrayList<String>();
    private List<String> sourceType = new ArrayList<String>();

    private String playerId;
    private JavaScriptObject playerObject;

    public VideoPlayer( int width, int height ) {
        if( VideoPlayerResources.INSTANCE.css().ensureInjected() ) {
            setFlashFallback();
        }

        this.width = width;
        this.height = height;

        setElement( Document.get().createDivElement() );
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Widget#onLoad()
     */
    @Override
    protected void onLoad() {
        playerId = Document.get().createUniqueId();

        VideoElement videoElem = Document.get().createVideoElement();

        videoElem.setId( playerId );
        videoElem.addClassName( "video-js" );
        videoElem.setWidth( width );

        videoElem.setHeight( height );

        if( skinName != null ) {
            videoElem.addClassName( skinName );
        }

        videoElem.setControls( controls );

        if( preload != null ) {
            videoElem.setPreload( preload );
        } else {
            videoElem.setPreload( DEFAULT_PRELOAD );
        }

        if( poster != null ) {
            videoElem.setPoster( poster );
        }

        if( ( sources.size() == 0 ) || ( sources.size() != sourceType.size() ) ) {
            throw new IllegalArgumentException( "Wrong number of video sources" );
        }

        for( int i = 0; i < sources.size(); i++ ) {
            SourceElement srcElem = Document.get().createSourceElement();

            srcElem.setSrc( sources.get( i ) );
            srcElem.setType( sourceType.get( i ) );

            videoElem.appendChild( srcElem );
        }

        getElement().appendChild( videoElem );

        this.playerObject = initPlayer();

        if( ( startPosition != 0 ) && !isFlashFallback() ) { // Because of lack in progressive download for flash
            addDurationChangeHandler( new VideoPlayerHandler() {
                public void handle( VideoPlayer player ) {
                    player.pause();
                    player.setCurrentTime( startPosition );
                    player.play();
                }
            } );
        }
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Widget#onUnload()
     */
    @Override
    protected void onUnload() {
        super.onUnload();

        this.playerObject = null;
    }

    /**
     * Updates the video source.
     * Use this method if you are sure the current playback technology (HTML5/Flash) can support the source you provide. 
     * Currently only MP4 files can be used in both HTML5 and Flash.
     */
    public native void setSource( String value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.src(value);
		}
    }-*/;

    /**
     * Updates the video source
     */
    public void setSource( VideoSource... sources ) {
        JavaScriptObject array = JsoHelper.createJavaScriptArray();
        for( int i = 0; i < sources.length; i++ ) {
            JsoHelper.setArrayValue( array, i, sources[i].getJsObj() );
        }
        _setSource( array );
    }

    private native void _setSource( JavaScriptObject values ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.src(values);
		}
    }-*/;

    /**
     * Add a CSS class name to the component's element
     * @param value, the new CSS class
     */
    public native void addClass( String value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.addClass(value);
		}
    }-*/;

    /**
     * Add a text track In addition to the W3C settings we allow adding additional info through options.
     * @param kind, Captions, subtitles, chapters, descriptions, or metadata
     */
    public native void addTextTrack( String kind ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.addTextTrack(kind);
		}
    }-*/;

    /**
     * Add a text track In addition to the W3C settings we allow adding additional info through options.
     * @param kind, Captions, subtitles, chapters, descriptions, or metadata
     * @param label, Optional label
     */
    public native void addTextTrack( String kind, String label ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.addTextTrack(kind, label);
		}
    }-*/;

    /**
     * Add a text track In addition to the W3C settings we allow adding additional info through options.
     * @param kind, Captions, subtitles, chapters, descriptions, or metadata,
     * @param label, Optional label,
     * @param language, Optional language,
     */
    public native void addTextTrack( String kind, String label, String language ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.addTextTrack(kind, label, language);
		}
    }-*/;

    public native void setAutoPlay( boolean value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.autoplay(value);
		}
    }-*/;

    /**
     * Get a TimeRange object with the times of the video that have been downloaded
     */
    public native double getBuffered() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.buffered();
		}
    }-*/;

    /**
     * Get the percent (as a decimal) of the video that's been downloaded.
     * 0 means none, 1 means all. (This method isn't in the HTML5 spec, but it's very convenient)
     */
    public native double getBufferedPercent() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.bufferedPercent();
		}
		return -100000;
    }-*/;

    /**
     * Allows sub components to stack CSS class names
     */
    public native double getBuildCSSClass() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.buildCSSClass();
		}

		return -100000;
    }-*/;

    /**
     * Get the ending time of the last buffered time range
     *  This is used in the progress bar to encapsulate all time ranges.
     */
    public native double getBufferedEnd() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.bufferedEnd();
		}
		return -100000;
    }-*/;

    public native void exitFullScreen() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.exitFullscreen();
		}
    }-*/;

    /**
     * Set whether or not the controls are showing.
     */
    public native void controls( boolean value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.controls(value);
		}
    }-*/;

    /**
     * Set whether or not the controls are showing.
     */
    public native void setHeight( String value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.height(value);
		}
    }-*/;

    /**
     *Hide the component element if currently showing
     */
    public native void hide() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.hide();
		}
    }-*/;

    /**
     * Hide the component element if currently showing
     */
    public native String id() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.id();
		}
		return "10000000-no-id";
    }-*/;

    /**
     * Check if the player is in fullscreen mode
     */
    public native boolean isFullScreen() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.isFullscreen();
		}
		return false;
    }-*/;

    /**
     * Set whether or not the controls are showing.
     */
    public native void setHeight( String value, boolean skipListeners ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.height(value, skipListeners);
		}
    }-*/;

    public native void setWidth( String value, boolean skipListeners ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.setWidth(value, skipListeners);
		}
    }-*/;

    /**
     * The player's language code
     */
    public native void setLanguage( String value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.setLanguage(value);
		}
    }-*/;

    /**
     * Get the current muted state, or turn mute on or off
     */
    public native void setMuted( boolean value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.muted(value);
		}
    }-*/;

    /**
     * Get the current muted state, or turn mute on or off
     */
    public native boolean isMuted() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.muted();
		}

		return false;
    }-*/;

    /**
     * Get the current muted state, or turn mute on or off
     */
    public native String getName() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.name();
		}

		return "";
    }-*/;

    /**
     * Returns the current state of network activity for the element, from the codes in the list below.
     *   NETWORK_EMPTY (numeric value 0) The element has not yet been initialised. All attributes are in their initial states.
       NETWORK_IDLE (numeric value 1) The element's resource selection algorithm is active and has selected a resource, but it is not actually using the network at this time.
    NETWORK_LOADING (numeric value 2) The user agent is actively trying to download data.
    NETWORK_NO_SOURCE (numeric value 3) The element's resource selection algorithm is active, but it has not yet found a resource to use.
    
     */
    public native String getNetworkState() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.networkState();
		}

		return "";
    }-*/;

    public native boolean isPaused() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.paused();
		}

		return false;
    }-*/;

    /**
     * Gets the current playback rate.
     * @return
     */
    public native double getPlackbackRate() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.playbackRate();
		}

		return -10000;
    }-*/;

    /**
     * Sets the current playback rate.
     * @param value
     */
    public native void setPlackbackRate( double value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.playbackRate(value);
		}
    }-*/;

    public native String getPoster() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.poster();
		}
    }-*/;

    /**
     * Begin loading the src data.
     */
    public native void load() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.load();
		}
    }-*/;

    /**
     * Set whether or not the controls are showing.
     */
    public native boolean hasClass( String value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.hasClass(value);
		}

		return false;

    }-*/;

    /**
     * Set whether or not the controls are showing.
     */
    public native String getCurrentSource() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.currentSrc();
		}
		return null;
    }-*/;

    /**
     * Get the current source type e.g. video/mp4 This can allow you rebuild the current source object so that you could load the same source and tech later
     */
    public native String getCurrentType() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.currentType();
		}
		return null;
    }-*/;

    /**
     * Set both width and height at the same time
     */
    public native void setDimensions( int width, int height ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.dimensions(width, height);
		}
    }-*/;

    /**
     * Start the video playback.
     */
    public native void play() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.play();
		}
    }-*/;

    /**
     * Destroys the video player and does any necessary cleanup.
     * This is especially helpful if you are dynamically adding and removing videos to/from the DOM.
     */
    public native void dispose() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.dispose();
		}
    }-*/;

    /**
     * Destroys the video player and does any necessary cleanup.
     * This is especially helpful if you are dynamically adding and removing videos to/from the DOM.
     */
    public native double getDuration() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.duration();
		}
		return -10000;
    }-*/;

    /**
     * Destroys the video player and does any necessary cleanup.
     * This is especially helpful if you are dynamically adding and removing videos to/from the DOM.
     */
    public native com.google.gwt.dom.client.Element getEl() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.el();
		}
		return null;
    }-*/;

    /**
     * Report user touch activity when touch events occur
    <p>
    User activity is used to determine when controls should show/hide. It's relatively simple when it comes to mouse events, because any mouse event should show the controls. So we capture mouse events that bubble up to the player and report activity when that happens.

    With touch events it isn't as easy. We can't rely on touch events at the player level, because a tap (touchstart + touchend) on the video itself on mobile devices is meant to turn controls off (and on). User activity is checked asynchronously, so what could happen is a tap event on the video turns the controls off, then the touchend event bubbles up to the player, which if it reported user activity, would turn the controls right back on. (We also don't want to completely block touch events from bubbling up)

    Also a touchmove, touch+hold, and anything other than a tap is not supposed to turn the controls back on on a mobile device.

    Here we're setting the default component behavior to report user activity whenever touch events happen, and this can be turned off by components that want touch events to act differently.
     */
    public native void enableTouchActivity() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.enableTouchActivity();
		}
    }-*/;

    public native boolean isEnded() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.ended();
		}
		return false;
    }-*/;

    /**
     * Pause the video playback.
     */
    public native void pause() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.pause();
		}
    }-*/;

    /**
     * Returns the current time of the video in seconds.
     * @return
     */
    public native float getCurrentTime() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.currentTime();
		}
    }-*/;

    /**
     * Returns the current time of the video in seconds.
     * @return
     */
    public native float getRemainingTime() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.remainingTime();
		}
    }-*/;

    /**
     * Returns the current time of the video in seconds.
     * @return
     */
    public native float removeClass( String value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.removeClass(value);
		}
    }-*/;

    public native boolean isSeeking() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.seeking();
		}
    }-*/;

    public native void setVolume( double value ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.volume(value);
		}
    }-*/;

    public native void show() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.show();
		}
    }-*/;

    /**
     * Increase the size of the video to full screen.
     * 
     * In some browsers, full screen is not supported natively, so it enters "full window mode", where the video fills the browser window.
     *  In browsers and devices that support native full screen, sometimes the browser's default controls will be shown, and not the Video.js custom skin. 
     *  This includes most mobile devices (iOS, Android) and older versions of Safari.
     */
    public native float requestFullscreen() /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			return player.requestFullscreen();
		}
    }-*/;

    /**
     * Seek to the supplied time (seconds).
     *
     * @param position
     */
    public native void setCurrentTime( float position ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;

		if (player) {
			player.currentTime(position);
		}
    }-*/;

    /**
     * Check are we using flash fallback for current video.
     * @return
     */
    public native boolean isFlashFallback() /*-{
		var objects = $wnd.document.getElementById(
				this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerId)
				.getElementsByTagName('object');

		return ((objects != null) && (objects.length != 0));
    }-*/;

    /**
     * @param startPosition the startPosition to set
     */
    public void setStartPosition( int startPosition ) {
        this.startPosition = startPosition;
    }

    /**
     * Fired whenever the media begins or resumes playback.
     * @param handler
     */
    public void addPlayHandler( VideoPlayerHandler handler ) {
        addEventHandler( "play", handler );
    }

    public void addErrorHandler( VideoPlayerHandler handler ) {
        addEventHandler( "error", handler );
    }

    public void addFirstPlayHandler( VideoPlayerHandler handler ) {
        addEventHandler( "firstplay", handler );
    }

    public void addFullscreenChangeHandler( VideoPlayerHandler handler ) {
        addEventHandler( "fullscreenchange", handler );
    }

    /**
     * Fired whenever the media has been paused.
     * @param handler
     */
    public void addPauseHandler( VideoPlayerHandler handler ) {
        addEventHandler( "pause", handler );
    }

    /**
     * Fired when the end of the media resource is reached. currentTime == duration
     * @param handler
     */
    public void addEndedHandler( VideoPlayerHandler handler ) {
        addEventHandler( "ended", handler );
    }

    /**
     * Fired when the current playback position has changed.
     * During playback this is fired every 15-250 milliseconds, depending on the playback technology in use.
     *
     * @param handler
     */
    public void addTimeUpdateHandler( VideoPlayerHandler handler ) {
        addEventHandler( "timeupdate", handler );
    }

    /**
     * Fired when the user agent begins looking for media data.
     * @param handler
     */
    public void addLoadStartHandler( VideoPlayerHandler handler ) {
        addEventHandler( "loadstart", handler );
    }

    public void addProgressHandler( VideoPlayerHandler handler ) {
        addEventHandler( "progress", handler );
    }

    public void addResizeHandler( VideoPlayerHandler handler ) {
        addEventHandler( "resize", handler );
    }

    public void addSeekedHandler( VideoPlayerHandler handler ) {
        addEventHandler( "seeked", handler );
    }

    public void addSeekingHandler( VideoPlayerHandler handler ) {
        addEventHandler( "seeking", handler );
    }

    public void addWaitingHaldner( VideoPlayerHandler handler ) {
        addEventHandler( "waiting", handler );
    }

    /**
     * Fired when the player has initial duration and dimension information.
     * @param handler
     */
    public void addLoadedMetadataHandler( VideoPlayerHandler handler ) {
        addEventHandler( "loadedmetadata", handler );
    };

    /**
     * Fired when the player has downloaded data at the current playback position.
     * @param handler
     */
    public void addLoadedDataHandler( VideoPlayerHandler handler ) {
        addEventHandler( "loadeddata", handler );
    }

    /**
     * Fired when the player has finished downloading the source data.
     * @param handler
     */
    public void addLoadedAllDataHandler( VideoPlayerHandler handler ) {
        addEventHandler( "loadedalldata", handler );
    }

    /**
     * Fired when the duration of the media resource is changed, or known for the first time.
     * @param handler
     */
    public void addDurationChangeHandler( VideoPlayerHandler handler ) {
        addEventHandler( "durationchange", handler );
    }

    /**
     * Set skin name.
     *
     * @param skinName the skinName to set
     */
    public void setSkinName( String skinName ) {
        this.skinName = skinName;
    }

    /**
     * Show controls for the player.
     *
     * @param controls the controls to set
     */
    public void setControls( boolean controls ) {
        this.controls = controls;
    }

    /**
     * Set preload type for the player. MediaElement.PRELOAD_NONE by default
     * @param preload the preload to set
     */
    public void setPreload( String preload ) {
        this.preload = preload;
    }

    public void setLoop( boolean loop ) {
        this.loop = loop;
    }

    /**
     * Add source for video tag. Type value could be from class VideoElement
     * @param src
     * @param type
     */
    public void addSource( String src, String type ) {
        sources.add( src );
        sourceType.add( type );
    }

    /**
     * @param poster the poster to set
     */
    public void setPoster( String poster ) {
        this.poster = poster;
    }

    private native void setFlashFallback() /*-{
		$wnd.videojs.options.flash.swf = @com.ait.toolkit.videoplayer.client.VideoPlayer::FALLBACK_SWF;
    }-*/;

    private native JavaScriptObject initPlayer() /*-{
		return $wnd.videojs(
				this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerId,
				{}, function() {
				});
    }-*/;

    private native void addEventHandler( String event, VideoPlayerHandler handler ) /*-{
		var player = this.@com.ait.toolkit.videoplayer.client.VideoPlayer::playerObject;
		var javaPlayer = this;

		if (player) {
			player
					.addEvent(
							event,
							function() {
								handler.@com.ait.toolkit.videoplayer.client.VideoPlayerHandler::handle(Lcom/ait/toolkit/videoplayer/client/VideoPlayer;)(javaPlayer);
							});
		}
    }-*/;
}
