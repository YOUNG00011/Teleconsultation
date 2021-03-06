package com.wxsoft.telereciver.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.helper.SharedPreferencesHelper;
import com.wxsoft.telereciver.ui.widget.ConversationListView;
import com.wxsoft.telereciver.ui.widget.SwipeLayoutConv;
import com.wxsoft.telereciver.util.SortConvList;
import com.wxsoft.telereciver.util.SortTopConvList;
import com.wxsoft.telereciver.util.ThreadUtil;
import com.wxsoft.telereciver.util.TimeFormat;
import com.wxsoft.telereciver.util.ViewHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class ConversationListAdapter extends BaseAdapter {

    private List<Conversation> mDatas;
    private Activity mContext;
    private Map<String, String> mDraftMap = new HashMap<>();
    private UIHandler mUIHandler = new UIHandler(this);
    private static final int REFRESH_CONVERSATION_LIST = 0x3003;
    private SparseBooleanArray mArray = new SparseBooleanArray();
    private SparseBooleanArray mAtAll = new SparseBooleanArray();
    private HashMap<Conversation, Integer> mAtConvMap = new HashMap<>();
    private HashMap<Conversation, Integer> mAtAllConv = new HashMap<>();
    private UserInfo mUserInfo;
    private GroupInfo mGroupInfo;
    private ConversationListView mConversationListView;

    public ConversationListAdapter(Activity context, List<Conversation> data, ConversationListView convListView) {
        this.mContext = context;
        this.mDatas = data;
        this.mConversationListView = convListView;
    }

    /**
     * ??????????????????????????????
     *
     * @param conv ??????????????????
     */
    public void setToTop(Conversation conv) {
        int oldCount = 0;
        int newCount = 0;
        ThreadUtil.runInUiThread(new Runnable() {
            @Override
            public void run() {
                mConversationListView.setNullConversation(true);
            }
        });

        //?????????????????????
        for (Conversation conversation : mDatas) {
            if (conv.getId().equals(conversation.getId())) {
                //??????????????????,????????????????????????,?????????list??????????????????
                if (!TextUtils.isEmpty(conv.getExtra())) {
                    mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                    //???????????????return???,??????????????????for?????????????????????,????????????????????????
                    return;
                    //?????????????????????,?????????????????????????????????????????????,?????????????????????????????????,??????????????????
                } else {
                    //?????????????????????????????????,???????????????
                    mDatas.remove(conversation);
                    //???????????????,????????????????????????????????????.????????????(????????????????????????????????????????????????);??????????????????????????????????????????
                    //???????????????,???????????????????????????,??????????????????.?????????????????????.

                    //????????????,????????????????????????????????????list????????????????????????,???????????????????????????????????????????????????????????????
                    //??????????????????????????????????????????.????????????????????????,??????????????????????????????.
                    for (int i = mDatas.size(); i > SharedPreferencesHelper.getCancelTopSize(); i--) {
                        if (conv.getLatestMessage() != null && mDatas.get(i - 1).getLatestMessage() != null) {
                            if (conv.getLatestMessage().getCreateTime() > mDatas.get(i - 1).getLatestMessage().getCreateTime()) {
                                oldCount = i - 1;
                            } else {
                                oldCount = i;
                                break;
                            }
                        } else {
                            oldCount = i;
                        }
                    }
                    mDatas.add(oldCount, conv);
                    mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
                    return;
                }
            }
        }
        if (mDatas.size() == 0) {
            mDatas.add(conv);
        } else {
            //?????????????????????,????????????????????????????????????????????????list???
            for (int i = mDatas.size(); i > SharedPreferencesHelper.getCancelTopSize(); i--) {
                if (conv.getLatestMessage() != null && mDatas.get(i - 1).getLatestMessage() != null) {
                    if (conv.getLatestMessage().getCreateTime() > mDatas.get(i - 1).getLatestMessage().getCreateTime()) {
                        newCount = i - 1;
                    } else {
                        newCount = i;
                        break;
                    }
                } else {
                    newCount = i;
                }
            }
            mDatas.add(newCount, conv);
        }
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);
    }

    //????????????
    public void setConvTop(Conversation conversation) {
        int count = 0;
        //?????????????????????,?????????????????????????????????
        for (Conversation conv : mDatas) {
            if (!TextUtils.isEmpty(conv.getExtra())) {
                count++;
            }
        }
        conversation.updateConversationExtra(count + "");
        mDatas.remove(conversation);
        mDatas.add(count, conversation);
        mUIHandler.removeMessages(REFRESH_CONVERSATION_LIST);
        mUIHandler.sendEmptyMessageDelayed(REFRESH_CONVERSATION_LIST, 200);

    }

    //??????????????????
    public void setCancelConvTop(Conversation conversation) {
        forCurrent.clear();
        topConv.clear();
        int i = 0;
        for (Conversation oldConv : mDatas) {
            //????????????????????????????????????????????????,?????????????????????
            if (oldConv.getId().equals(conversation.getId())) {
                oldConv.updateConversationExtra("");
                break;
            }
        }
        //??????????????????
        SortConvList sortConvList = new SortConvList();
        Collections.sort(mDatas, sortConvList);

        //???????????????????????????????????????
        for (Conversation con : mDatas) {
            if (!TextUtils.isEmpty(con.getExtra())) {
                forCurrent.add(con);
            }
        }
        topConv.addAll(forCurrent);
        SharedPreferencesHelper.setCancelTopSize(topConv.size());
        mDatas.removeAll(forCurrent);
        if (topConv != null && topConv.size() > 0) {
            SortTopConvList top = new SortTopConvList();
            Collections.sort(topConv, top);
            for (Conversation conv : topConv) {
                mDatas.add(i, conv);
                i++;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public Conversation getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Conversation convItem = mDatas.get(position);
        mConversationListView.setUnReadMsg(JMessageClient.getAllUnReadMsgCount());
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_conv_list, null);
        }
        final ImageView headIcon = ViewHolder.get(convertView, R.id.msg_item_head_icon);
        TextView convName = ViewHolder.get(convertView, R.id.conv_item_name);
        TextView content = ViewHolder.get(convertView, R.id.msg_item_content);
        TextView datetime = ViewHolder.get(convertView, R.id.msg_item_date);
        TextView newGroupMsgNumber = ViewHolder.get(convertView, R.id.new_group_msg_number);
        TextView newMsgNumber = ViewHolder.get(convertView, R.id.new_msg_number);
        ImageView groupBlocked = ViewHolder.get(convertView, R.id.iv_groupBlocked);
        ImageView newMsgDisturb = ViewHolder.get(convertView, R.id.new_group_msg_disturb);
        ImageView newGroupMsgDisturb = ViewHolder.get(convertView, R.id.new_msg_disturb);

        final SwipeLayoutConv swipeLayout = ViewHolder.get(convertView, R.id.swp_layout);
        final TextView delete = ViewHolder.get(convertView, R.id.tv_delete);

        String draft = mDraftMap.get(convItem.getId());
        if (!TextUtils.isEmpty(convItem.getExtra())) {
            swipeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.conv_list_background));
        } else {
            swipeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        //????????????????????????,????????????????????????
        if (TextUtils.isEmpty(draft)) {
            Message lastMsg = convItem.getLatestMessage();
            if (lastMsg != null) {
                TimeFormat timeFormat = new TimeFormat(mContext, lastMsg.getCreateTime());
//                //??????????????????
                datetime.setText(timeFormat.getTime());
                String contentStr;
                switch (lastMsg.getContentType()) {
                    case image:
                        contentStr = mContext.getString(R.string.type_picture);
                        break;
//                    case voice:
//                        contentStr = mContext.getString(R.string.type_voice);
//                        break;
//                    case location:
//                        contentStr = mContext.getString(R.string.type_location);
//                        break;
//                    case file:
//                        String extra = lastMsg.getContent().getStringExtra("video");
//                        if (!TextUtils.isEmpty(extra)) {
//                            contentStr = mContext.getString(R.string.type_smallvideo);
//                        } else {
//                            contentStr = mContext.getString(R.string.type_file);
//                        }
//                        break;
//                    case video:
//                        contentStr = mContext.getString(R.string.type_video);
//                        break;
                    case eventNotification:
//                        contentStr = mContext.getString(R.string.group_notification);
                        contentStr = ((EventNotificationContent) lastMsg.getContent()).getEventText();
                        break;
//                    case custom:
//                        CustomContent customContent = (CustomContent) lastMsg.getContent();
//                        Boolean isBlackListHint = customContent.getBooleanValue("blackList");
//                        if (isBlackListHint != null && isBlackListHint) {
//                            contentStr = mContext.getString(R.string.jmui_server_803008);
//                        } else {
//                            contentStr = mContext.getString(R.string.type_custom);
//                        }
//                        break;
                    case prompt:
                        contentStr = ((PromptContent) lastMsg.getContent()).getPromptText();
                        break;
                    default:
                        contentStr = ((TextContent) lastMsg.getContent()).getText();
                        break;
                }

                MessageContent msgContent = lastMsg.getContent();
                Boolean isRead = msgContent.getBooleanExtra("isRead");
                Boolean isReadAtAll = msgContent.getBooleanExtra("isReadAtAll");
                if (lastMsg.isAtMe()) {
                    if (null != isRead && isRead) {
                        mArray.delete(position);
                        mAtConvMap.remove(convItem);
                    } else {
                        mArray.put(position, true);
                    }
                }
                if (lastMsg.isAtAll()) {
                    if (null != isReadAtAll && isReadAtAll) {
                        mAtAll.delete(position);
                        mAtAllConv.remove(convItem);
                    } else {
                        mAtAll.put(position, true);
                    }

                }
                long gid = 0;
                if (convItem.getType().equals(ConversationType.group)) {
                    gid = Long.parseLong(convItem.getTargetId());
                }

                if (lastMsg.getUnreceiptCnt() == 0) {
                    if (lastMsg.getTargetType().equals(ConversationType.single) &&
                            lastMsg.getDirect().equals(MessageDirect.send) &&
                            !lastMsg.getContentType().equals(ContentType.prompt) &&
                            //?????????????????????????????????
                            !((UserInfo) lastMsg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                        content.setText("[??????]" + contentStr);
                    } else {
                        content.setText(contentStr);
                    }
                } else {
                    if (lastMsg.getTargetType().equals(ConversationType.single) &&
                            lastMsg.getDirect().equals(MessageDirect.send) &&
                            !lastMsg.getContentType().equals(ContentType.prompt) &&
                            !((UserInfo) lastMsg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                        contentStr = "[??????]" + contentStr;
                        SpannableStringBuilder builder = new SpannableStringBuilder(contentStr);
                        builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.line_normal)),
                                0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        content.setText(builder);
                    } else {
                        content.setText(contentStr);
                    }
                }
            } else {
                if (convItem.getLastMsgDate() == 0) {
                    //????????????????????????????????????????????????,??????????????????????????????????????????????????????
                    datetime.setText("");
                    content.setText("");
                } else {
                    TimeFormat timeFormat = new TimeFormat(mContext, convItem.getLastMsgDate());
                    datetime.setText(timeFormat.getTime());
                    content.setText("");
                }
            }
        } else {
            draft = mContext.getString(R.string.draft) + draft;
            SpannableStringBuilder builder = new SpannableStringBuilder(draft);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            content.setText(builder);
        }

        if (convItem.getType().equals(ConversationType.single)) {
            groupBlocked.setVisibility(View.GONE);
            convName.setText(convItem.getTitle());
            mUserInfo = (UserInfo) convItem.getTargetInfo();
            if (mUserInfo != null) {
                mUserInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            headIcon.setImageBitmap(bitmap);
                        } else {
                            headIcon.setImageResource(R.drawable.jmui_head_icon);
                        }
                    }
                });
            } else {
                headIcon.setImageResource(R.drawable.jmui_head_icon);
            }
        } else {
            mGroupInfo = (GroupInfo) convItem.getTargetInfo();
            if (mGroupInfo != null) {
                mGroupInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        if (i == 0) {
                            headIcon.setImageBitmap(bitmap);
                        } else {
                            headIcon.setImageResource(R.drawable.group);
                        }
                    }
                });
                int blocked = mGroupInfo.isGroupBlocked();
                if (blocked == 1) {
                    groupBlocked.setVisibility(View.VISIBLE);
                } else {
                    groupBlocked.setVisibility(View.GONE);
                }
            }
            convName.setText(convItem.getTitle());
        }

        if (convItem.getUnReadMsgCnt() > 0) {
            newGroupMsgDisturb.setVisibility(View.GONE);
            newMsgDisturb.setVisibility(View.GONE);
            newGroupMsgNumber.setVisibility(View.GONE);
            newMsgNumber.setVisibility(View.GONE);
            if (convItem.getType().equals(ConversationType.single)) {
                if (mUserInfo != null && mUserInfo.getNoDisturb() == 1) {
                    newMsgDisturb.setVisibility(View.VISIBLE);
                } else {
                    newMsgNumber.setVisibility(View.VISIBLE);
                }
                if (convItem.getUnReadMsgCnt() < 100) {
                    newMsgNumber.setText(String.valueOf(convItem.getUnReadMsgCnt()));
                } else {
                    newMsgNumber.setText("99+");
                }
            } else {
                if (mGroupInfo != null && mGroupInfo.getNoDisturb() == 1) {
                    newGroupMsgDisturb.setVisibility(View.VISIBLE);
                } else {
                    newGroupMsgNumber.setVisibility(View.VISIBLE);
                }
                if (convItem.getUnReadMsgCnt() < 100) {
                    newGroupMsgNumber.setText(String.valueOf(convItem.getUnReadMsgCnt()));
                } else {
                    newGroupMsgNumber.setText("99+");
                }
            }

        } else {
            newGroupMsgDisturb.setVisibility(View.GONE);
            newMsgDisturb.setVisibility(View.GONE);
            newGroupMsgNumber.setVisibility(View.GONE);
            newMsgNumber.setVisibility(View.GONE);
        }

        //????????????????????????.??????????????????????????????true
        swipeLayout.setSwipeEnabled(false);
        //??????????????????
        swipeLayout.addSwipeListener(new SwipeLayoutConv.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayoutConv layout) {

            }

            @Override
            public void onOpen(SwipeLayoutConv layout) {
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (convItem.getType() == ConversationType.single) {
                            JMessageClient.deleteSingleConversation(((UserInfo) convItem.getTargetInfo()).getUserName());
                        } else {
                            JMessageClient.deleteGroupConversation(((GroupInfo) convItem.getTargetInfo()).getGroupID());
                        }
                        mDatas.remove(position);
                        if (mDatas.size() > 0) {
                            mConversationListView.setNullConversation(true);
                        } else {
                            mConversationListView.setNullConversation(false);
                        }
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onStartClose(SwipeLayoutConv layout) {

            }

            @Override
            public void onClose(SwipeLayoutConv layout) {

            }

            @Override
            public void onUpdate(SwipeLayoutConv layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayoutConv layout, float xvel, float yvel) {

            }
        });


        return convertView;
    }

    List<Conversation> topConv = new ArrayList<>();
    List<Conversation> forCurrent = new ArrayList<>();

    public void sortConvList() {
        forCurrent.clear();
        topConv.clear();
        int i = 0;
        SortConvList sortConvList = new SortConvList();
        Collections.sort(mDatas, sortConvList);
        for (Conversation con : mDatas) {
            if (!TextUtils.isEmpty(con.getExtra())) {
                forCurrent.add(con);
            }
        }
        topConv.addAll(forCurrent);
        mDatas.removeAll(forCurrent);
        if (topConv != null && topConv.size() > 0) {
            SortTopConvList top = new SortTopConvList();
            Collections.sort(topConv, top);
            for (Conversation conv : topConv) {
                mDatas.add(i, conv);
                i++;
            }
        }
        notifyDataSetChanged();
    }

    public void addNewConversation(Conversation conv) {
        mDatas.add(0, conv);
        if (mDatas.size() > 0) {
            mConversationListView.setNullConversation(true);
        } else {
            mConversationListView.setNullConversation(false);
        }
        notifyDataSetChanged();
    }

    public void addAndSort(Conversation conv) {
        mDatas.add(conv);
        SortConvList sortConvList = new SortConvList();
        Collections.sort(mDatas, sortConvList);
        notifyDataSetChanged();
    }

    public void deleteConversation(Conversation conversation) {
        mDatas.remove(conversation);
        notifyDataSetChanged();
    }

    public void putDraftToMap(Conversation conv, String draft) {
        mDraftMap.put(conv.getId(), draft);
    }

    public void delDraftFromMap(Conversation conv) {
        mArray.delete(mDatas.indexOf(conv));
        mAtConvMap.remove(conv);
        mAtAllConv.remove(conv);
        mDraftMap.remove(conv.getId());
        notifyDataSetChanged();
    }

    public String getDraft(String convId) {
        return mDraftMap.get(convId);
    }

    public boolean includeAtMsg(Conversation conv) {
        if (mAtConvMap.size() > 0) {
            Iterator<Map.Entry<Conversation, Integer>> iterator = mAtConvMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Conversation, Integer> entry = iterator.next();
                if (conv == entry.getKey()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean includeAtAllMsg(Conversation conv) {
        if (mAtAllConv.size() > 0) {
            Iterator<Map.Entry<Conversation, Integer>> iterator = mAtAllConv.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Conversation, Integer> entry = iterator.next();
                if (conv == entry.getKey()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getAtMsgId(Conversation conv) {
        return mAtConvMap.get(conv);
    }

    public int getatAllMsgId(Conversation conv) {
        return mAtAllConv.get(conv);
    }

    public void putAtConv(Conversation conv, int msgId) {
        mAtConvMap.put(conv, msgId);
    }

    public void putAtAllConv(Conversation conv, int msgId) {
        mAtAllConv.put(conv, msgId);
    }

    private static class UIHandler extends Handler {

        private final WeakReference<ConversationListAdapter> mAdapter;

        public UIHandler(ConversationListAdapter adapter) {
            mAdapter = new WeakReference<>(adapter);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ConversationListAdapter adapter = mAdapter.get();
            if (adapter != null) {
                switch (msg.what) {
                    case REFRESH_CONVERSATION_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }
}
