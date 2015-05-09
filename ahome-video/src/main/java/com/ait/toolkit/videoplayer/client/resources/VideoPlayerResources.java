package com.ait.toolkit.videoplayer.client.resources;

import static com.ait.toolkit.videoplayer.client.VideoPlayer.VERSION;
import static com.google.gwt.core.client.GWT.create;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;

public interface VideoPlayerResources extends ClientBundle {

    public static final VideoPlayerResources INSTANCE = create( VideoPlayerResources.class );

    @Source( VERSION + "/video-js.css" )
    CssResource css();

    @Source( VERSION + "/video.js" )
    TextResource js();

}
