package cn.zjt.iot.oncar.android.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.GetScreenSizeUtil;
import cn.zjt.iot.oncar.android.Fragment.EmergencyCallFragment;
import cn.zjt.iot.oncar.android.Fragment.MineSettingFragment;

/**
 * @author Mr Dk.
 * @version 2018.4.26
 * @see EmergencyCallFragment
 * @see MineSettingFragment
 * @since 2018.4.26
 */

public class MenuAdapter extends BaseAdapter {

    /**
     * @Variables
     */
    private Context context;
    private List<Map<String, Object>> AllValues;

    /**
     * @Methods
     */

    /*
     * @Constructor MenuAdapter
     * @Param Context context
     * @Param List<Map<String, Object>> allValues
     */
    public MenuAdapter(Context context, List<Map<String, Object>> allValues) {
        this.context = context;
        AllValues = allValues;
    }

    /**
     * @Override
     */

    /*
     * @Override getCount
     * @Return int
     */
    @Override
    public int getCount() {
        return AllValues.size();
    }

    /*
     * @Override getItem
     * @Param int position
     * @Return Object
     */
    @Override
    public Object getItem(int position) {
        return AllValues.get(position);
    }

    /*
     * @Override getItemId
     * @Param int position
     * @Return long
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * @Override getView
     * @Param int position
     * @Param View convertView
     * @Param ViewGroup parent
     * @Return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_menu, null);
            convertView.setBackgroundColor(Color.parseColor("#ff212121"));
            convertView.setLayoutParams(
                    new AbsListView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            GetScreenSizeUtil.GetScreenHeight(context) / 8));
        }

        TextView textViewMenuItem = (TextView) convertView.findViewById(R.id.textViewMenuItem);
        ImageView imageViewMenuIcon = (ImageView) convertView.findViewById(R.id.imageViewMenuIcon);

        Map<String, Object> map = AllValues.get(position);

        textViewMenuItem.setText(map.get("listViewMenuItem").toString());
        imageViewMenuIcon.setImageResource((int) map.get("listViewMenuIcon"));

        return convertView;
    }
}
