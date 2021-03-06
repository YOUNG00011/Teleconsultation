package com.wxsoft.telereciver.ui.fragment.message;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.conversation.Event;
import com.wxsoft.telereciver.event.ChatEvent;
import com.wxsoft.telereciver.ui.activity.SystemMessageActivity;
import com.wxsoft.telereciver.ui.controller.ConversationListController;
import com.wxsoft.telereciver.ui.widget.ConversationListView;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ConversationRefreshEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.eventbus.EventBus;
import me.yokeyword.fragmentation.SupportFragment;

public class MessageContentFragment extends SupportFragment {

    public static MessageContentFragment newInstance() {
        return new MessageContentFragment();
    }

    private static final int REFRESH_CONVERSATION_LIST = 0x3000;
    private static final int DISMISS_REFRESH_HEADER = 0x3001;
    private static final int ROAM_COMPLETED = 0x3002;

    private float mDensity;
    private int mDensityDpi;
    private int mWidth;
    private int mHeight;

    private Activity mContext;
    private View mRootView;
    private ConversationListView mConvListView;
    private ConversationListController mConvListController;
    private HandlerThread mThread;
    private BackgroundHandler mBackgroundHandler;
//    private View mMenuView;
//    private PopupWindow mMenuPopWindow;
//    private MenuItemView mMenuItemView;
//    private NetworkReceiver mReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //??????????????????,??????????????????onEvent??????????????????
        JMessageClient.registerEventReceiver(this);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mContext = this.getActivity();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        mRootView = layoutInflater.inflate(R.layout.fragment_conv_list,
                (ViewGroup) getActivity().findViewById(R.id.fl_container), false);
        mConvListView = new ConversationListView(mRootView, this.getActivity(), this);
        mConvListView.initModule();
        mThread = new HandlerThread("HomeActivity");
        mThread.start();
        mBackgroundHandler = new BackgroundHandler(mThread.getLooper());
//        mMenuView = getActivity().getLayoutInflater().inflate(R.layout.drop_down_menu, null);
        mConvListController = new ConversationListController(mConvListView, this, mWidth);
        mConvListView.setListener(mConvListController);
        mConvListView.setItemListeners(mConvListController);
        mConvListView.setLongClickListener(mConvListController);

        mConvListView.showLoadingHeader();
        mBackgroundHandler.sendEmptyMessageDelayed(DISMISS_REFRESH_HEADER, 1000);
        mRootView.findViewById(R.id.rl_message_root).setOnClickListener(view -> {
            SystemMessageActivity.launch(_mActivity);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mConvListController.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        //??????????????????
        JMessageClient.unRegisterEventReceiver(this);
        EventBus.getDefault().unregister(this);
        mBackgroundHandler.removeCallbacksAndMessages(null);
        mThread.getLooper().quit();
        super.onDestroy();
    }

    /**
     * ????????????
     */
    public void onEvent(MessageEvent event) {
        mConvListView.setUnReadMsg(JMessageClient.getAllUnReadMsgCount());
        Message msg = event.getMessage();
        if (msg.getTargetType() == ConversationType.group) {
            long groupId = ((GroupInfo) msg.getTargetInfo()).getGroupID();
            Conversation conv = JMessageClient.getGroupConversation(groupId);
            if (conv != null && mConvListController != null) {
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                        conv));
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param event ??????????????????
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android")) {
            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
        }
    }

    /**
     * ????????????
     */
    public void onEvent(MessageRetractEvent event) {
        Conversation conversation = event.getConversation();
        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conversation));
    }

    /**
     * ??????????????????
     */
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        mConvListController.getAdapter().notifyDataSetChanged();
    }

    /**
     * ????????????????????????
     *
     * @param event ?????????????????? ??????????????????
     */
    public void onEvent(ConversationRefreshEvent event) {
        Conversation conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android")) {
            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
            //????????????????????????????????????
            if (event.getReason().equals(ConversationRefreshEvent.Reason.UNREAD_CNT_UPDATED)) {
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
            }
        }
    }

    public void onEvent(ChatEvent event) {
        
    }

    public void onEventMainThread(Event event) {
        switch (event.getType()) {
            case createConversation:
                Conversation conv = event.getConversation();
                if (conv != null) {
                    mConvListController.getAdapter().addNewConversation(conv);
                }
                break;
            case deleteConversation:
                conv = event.getConversation();
                if (null != conv) {
                    mConvListController.getAdapter().deleteConversation(conv);
                }
                break;
            //???????????????????????????
            case draft:
                conv = event.getConversation();
                String draft = event.getDraft();
                //????????????????????????????????????????????????????????????
                if (!TextUtils.isEmpty(draft)) {
                    mConvListController.getAdapter().putDraftToMap(conv, draft);
                    mConvListController.getAdapter().setToTop(conv);
                    //????????????
                } else {
                    mConvListController.getAdapter().delDraftFromMap(conv);
                }
                break;
            case addFriend:
                break;
        }
    }

//    //???????????????????????????
//    private class NetworkReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
//                ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
//                if (null == activeInfo) {
//                    mConvListView.showHeaderView();
//                } else {
//                    mConvListView.dismissHeaderView();
//                }
//            }
//        }
//    }

    public void sortConvList() {
        if (mConvListController != null) {
            mConvListController.getAdapter().sortConvList();
        }
    }

    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CONVERSATION_LIST:
                    Conversation conv = (Conversation) msg.obj;
                    mConvListController.getAdapter().setToTop(conv);
                    break;
                case DISMISS_REFRESH_HEADER:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConvListView.dismissLoadingHeader();
                        }
                    });
                    break;
                case ROAM_COMPLETED:
                    conv = (Conversation) msg.obj;
                    mConvListController.getAdapter().addAndSort(conv);
                    break;
            }
        }
    }
}
