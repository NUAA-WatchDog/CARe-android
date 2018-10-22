package cn.zjt.iot.oncar.android.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zjt.iot.oncar.android.Activity.AboutActivity;
import cn.zjt.iot.oncar.android.Activity.FeedbackActivity;
import cn.zjt.iot.oncar.android.Activity.LogInActivity;
import cn.zjt.iot.oncar.android.Activity.MainActivity;
import cn.zjt.iot.oncar.android.Activity.UpdateHeightActivity;
import cn.zjt.iot.oncar.android.Activity.UpdateNicknameActivity;
import cn.zjt.iot.oncar.android.Adapter.MenuAdapter;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Model.User;

/**
 * @author Mr Dk.
 * @version 2018.5.20
 * @see MainActivity
 * @see UpdateNicknameActivity
 * @see UpdateHeightActivity
 * @see FeedbackActivity
 * @see AboutActivity
 * @since 2018.4.24
 */

public class MineSettingFragment extends Fragment {

    /**
     * @Variables
     */

    /*
     * @Variable ListViews
     */
    private ListView listViewMenu;
    private ListView listViewLogOut;

    /*
     * @Variable user model
     */
    private User user;

    /**
     * @Override
     */

    /*
     * @Override onDestroyView
     * @Return void
     * @Function Destroy Views of this Fragment
     */
    @Override
    public void onDestroyView() {
        listViewMenu = null;
        listViewLogOut = null;
        user = null;
        super.onDestroyView();
    }

    /*
     * @Override onActivityResult
     * @Param int requestCode
     * @Param int resultCode
     * @Param Intent data
     * @Return void
     * @Function Refactor the user model after Internet operation
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstArgument.REQUEST_UPDATE_NICKNAME &&
                resultCode == ConstArgument.RESPONSE_UPDATE_NICKNAME) {
            user.setUser_nickname(data.getStringExtra("nickname"));
        }
        if (requestCode == ConstArgument.REQUEST_UPDATE_HEIGHT &&
                resultCode == ConstArgument.RESPONSE_UPDATE_HEIGHT) {
            user.setUser_height(data.getIntExtra("height", 0));
        }
    }

    /*
     * @Override onCreateView
     * @Param LayoutInflater inflater
     * @Param ViewGroup container
     * @Param Bundle savedInstanceState
     * @Return View
     * @Function Create Views of this Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine_setting, null);

        InitView(view);
        InitListViewMenu();
        InitListViewLogOut();
        SetOnItemClickListener();

        return view;
    }

    /**
     * @Methods
     */

    /*
     * @Setter setUser
     * @Return void
     */
    public void setUser(User user) {
        this.user = user;
    }

    /*
     * @Method InitView
     * @Param View view
     * @Return void
     * @Function Initializing Views of this Activity
     */
    private void InitView(View view) {
        listViewMenu = (ListView) view.findViewById(R.id.listViewMenu);
        listViewLogOut = (ListView) view.findViewById(R.id.listViewLogOut);
    }

    /*
     * @Method InitListViewMenu
     * @Return void
     * @Function Initializing the menu ListView
     */
    private void InitListViewMenu() {
        List<Map<String, Object>> AllValues = new ArrayList<>();
        for (int i = 0; i < ConstArgument.MenuText.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("listViewMenuItem", ConstArgument.MenuText[i]);
            map.put("listViewMenuIcon", ConstArgument.MenuIcon[i]);
            AllValues.add(map);
        }

        MenuAdapter menuAdapter = new MenuAdapter(getContext(), AllValues);
        listViewMenu.setAdapter(menuAdapter);
    }

    /*
     * @Method InitListViewLogOut
     * @Return void
     * @Function Initializing the log out ListView
     */
    private void InitListViewLogOut() {
        List<Map<String, Object>> AllValues = new ArrayList<>();
        for (int i = 0; i < ConstArgument.MenuOutText.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("listViewMenuItem", ConstArgument.MenuOutText[i]);
            map.put("listViewMenuIcon", ConstArgument.MenuOutIcon[i]);
            AllValues.add(map);
        }

        MenuAdapter menuAdapter = new MenuAdapter(getContext(), AllValues);
        listViewLogOut.setAdapter(menuAdapter);
    }

    /*
     * @Method SetOnItemClickListener
     * @Return void
     * @Function Set events for items in ListViews
     */
    private void SetOnItemClickListener() {

        listViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(getContext(), UpdateNicknameActivity.class);
                        intent.putExtra("id", user.getUser_id());
                        intent.putExtra("nickname", user.getUser_nickname());
                        getActivity().startActivityForResult(intent, ConstArgument.REQUEST_UPDATE_NICKNAME);
                        break;
                    case 1:
                        intent = new Intent(getContext(), UpdateHeightActivity.class);
                        intent.putExtra("id", user.getUser_id());
                        intent.putExtra("height", user.getUser_height());
                        getActivity().startActivityForResult(intent, ConstArgument.REQUEST_UPDATE_HEIGHT);
                        break;
                    case 2:
                        intent = new Intent(getContext(), FeedbackActivity.class);
                        getActivity().startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(getContext(), AboutActivity.class);
                        getActivity().startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

        listViewLogOut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                    builder.setIcon(R.drawable.logout);
                    builder.setMessage("确认要注销当前用户吗？");
                    builder.setNegativeButton(ConstArgument.ALERT_CANCEL, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    builder.setPositiveButton(ConstArgument.ALERT_SURE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), LogInActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });

                    builder.create().show();
                }
            }
        });
    }

}
