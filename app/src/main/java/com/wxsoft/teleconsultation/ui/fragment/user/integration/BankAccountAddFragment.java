package com.wxsoft.teleconsultation.ui.fragment.user.integration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.Bank;
import com.wxsoft.teleconsultation.entity.BankAccount;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.event.BankAccountEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.widget.ClearableEditText;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BankAccountAddFragment extends BaseFragment {

    private static final String FRAGMENT_ARGS_HAS_RESULT = "FRAGMENT_ARGS_HAS_RESULT";
    public static final int REQUEST_ADD_PATIENT = 39;
    public static final String KEY_BANK_ACCOUNT = "KEY_INTEGRAL";

    BankAccount account=new BankAccount();

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, BankAccountAddFragment.class, null);
    }

    @BindView(R.id.tv_owner)
    TextView tv_owner;

    @BindView(R.id.et_card_number)
    ClearableEditText et_card_number;

    @BindView(R.id.l_bank)
    LinearLayout l_bank;

    @BindView(R.id.tv_bank)
    TextView tv_bank;

    @BindView(R.id.ed_bank_branch)
    ClearableEditText ed_bank_branch;

    @BindView(R.id.btn_save)
    Button mSaveView;


    @OnClick(R.id.btn_save)
    void saveClick() {
        savePatient();
    }

    @OnClick(R.id.l_bank)
    void getBankInfo() {
       BankTypeListFragment.launch(this);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bank_account_add;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        User mUser= AppContext.getUser();
        account.owner=mUser.getName();
        account.doctorId=mUser.getDoctId();
        account.createrId=mUser.getId();
        account.createrName=mUser.getName();
        setupToolbar();
        mSaveView.setText(R.string.save_button_text);
        tv_owner.setText(account.owner);
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.new_bank_account_title);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bank item = (Bank) data.getSerializableExtra(BankTypeListFragment.KEY_BANK);
                    account.bankType=item.type;
                    account.bankTypeName=item.typeName;
                    tv_bank.setText(item.typeName);
                }
        }
    }

    private void savePatient() {
        String cardNo = et_card_number.getText().toString();
        if (TextUtils.isEmpty(cardNo)) {
            ViewUtil.showMessage("请输入卡号");
            return;
        }

        int length=cardNo.length();
        if(length!=16&&length!=17&&length!=19){
            ViewUtil.showMessage("卡号位数不对，请重新输入");
            et_card_number.findFocus();
            et_card_number.selectAll();
            return;
        }

        if (TextUtils.isEmpty(account.bankType)) {
            ViewUtil.showMessage("请选择开户银行");
            return;
        }

        String branch=ed_bank_branch.getText().toString();
        if (TextUtils.isEmpty(branch)) {
            ViewUtil.showMessage("请选择开户网点");
            return;
        }

        account.bankCardNo=cardNo;
        account.bankBranch=branch;

        ApiFactory.getCommApi().saveBankAccount(account)
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
                        processResponse(resp);
                    }
                });
    }

    public void processResponse(BaseResp resp) {
        if (resp.isSuccess()) {
            EventBus.getDefault().post(new BankAccountEvent());
            _mActivity.finish();
        } else {
            ViewUtil.showMessage(resp.getMessage());
        }
    }
}
