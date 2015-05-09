package com.ait.toolkit.videoplayer.client;

import com.ait.toolkit.core.client.JsObject;
import com.ait.toolkit.core.client.JsoHelper;

public class VideoSource extends JsObject {

    public VideoSource( String type, String source ) {
        jsObj = JsoHelper.createObject();
        JsoHelper.setAttribute( jsObj, "type", type );
        JsoHelper.setAttribute( jsObj, "src", source );
    }
}
