package com.instancy.instancylearning.interfaces;

/**
 * Created by Upendranath on 5/22/2017.
 */

public interface SiteConfigInterface {

    public void preExecuteIn( );
    public void progressUpdateIn(int status);
    public void postExecuteIn(String results);

}
