package net.johnkendall.cameraapp;

/**
 * Created by John on 9/07/2015.
 */
public interface FileSendThreadInterface {
    void handleFileSendThreadCompletionFailure(Exception e);
    void handleFileSendThreadCompletionSuccess();
}
