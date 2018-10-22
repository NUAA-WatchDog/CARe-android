package cn.zjt.iot.oncar.android.Filter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Mr Dk.
 * @version 2018.6.12
 * @since 2018.6.12
 */

/*
 * @Implements FileFilter
 * @Function To filter in the file system searching the model of user in most updated version
 */

public class ModelTemperatureFileFilter implements FileFilter {

    /**
     * @Variables
     */
    private int id;

    /**
     * @Methods
     */

    /*
     * @Constructor ModelTemperatureFileFilter
     * @Param int id
     */
    public ModelTemperatureFileFilter(int id) {
        this.id = id;
    }

    /**
     * @Override
     */

    /*
     * @Override accept
     * @Param File pathname
     * @Return boolean
     * @See MODEL NAME : "model_temperature_id_version.txt"
     */
    @Override
    public boolean accept(File pathname) {
        String fileName = pathname.getName();
        return (fileName.startsWith("model_temperature_" + id + "_") && fileName.endsWith(".txt"));
    }
}
