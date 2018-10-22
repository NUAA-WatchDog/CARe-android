package cn.zjt.iot.oncar.android.Model;

/**
 * @author Mr Dk.
 * @since 2018.4.20
 * @version 2018.6.12
 */

public class Temperature {
	
	private long Temperature_time;
	private String Temperature_record;
	
	public long getTemperature_time() {
		return Temperature_time;
	}
	public void setTemperature_time(long temperature_time) {
		Temperature_time = temperature_time;
	}
	public String getTemperature_record() {
		return Temperature_record;
	}
	public void setTemperature_record(String temperature_record) {
		Temperature_record = temperature_record;
	}
}
