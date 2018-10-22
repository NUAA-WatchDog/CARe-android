package cn.zjt.iot.oncar.android.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zjt.iot.oncar.android.Activity.CreateEMERContactActivity;
import cn.zjt.iot.oncar.android.Activity.MainActivity;
import cn.zjt.iot.oncar.android.Activity.UpdateEMERContactActivity;
import cn.zjt.iot.oncar.android.Adapter.MenuAdapter;
import cn.zjt.iot.oncar.R;
import cn.zjt.iot.oncar.android.Util.ConstArgument;
import cn.zjt.iot.oncar.android.Model.User;

/**
 * @author Mr Dk.
 * @version 2018.5.21
 * @see MainActivity
 * @see UpdateEMERContactActivity
 * @since 2018.4.22
 */

public class EmergencyCallFragment extends Fragment {

    /**
     * @Variables
     */

    /*
     * @Variable User model
     */
    private User user;

    /*
     * @Variable Call buttons
     */
    private ImageButton imageButtonNumberOne;
    private ImageButton imageButtonNumberTwo;
    private ImageButton imageButtonNumberThree;
    private ImageButton imageButtonNumberFour;
    private ImageButton imageButtonNumberFive;
    private ImageButton imageButtonNumberSix;
    private ImageButton imageButtonNumberSeven;
    private ImageButton imageButtonNumberEight;
    private ImageButton imageButtonNumberNine;
    private ImageButton imageButtonNumberZero;
    private ImageButton imageButtonNumberLeft;
    private ImageButton imageButtonNumberRight;
    private ImageButton imageButtonCall;
    private ImageView imageButtonNumberDelete;
    private TableLayout buttonDeleteNumber;
    private TextView textViewCurrentPhone;
    private StringBuffer currentPhone = new StringBuffer();

    /*
     * @Variable ListViews displaying emergency contacts
     */
    private ListView listViewEMERcall;
    private ListView listViewSOScall;
    private MenuAdapter EMERNumberAdapter;
    private List<Map<String, Object>> AllEMER;
    private List<Map<String, Object>> AllSOS;

    /**
     * @Override
     */

    /*
     * @Override onDestroyView
     * @Return void
     * @Function To destroy this Fragment
     */
    @Override
    public void onDestroyView() {
        user = null;
        imageButtonNumberOne = null;
        imageButtonNumberTwo = null;
        imageButtonNumberThree = null;
        imageButtonNumberFour = null;
        imageButtonNumberFive = null;
        imageButtonNumberSix = null;
        imageButtonNumberSeven = null;
        imageButtonNumberEight = null;
        imageButtonNumberNine = null;
        imageButtonNumberZero = null;
        imageButtonNumberLeft = null;
        imageButtonNumberRight = null;
        imageButtonCall = null;
        imageButtonNumberDelete = null;
        buttonDeleteNumber = null;
        textViewCurrentPhone = null;
        currentPhone = null;
        listViewEMERcall = null;
        listViewSOScall = null;
        EMERNumberAdapter = null;
        AllEMER.clear();
        AllSOS.clear();
        AllEMER = null;
        AllSOS = null;

        super.onDestroyView();
    }

    /*
     * @Override onActivityResult
     * @Param int requestCode
     * @Param int resultCode
     * @Param Intent data
     * @Return void
     * @Function Refactor the user model after Internet operation
     * @Function Reload the data in ListViews
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstArgument.REQUEST_CREATE_CONTACT &&
                resultCode == ConstArgument.RESPONSE_CREATE_CONTACT) {
            if (data.getStringExtra("ContactStatus").equals("00")) {
                user.setUser_EMERcontact_1(data.getStringExtra("EMERContact_1"));
            } else if (data.getStringExtra("ContactStatus").equals("01")) {
                user.setUser_EMERcontact_1(data.getStringExtra("EMERContact_1"));
            } else if (data.getStringExtra("ContactStatus").equals("10")) {
                user.setUser_EMERcontact_2(data.getStringExtra("EMERContact_2"));
            }

            AllEMER.clear();
            DataIntoListView();
            EMERNumberAdapter.notifyDataSetChanged();

        } else if (requestCode == ConstArgument.REQUEST_UPDATE_CONTACT &&
                resultCode == ConstArgument.RESPONSE_UPDATE_CONTACT) {

            int updateTag = data.getIntExtra("updateTag", -1);
            if (updateTag == 1) {
                user.setUser_EMERcontact_1(data.getStringExtra("updateContact"));
                AllEMER.clear();
                DataIntoListView();
                EMERNumberAdapter.notifyDataSetChanged();
            } else if (updateTag == 2) {
                user.setUser_EMERcontact_2(data.getStringExtra("updateContact"));
                AllEMER.clear();
                DataIntoListView();
                EMERNumberAdapter.notifyDataSetChanged();
            }
        }
    }

    /*
     * @Override onCreateView
     * @Param LayoutInflater inflater
     * @Param ViewGroup container
     * @Param Bundle savedInstanceState
     * @Return View
     * @Function To create this Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_call, null);

        InitViews(view);
        InitializeSOSListView();
        InitializeEMERListView();
        PhoneNumberListeners();
        ListViewListeners();

        return view;
    }

    /**
     * @Methods
     */

