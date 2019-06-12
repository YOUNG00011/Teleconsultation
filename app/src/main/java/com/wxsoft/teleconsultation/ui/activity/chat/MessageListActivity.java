package com.wxsoft.teleconsultation.ui.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.entity.diseasecounseling.DiseaseCounseling;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.adapter.MessageAdapter;
import com.wxsoft.teleconsultation.ui.base.BaseActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author allen
 * 新的对话列表
 */
public class MessageListActivity extends BaseActivity {

    /**
     * 消息列表
     */
    @BindView(R.id.message_list)
    RecyclerView recyclerView;
    /**
     * 输入框
     */
    @BindView(R.id.chat_content)
    EditText chat_content;

    @BindView(R.id.send)
    TextView send;

    @OnClick(R.id.send)
    void send(){
        String content=chat_content.getText().toString();

        if(TextUtils.isEmpty(content))return;

        sendText(content);
    }

    @OnTextChanged(R.id.chat_content)
    void onTextChanged(){
        if(chat_content.getText().length()==0){
            send.setText(" ");
            send.setBackgroundResource(R.drawable.ic_filter_list_white_24dp);
        }else{
            send.setBackgroundResource(0);
            send.setText(getString(R.string.chat_send_button_text));
        }
    }

    private Conversation conversation;

    private MessageAdapter adapter;

    private boolean single=true;

    private UserInfo mMyInfo;

    private User mUser;

    private String from ;

    private List<Message> messagelist=new ArrayList<>();

    private String  diseaseCounselingId;

    public static final String DISEASECOUNSELING_ID = "DISEASECOUNSELING_ID";
    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_list;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {

        Intent intent = getIntent();

        diseaseCounselingId=intent.getStringExtra(DISEASECOUNSELING_ID);
        mMyInfo = JMessageClient.getMyInfo();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MessageListActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new MessageAdapter(messagelist);
        recyclerView.setAdapter(adapter);
        getMessages();
    }

    private void getMessages(){
        ApiFactory.getDiseaseCounselingApi().getDetail(diseaseCounselingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<DiseaseCounseling>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<DiseaseCounseling> resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {

                            from=resp.getData().weChatAccount.jMessagAccount.getjUserName();
                            conversation = JMessageClient.getSingleConversation(from, mMyInfo.getAppKey());

                            List<Message> the_message=conversation.getAllMessage();

                            messagelist.addAll(the_message);
                            adapter.notifyDataSetChanged();

                        } else {

                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }

    private void sendText(String content){

        TextContent text = new TextContent(content);
        Message message=conversation.createSendMessage(text);
        messagelist.add(message);
        adapter.notifyDataSetChanged();
        MessageSendingOptions options = new MessageSendingOptions();
        options.setNeedReadReceipt(true);
        JMessageClient.sendMessage(message, options);
    }
}
