package com.wxsoft.telereciver.ui.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wxsoft.telereciver.R;

import java.util.List;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;

    //文本
    private final int TYPE_SEND_TXT = 0;
    private final int TYPE_RECEIVE_TXT = 1;

    // 图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;

    public MessageAdapter(List<Message> messages){
        this.messages=messages;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v;
        Message message=messages.get(i);
        switch (message.getContentType()){
            case text:
                if(message.getDirect() == MessageDirect.send){
                    v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_text_send,parent,false);

                }else{
                    v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_text_receive,parent,false);
                }
                return new MessageViewHolder<TextView>(v);

            case image:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int i) {

        Message message=messages.get(i);
        if(message.getDirect() == MessageDirect.send) {
            final UserInfo from = message.getFromUser();

            if (from != null && !TextUtils.isEmpty(from.getAvatar()))
                from.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            holder.profile.setImageBitmap(bitmap);
                        } else {
                            holder.profile.setImageResource(R.drawable.jmui_head_icon);
                        }
                    }
                });
            else {
                holder.profile.setImageResource(R.drawable.jmui_head_icon);
            }

            final MessageContent content=message.getContent();

            if((content instanceof TextContent) ){
                TextContent textContent=(TextContent)content;

                ((MessageViewHolder<TextView>)holder).body.setText(textContent.getText());

            }
            if(message.getDirect()==MessageDirect.send){

                 switch (message.getStatus()){
                    case created:
                        if (null != from) {
                            holder.sending.setVisibility(View.GONE);
                            holder.resend.setVisibility(View.VISIBLE);
                        }
                        break;
                    case send_success:
                        holder.sending.clearAnimation();
                        holder.sending.setVisibility(View.GONE);
                        holder.resend.setVisibility(View.GONE);
                        break;
                    case send_fail:
                        holder.sending.clearAnimation();
                        holder.sending.setVisibility(View.GONE);
                        holder.resend.setVisibility(View.VISIBLE);
                        break;
                    case send_going:
                        //TODO("这里还没处理")
                        //sendingTextOrVoice(holder, msg);
                        break;
                }
                //名片，暂时不做
                //String card = content.getStringExtra("businessCard");
            }



        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }



    abstract class ViewHolder extends  RecyclerView.ViewHolder{

        public ImageView profile;



        public ImageView resend;

        public ProgressBar sending;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            profile=itemView.findViewById(R.id.message_profile);
            resend=itemView.findViewById(R.id.fail_and_resend);
            sending =itemView.findViewById(R.id.sending);
        }
    }

    class MessageViewHolder<V extends View> extends ViewHolder{
        public V body;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            body=itemView.findViewById(R.id.message_body);
        }
    }

}
