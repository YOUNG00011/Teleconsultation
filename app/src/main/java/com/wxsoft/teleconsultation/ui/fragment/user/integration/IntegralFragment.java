package com.wxsoft.teleconsultation.ui.fragment.user.integration;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.IntegralAccount;
import com.wxsoft.teleconsultation.entity.User;
import com.wxsoft.teleconsultation.event.DrawMoneyEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IntegralFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, IntegralFragment.class, null);
    }
    IntegralAccount info;

    @BindView(R.id.integral)
    TextView score;

    private User mUser;

    @OnClick(R.id.integral_in_history)
    void history1() {
        IntegralInListFragment.launch(_mActivity);
    }

    @OnClick(R.id.integral_out_history)
    void history2() {
        IntegralOutListFragment.launch(_mActivity);
    }

    @OnClick(R.id.integral_out)
    void history3() {

        if(info.accountAmount-info.drawingAmount>0) {

            DrawMoneyFragment.launch(_mActivity,info);
        }else {
            ViewUtil.showMessage(R.string.no_score_to_draw);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_integral;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mUser = AppContext.getUser();
        setupRecyclerView();
    }

    private void setupToolbar() {
        EventBus.getDefault().register(this);
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.integral_title);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        // 设置上边距10dp
        loadScore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onEvent(DrawMoneyEvent uploadPhotoSuccessEvent) {

        loadScore();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        MenuItem menuItem = menu.findItem(R.id.action);
        menuItem.setTitle(R.string.menu_integral_card);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                //commit();
                BankAccountListFragment.launch(_mActivity);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadScore() {

        ApiFactory.getCommApi().queryIntegral(mUser.getDoctId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<IntegralAccount>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResp<IntegralAccount> stringBaseResp) {

                        if(stringBaseResp.isSuccess()){
                           info=stringBaseResp.getData();
                            score.setText(String.valueOf(info.accountAmount-info.drawingAmount));
                        }

                    }
                });
    }
}
