package com.wxsoft.teleconsultation.ui.controller;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.huawei.ecs.mip.msg.ConfUserInfoNotify;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.ui.activity.ChatActivity;
import com.wxsoft.teleconsultation.ui.adapter.ConversationListAdapter;
import com.wxsoft.teleconsultation.ui.fragment.message.MessageContentFragment;
import com.wxsoft.teleconsultation.ui.widget.ConversationListView;
import com.wxsoft.teleconsultation.util.DialogCreator;
import com.wxsoft.teleconsultation.util.SortConvList;
import com.wxsoft.teleconsultation.util.SortTopConvList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

public class ConversationListController implements View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private ConversationListView mConvListView;
    private MessageContentFragment mContext;
    private int mWidth;
    private ConversationListAdapter mListAdapter;
    private List<Conversation> mDatas = new ArrayList<Conversation>();
    private Dialog mDialog;

    public ConversationListController(ConversationListView listView, MessageContentFragment context,
                                      int width) {
        this.mConvListView = listView;
        this.mContext = context;
        this.mWidth = width;
        initConvListAdapter();
    }

    List<Conversation> topConv = new ArrayList<>();
    List<Conversation> forCurrent = new ArrayList<>();
    List<Conversation> delFeedBack = new ArrayList<>();

    private void initConvListAdapter() {
        forCurrent.clear();
        topConv.clear();
        delFeedBack.clear();
        int i = 0;
        mDatas = JMessageClient.getConversationList();
        if (mDatas != null && mDatas.size() > 0) {
            mConvListView.setNullConversation(true);
            SortConvList sortConvList = new SortConvList();
            Collections.sort(mDatas, sortConvList);
            for (Conversation con : mDatas) {
                if (con.getTargetId().equals("feedback_Android")) {
                    delFeedBack.add(con);
                }
                if (!TextUtils.isEmpty(con.getExtra())) {
                    forCurrent.add(con);
                }
            }
            topConv.addAll(forCurrent);
            mDatas.removeAll(forCurrent);
            mDatas.removeAll(delFeedBack);

        } else {
            mConvListView.setNullConversation(false);
        }
        if (topConv != null && topConv.size() > 0) {
            SortTopConvList top = new SortTopConvList();
            Collections.sort(topConv, top);
            for (Conversation conv : topConv) {
                mDatas.add(i, conv);
                i++;
            }
        }
        mListAdapter = new ConversationListAdapter(mContext.getActivity(), mDatas, mConvListView);
        mConvListView.setConvListAdapter(mListAdapter);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击会话条目
        Intent intent = new Intent();
        if (position > 0) {
//            //这里-1是减掉添加的一个headView
            Conversation conv = mDatas.get(position - 1);
            intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE, conv.getTitle());
//            //群聊
            if (conv.getType() == ConversationType.group) {
                long groupId = ((GroupInfo) conv.getTargetInfo()).getGroupID();
                intent.putExtra(ChatActivity.EXTRA_KEY_GROUP_ID, groupId);
                intent.putExtra(ChatActivity.EXTRA_KEY_DRAFT, getAdapter().getDraft(conv.getId()));
                intent.setClass(mContext.getActivity(), ChatActivity.class);
                mContext.getActivity().startActivity(intent);
                return;
                //单聊
            }else if(conv.getType() == ConversationType.single){
                intent.putExtra(ChatActivity.EXTRA_KEY_CONV_TITLE,conv.getTitle());
                intent.putExtra(ChatActivity.EXTRA_KEY_SINGE,true);
                intent.putExtra(ChatActivity.EXTRA_KEY_CONV,((UserInfo)conv.getTargetInfo()).getUserName());
                intent.setClass(mContext.getActivity(), ChatActivity.class);
                mContext.getActivity().startActivity(intent);
                return;
            }
        }
    }

    public ConversationListAdapter getAdapter() {
        return mListAdapter;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Conversation conv = mDatas.get(position - 1);
        if (conv != null) {
            View.OnClickListener listener = v -> {
                switch (v.getId()) {
                    //会话置顶
                    case R.id.jmui_top_conv_ll:
                        //已经置顶,去取消
                        if (!TextUtils.isEmpty(conv.getExtra())) {
                            mListAdapter.setCancelConvTop(conv);
                            //没有置顶,去置顶
                        } else {
                            mListAdapter.setConvTop(conv);
                        }
                        mDialog.dismiss();
                        break;
                    //删除会话
                    case R.id.jmui_delete_conv_ll:
                        if (conv.getType() == ConversationType.group) {
                            JMessageClient.deleteGroupConversation(((GroupInfo) conv.getTargetInfo()).getGroupID());
                        } else {
                            JMessageClient.deleteSingleConversation(((UserInfo) conv.getTargetInfo()).getUserName());
                        }
                        mDatas.remove(position - 1);
                        if (mDatas.size() > 0) {
                            mConvListView.setNullConversation(true);
                        } else {
                            mConvListView.setNullConversation(false);
                        }
                        mListAdapter.notifyDataSetChanged();
                        mDialog.dismiss();
                        break;
                    default:
                        break;
                }

            };
            mDialog = DialogCreator.createDelConversationDialog(mContext.getActivity(), listener, TextUtils.isEmpty(conv.getExtra()));
            mDialog.show();
            mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        }
        return true;
    }
}
