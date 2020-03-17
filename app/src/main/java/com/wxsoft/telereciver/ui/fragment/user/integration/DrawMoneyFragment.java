package com.wxsoft.telereciver.ui.fragment.user.integration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BankAccount;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.DrwaMoneyApplyRecord;
import com.wxsoft.telereciver.entity.IntegralAccount;
import com.wxsoft.telereciver.entity.User;
import com.wxsoft.telereciver.event.DrawMoneyEvent;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.ui.widget.ClearableEditText;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DrawMoneyFragment extends BaseFragment {

    private static final String FRAGMENT_ARGS_HAS_RESULT = "FRAGMENT_ARGS_HAS_RESULT";
    public static final int REQUEST_ADD_PATIENT = 39;
    public static final String KEY_INTEGRAL = "KEY_INTEGRAL";
    IntegralAccount info;
    DrwaMoneyApplyRecord account=new DrwaMoneyApplyRecord();

    public static void launch(Activity from, IntegralAccount account) {
        FragmentArgs args = new FragmentArgs();
        args.add(KEY_INTEGRAL, new Gson().toJson(account));
        FragmentContainerActivity.launch(from, DrawMoneyFragment.class, args);
    }


    @BindView(R.id.l_bank)
    LinearLayout l_bank;

    @BindView(R.id.tv_bank)
    TextView tv_bank;

    @BindView(R.id.can_draw)
    TextView can_draw;

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
       BankAccountListFragment.launch(this);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_draw_money_add;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

        info=new Gson().fromJson(getArguments().getString(KEY_INTEGRAL),IntegralAccount.class);
        User mUser= AppContext.getUser();

        account.doctorId=mUser.getDoctId();
        account.createrId=mUser.getId();
        account.createrName=mUser.getName();
        account.doctorName=mUser.getName();
        account.payee=mUser.getName();
        account.status="307-0001";
        account.statusName="已申请";

        if(info!=null){
            can_draw.setText(String.valueOf(info.accountAmount-info.drawingAmount)+getString(R.string.score));
        }else{
            can_draw.setText("0"+getString(R.string.score));
        }
        setupToolbar();
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.draw_money_title);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
                if (data != null) {
                    BankAccount item = (BankAccount) data.getSerializableExtra(BankAccountListFragment.BANK_ACCOUNT);
                    account.bankAccount=item.bankCardNo;
                    account.bankName=item.bankTypeName;
                    tv_bank.setText(item.bankTypeName);
                }
        }
    }

    private void savePatient() {

        if (TextUtils.isEmpty(account.bankName)) {
            ViewUtil.showMessage("请选择开户银行");
            return;
        }

        String branch=ed_bank_branch.getText().toString();
        if (TextUtils.isEmpty(branch)) {
            ViewUtil.showMessage("请选择开户网店");
            return;
        }

        account.applyAmount=Float.valueOf(branch);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calS = Calendar.getInstance();
        account.applyDate= formatter.format(calS.getTime());
//        account.bankCardNo=cardNo;
//        account.bankBranch=branch;
        ApiFactory.getCommApi().saveDrawMoneyApply(account)
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
            EventBus.getDefault().post(new DrawMoneyEvent());
            _mActivity.finish();
        } else {
            ViewUtil.showMessage(resp.getMessage());
        }
    }
}
