package com.wxsoft.telereciver.ui.fragment.homepage.transfertreatment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.transfertreatment.MessageTemplate;
import com.wxsoft.telereciver.event.UpdateModelStateEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TransferTreatmentModelSubmitFragment extends BaseFragment {

    public static void launch(Activity from, MessageTemplate template) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_DISEASE_ID, template);
        FragmentContainerActivity.launch(from, TransferTreatmentModelSubmitFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_DISEASE_ID = "FRAGMENTARGS_KEY_DISEASE_ID";
    private static final int EDITTEXT_MAX_LENGTH = 500;

    @BindView(R.id.et_content)
    EditText mContentView;

    @BindView(R.id.tv_count)
    TextView mCountView;

    private MessageTemplate mClinicId;

    @OnTextChanged(R.id.et_content)
    void textChanged(CharSequence s, int start, int before, int count) {
        mCountView.setText(s.length() + "/" + EDITTEXT_MAX_LENGTH);
        _mActivity.invalidateOptionsMenu();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_transfertreatment_submit;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        mClinicId = (MessageTemplate) getArguments().getSerializable(FRAGMENTARGS_KEY_DISEASE_ID);
        setupToolbar();

        mContentView.setText(mClinicId.messageNote);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        menuItem.setTitle(R.string.ok);
        menuItem.setVisible(mCountView.getText().length() > 0 ? true : false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                commit();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.treatment_submit_title);
        setHasOptionsMenu(true);
    }

    private void commit() {
        String content = mContentView.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ViewUtil.showMessage("请输入内容");
            return;
        }
        mClinicId.messageNote=content;
        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getCommApi().saveTemplate(mClinicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.dismissProgressDialog();
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp resp) {
                        ViewUtil.dismissProgressDialog();
                        if (resp.isSuccess()) {
                            EventBus.getDefault().post(new UpdateModelStateEvent());
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
