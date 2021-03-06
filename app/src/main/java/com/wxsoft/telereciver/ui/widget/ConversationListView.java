package com.wxsoft.telereciver.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.event.MessageEvent;
import com.wxsoft.telereciver.ui.fragment.message.MessageContentFragment;
import com.wxsoft.telereciver.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;

public class ConversationListView {

    private View mConvListFragment;
    private ListView mConvListView = null;
    private TextView mTitle;
    private ImageButton mCreateGroup;
    private LinearLayout mSearchHead;
    private LinearLayout mHeader;
    private RelativeLayout mLoadingHeader;
    private ProgressBar mLoadingIv;
    private LinearLayout mLoadingTv;
    private Context mContext;
    private TextView mNull_conversation;
    private LinearLayout mSearch;
    private TextView mAllUnReadMsg;
    private MessageContentFragment mFragment;

    public ConversationListView(View view, Context context, MessageContentFragment fragment) {
        this.mConvListFragment = view;
        this.mContext = context;
        this.mFragment = fragment;
    }

    public void initModule() {
        mConvListView = (ListView) mConvListFragment.findViewById(R.id.conv_list_view);
//        mCreateGroup = (ImageButton) mConvListFragment.findViewById(R.id.create_group_btn);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mHeader = (LinearLayout) inflater.inflate(R.layout.conv_list_head_view, mConvListView, false);
//        mSearchHead = (LinearLayout) inflater.inflate(R.layout.conversation_head_view, mConvListView, false);
        mLoadingHeader = (RelativeLayout) inflater.inflate(R.layout.jmui_drop_down_list_header, mConvListView, false);
        mLoadingIv = (ProgressBar) mLoadingHeader.findViewById(R.id.jmui_loading_img);
        mLoadingTv = (LinearLayout) mLoadingHeader.findViewById(R.id.loading_view);
//        mSearch = (LinearLayout) mSearchHead.findViewById(R.id.search_title);
        mNull_conversation = (TextView) mConvListFragment.findViewById(R.id.null_conversation);
//        mAllUnReadMsg = (TextView) mFragment.getActivity().findViewById(R.id.all_unread_number);
        mConvListView.addHeaderView(mLoadingHeader);
//        mConvListView.addHeaderView(mSearchHead);
//        mConvListView.addHeaderView(mHeader);
    }

    public void setConvListAdapter(ListAdapter adapter) {
        mConvListView.setAdapter(adapter);
    }


    public void setListener(View.OnClickListener onClickListener) {
//        mSearch.setOnClickListener(onClickListener);
//        mCreateGroup.setOnClickListener(onClickListener);
    }

    public void setItemListeners(AdapterView.OnItemClickListener onClickListener) {
        mConvListView.setOnItemClickListener(onClickListener);
    }

    public void setLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mConvListView.setOnItemLongClickListener(listener);
    }


//    public void showHeaderView() {
//        mHeader.findViewById(R.id.network_disconnected_iv).setVisibility(View.VISIBLE);
//        mHeader.findViewById(R.id.check_network_hit).setVisibility(View.VISIBLE);
//    }

//    public void dismissHeaderView() {
//        mHeader.findViewById(R.id.network_disconnected_iv).setVisibility(View.GONE);
//        mHeader.findViewById(R.id.check_network_hit).setVisibility(View.GONE);
//    }


    public void showLoadingHeader() {
        mLoadingIv.setVisibility(View.VISIBLE);
        mLoadingTv.setVisibility(View.VISIBLE);
//        AnimationDrawable drawable = (AnimationDrawable) mLoadingIv.getDrawable();
//        drawable.start();
    }

    public void dismissLoadingHeader() {
        mLoadingIv.setVisibility(View.GONE);
        mLoadingTv.setVisibility(View.GONE);
    }

    public void setNullConversation(boolean isHaveConv) {
        if (isHaveConv) {
            mNull_conversation.setVisibility(View.GONE);
        } else {
            mNull_conversation.setVisibility(View.VISIBLE);
        }
    }


    public void setUnReadMsg(final int count) {
        ThreadUtil.runInUiThread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new MessageEvent(count));
//                if (mAllUnReadMsg != null) {
//                    if (count > 0) {
//                        mAllUnReadMsg.setVisibility(View.VISIBLE);
//                        if (count < 100) {
//                            mAllUnReadMsg.setText(count + "");
//                        } else {
//                            mAllUnReadMsg.setText("99+");
//                        }
//                    } else {
//                        mAllUnReadMsg.setVisibility(View.GONE);
//                    }
//                }
            }
        });
    }
}