    /*
     * @Setter setUser
     * @Param User user
     * @Return void
     */
    public void setUser(User user) {
        this.user = user;
    }

    /*
     * @Method InitViews
     * @Param View view
     * @Return void
     * @Function To initialize Views int this Fragment
     */
    private void InitViews(View view) {
        imageButtonNumberOne = (ImageButton) view.findViewById(R.id.imageButtonNumberOne);
        imageButtonNumberTwo = (ImageButton) view.findViewById(R.id.imageButtonNumberTwo);
        imageButtonNumberThree = (ImageButton) view.findViewById(R.id.imageButtonNumberThree);
        imageButtonNumberFour = (ImageButton) view.findViewById(R.id.imageButtonNumberFour);
        imageButtonNumberFive = (ImageButton) view.findViewById(R.id.imageButtonNumberFive);
        imageButtonNumberSix = (ImageButton) view.findViewById(R.id.imageButtonNumberSix);
        imageButtonNumberSeven = (ImageButton) view.findViewById(R.id.imageButtonNumberSeven);
        imageButtonNumberEight = (ImageButton) view.findViewById(R.id.imageButtonNumberEight);
        imageButtonNumberNine = (ImageButton) view.findViewById(R.id.imageButtonNumberNine);
        imageButtonNumberZero = (ImageButton) view.findViewById(R.id.imageButtonNumberZero);
        imageButtonNumberLeft = (ImageButton) view.findViewById(R.id.imageButtonNumberLeft);
        imageButtonNumberRight = (ImageButton) view.findViewById(R.id.imageButtonNumberRight);
        imageButtonCall = (ImageButton) view.findViewById(R.id.imageButtonCall);
        imageButtonNumberDelete = (ImageView) view.findViewById(R.id.imageButtonNumberDelete);
        buttonDeleteNumber = (TableLayout) view.findViewById(R.id.buttonDeleteNumber);
        textViewCurrentPhone = (TextView) view.findViewById(R.id.textViewCurrentPhone);
        listViewSOScall = (ListView) view.findViewById(R.id.listViewSOScall);
        listViewEMERcall = (ListView) view.findViewById(R.id.listViewEMERcall);
    }

