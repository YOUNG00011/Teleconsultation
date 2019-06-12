package com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.event.TreatMentStateChangeEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TransferTreatmentSubmitFragment extends BaseFragment {

    public static void launch(Activity from, String disId) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENTARGS_KEY_DISEASE_ID, disId);
        FragmentContainerActivity.launch(from, TransferTreatmentSubmitFragment.class, args);
    }

    private static final String FRAGMENTARGS_KEY_DISEASE_ID = "FRAGMENTARGS_KEY_DISEASE_ID";
    private static final int EDITTEXT_MAX_LENGTH = 500;

    @BindView(R.id.et_content)
    EditText mContentView;

    @BindView(R.id.tv_count)
    TextView mCountView;

    private String mClinicId;

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
        mClinicId = getArguments().getString(FRAGMENTARGS_KEY_DISEASE_ID);
        setupToolbar();
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
        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getTransferTreatmentApi().submit(mClinicId, content)
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
                            EventBus.getDefault().post(new TreatMentStateChangeEvent());
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
