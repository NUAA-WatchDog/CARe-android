package cn.zjt.iot.oncar.android.Filter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Mr Dk.
 * @version 2018.5.17
 * @since 2018.5.17
 */

/*
 * @Implements FileFilter
 * @Function To filter in the file system searching the model of user in most updated version
 */

public class ModelHeartRateFileFilter implements FileFilter {

    /**
     * @Variables
     */
    private int id;

    /**
     * @Methods
     */

    /*
     * @Constructor ModelHeartRateFileFilter
     * @Param int id
     */
    public ModelHeartRateFileFilter(int id) {
        this.id = id;
    }

    /**
     * @Override
     */

    /*
     * @Override accept
     * @Param File pathname
     * @Return boolean
     * @See MODEL NAME : "model_heart_rate_id_version.txt"
     */
    @Override
    public boolean accept(File pathname) {
        String fileName = pathname.getName();
        return (fileName.startsWith("model_heart_rate_" + id + "_") && fileName.endsWith(".txt"));
    }
}