    /*
     * @Method PhoneNumberListeners
     * @Return void
     * @Function To set events for calling buttons
     */
    private void PhoneNumberListeners() {

        imageButtonNumberOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("1");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("2");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("3");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("4");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("5");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("6");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("7");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("8");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("9");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("0");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("*");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        imageButtonNumberRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonNumberDelete.setVisibility(View.VISIBLE);
                currentPhone.append("#");
                textViewCurrentPhone.setText(currentPhone);
            }
        });

        buttonDeleteNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPhone.length() > 0) {
                    currentPhone = currentPhone.deleteCharAt(currentPhone.length() - 1);
                    textViewCurrentPhone.setText(currentPhone);
                    if (currentPhone.length() == 0) {
                        imageButtonNumberDelete.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        buttonDeleteNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (currentPhone.length() > 0) {
                    currentPhone.delete(0, currentPhone.length());
                    textViewCurrentPhone.setText(currentPhone);
                    imageButtonNumberDelete.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

        imageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewCurrentPhone.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "号码为空", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                    builder.setIcon(R.drawable.call_icon);
                    builder.setMessage("确认呼叫：" + textViewCurrentPhone.getText().toString() + " 吗？");
                    builder.setNegativeButton(ConstArgument.ALERT_CANCEL, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    builder.setPositiveButton(ConstArgument.ALERT_SURE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + textViewCurrentPhone.getText().toString()));
                            startActivity(intent);
                        }
                    });

                    builder.create().show();
                }
            }
        });
    }

    /*
     * @Method InitializeSOSListView
     * @Return void
     * @Function To initialize the SOS ListView
     */
    private void InitializeSOSListView() {

        AllSOS = new ArrayList<>();
        for (int i = 0; i < ConstArgument.SOSIcon.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("listViewMenuItem", ConstArgument.SOSItem[i]);
            map.put("listViewMenuIcon", ConstArgument.SOSIcon[i]);
            AllSOS.add(map);
        }

        MenuAdapter SOSAdapter = new MenuAdapter(getContext(), AllSOS);
        listViewSOScall.setAdapter(SOSAdapter);
    }

    /*
     * @Method InitializeEMERListView
     * @Return void
     * @Function To initialize the emergency contacts ListView
     */
    private void InitializeEMERListView() {
        AllEMER = new ArrayList<>();
        DataIntoListView();

        EMERNumberAdapter = new MenuAdapter(getContext(), AllEMER);
        listViewEMERcall.setAdapter(EMERNumberAdapter);
    }

    /*
     * @Method DataIntoListView
     * @Return void
     * @Function Putting data into Adapter for EMERListView
     */
    private void DataIntoListView() {
        if (user.getUser_EMERcontact_1() != null) {
            if (!user.getUser_EMERcontact_1().isEmpty()) {
                Map<String, Object> map = new HashMap<>();
                map.put("listViewMenuItem", user.getUser_EMERcontact_1());
                map.put("listViewMenuIcon", R.drawable.emer_contact);
                map.put("isContact", true);
                AllEMER.add(map);
            }
        }
        if (user.getUser_EMERcontact_2() != null && !user.getUser_EMERcontact_2().isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("listViewMenuItem", user.getUser_EMERcontact_2());
            map.put("listViewMenuIcon", R.drawable.emer_contact);
            map.put("isContact", true);
            AllEMER.add(map);
        }
        if (AllEMER.size() < 2) {
            Map<String, Object> map = new HashMap<>();
            map.put("listViewMenuItem", "添加紧急联系人");
            map.put("listViewMenuIcon", R.drawable.add_emer);
            map.put("isContact", false);
            AllEMER.add(map);
        }
    }

    /*
     * @Method ListViewListeners
     * @Return void
     * @Function To set events for ListViews
     */
    private void ListViewListeners() {

        // Start a call to emergency contacts
        listViewEMERcall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (!(Boolean) AllEMER.get(position).get("isContact")) {
                    // Create a contact
                    Intent intent = new Intent(getContext(), CreateEMERContactActivity.class);

                    if ((user.getUser_EMERcontact_1() == null || user.getUser_EMERcontact_1().isEmpty()) &&
                            (user.getUser_EMERcontact_2() == null || user.getUser_EMERcontact_2().isEmpty())) {
                        intent.putExtra("ContactStatus", "00");
                    } else if (user.getUser_EMERcontact_1() == null ||
                            user.getUser_EMERcontact_1().isEmpty()) {
                        intent.putExtra("ContactStatus", "01");
                    } else if (user.getUser_EMERcontact_2() == null ||
                            user.getUser_EMERcontact_2().isEmpty()) {
                        intent.putExtra("ContactStatus", "10");
                    }

                    intent.putExtra("id", user.getUser_id());
                    intent.putExtra("EMERContact_1", user.getUser_EMERcontact_1());
                    intent.putExtra("EMERContact_2", user.getUser_EMERcontact_2());
                    getActivity().startActivityForResult(intent, ConstArgument.REQUEST_CREATE_CONTACT);

                } else {
                    // Start a call
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                    builder.setIcon(R.drawable.call_icon);
                    builder.setMessage("确认呼叫：" + AllEMER.get(position).get("listViewMenuItem") + " 吗？");
                    builder.setNegativeButton(ConstArgument.ALERT_CANCEL, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //
                        }
                    });
                    builder.setPositiveButton(ConstArgument.ALERT_SURE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + AllEMER.get(position).get("listViewMenuItem")));
                            startActivity(intent);
                        }
                    });

                    builder.create().show();
                }
            }
        });

        // Update contacts
        listViewEMERcall.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if ((Boolean) AllEMER.get(position).get("isContact")) {

                    Intent intent = new Intent(getContext(), UpdateEMERContactActivity.class);
                    intent.putExtra("id", user.getUser_id());

                    if (position == 1) {
                        // Update contact_2
                        intent.putExtra("updateTag", 2);
                        intent.putExtra("EMERcontact_2", user.getUser_EMERcontact_2());
                    } else {
                        // position == 0
                        if (!(Boolean) AllEMER.get(1).get("isContact")) {
                            // One contact
                            if (user.getUser_EMERcontact_2() == null || user.getUser_EMERcontact_2().isEmpty()) {
                                // Update contact_1
                                intent.putExtra("updateTag", 1);
                                intent.putExtra("EMERcontact_1", user.getUser_EMERcontact_1());
                            } else {
                                // Update contact_2
                                intent.putExtra("updateTag", 2);
                                intent.putExtra("EMERcontact_2", user.getUser_EMERcontact_2());
                            }
                        } else {
                            // Two contact
                            // Update contact_1
                            intent.putExtra("updateTag", 1);
                            intent.putExtra("EMERcontact_1", user.getUser_EMERcontact_1());
                        }
                    }

                    getActivity().startActivityForResult(intent, ConstArgument.REQUEST_UPDATE_CONTACT);
                }
                return true;
            }
        });

        // Start a SOS call
        listViewSOScall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(ConstArgument.ALERT_TITLE_CHECK);
                builder.setIcon(R.drawable.call_icon);
                builder.setMessage("确认呼叫：" + AllSOS.get(position).get("listViewMenuItem") + " 吗？");
                builder.setNegativeButton(ConstArgument.ALERT_CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                builder.setPositiveButton(ConstArgument.ALERT_SURE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + AllSOS.get(position).get("listViewMenuItem")));
                        startActivity(intent);
                    }
                });

                builder.create().show();
            }
        });
    }

}
