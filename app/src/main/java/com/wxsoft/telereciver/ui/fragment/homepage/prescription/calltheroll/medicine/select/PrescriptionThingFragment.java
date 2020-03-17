package com.wxsoft.telereciver.ui.fragment.homepage.prescription.calltheroll.medicine.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.CommEnum;
import com.wxsoft.telereciver.entity.prescription.Medicine;
import com.wxsoft.telereciver.entity.prescription.Recipe;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_COMMON_MEMO;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_HZ;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_USING;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_USING_AMOUNT;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_USING_COUNT_DAYLY;
import static com.wxsoft.telereciver.AppConstant.REQUEST_TYPE_NAME.MEDICAINE_USING_COUNT_DAYS;

/**
 * 单个药品
 */
public class PrescriptionThingFragment extends BaseFragment {


    private static final String KEY_RECIPE = "KEY_RECIPE";

    public static void launch(Activity from) {
        FragmentContainerActivity.launch(from, PrescriptionThingFragment.class, null);
    }

    public static void launch(Activity from, Medicine clinic) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_MEDICINE, clinic);
        FragmentContainerActivity.launch(from, PrescriptionThingFragment.class, args);
    }

    private static final String FRAGMENT_ARGS_MEDICINE = "FRAGMENT_ARGS_MEDICINE";

    private Medicine medicine;

    private Recipe recipe=new Recipe();
    @BindView(R.id.tv_med_name)
    TextView tvMedName;

    @BindView(R.id.tv_hz)
    TextView tvHz;

    @BindView(R.id.tv_taking_way)
    TextView tvTakingWay;

    @BindView(R.id.tv_dose)
    TextView tvDose;

    @BindView(R.id.tv_days)
    TextView tvDays;


    @BindView(R.id.tv_count)
    TextView tvCount;

    @BindView(R.id.tv_momo)
    TextView tvMemo;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<CommEnum> mAdapter;

//    @BindView(R.id.tv_add_med)
//    TextView mAddMed;

//    @BindView(R.id.recycler_view)
//    EasyRecyclerView mPhotoRecyclerView;

//    @OnClick(R.id.lr_medicine)
//    void clickMed() {
//        SelectFragment.launch(this,"");
//    }

    @OnClick(R.id.ll_hz)
    void clickHz() {
        SelectFragment.launch(this,MEDICAINE_HZ);
    }

    @OnClick(R.id.lr_taking_way)
    void clickWay() {
        SelectFragment.launch(this,MEDICAINE_USING);
    }

    @OnClick(R.id.lr_days)
    void clickDay() {
        SelectFragment.launch(this,MEDICAINE_USING_COUNT_DAYS);
    }

    @OnClick(R.id.lr_dose)
    void clickDose() {
        SelectFragment.launch(this,MEDICAINE_USING_COUNT_DAYLY,medicine.ggdw);
    }

    @OnClick(R.id.lr_count)
    void clickCount() {
        SelectFragment.launch(this,MEDICAINE_USING_AMOUNT,medicine.zxdw);
    }

    @OnClick(R.id.btn_save)
    void saveClick() {
        commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_prescription_thing;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {

        medicine =(Medicine) getArguments().getSerializable(FRAGMENT_ARGS_MEDICINE) ;
        setupToolbar();

        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setLayoutManager(new GridLayoutManager(_mActivity,4));
        mRecyclerView.setRefreshingColorResources(R.color.colorPrimary);
        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<CommEnum>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new SelectViewHolder(parent);
            }
        });

        mAdapter.setOnItemClickListener(position -> {
            String selected=tvMemo.getText().toString();
            boolean empty=selected==null || selected.isEmpty();
            tvMemo.setText(selected+(empty?mAdapter.getItem(position).getItemName():","+mAdapter.getItem(position).getItemName()));
            recipe.tags=tvMemo.getText().toString();
        });

        recipe.medicineId=medicine.id;
        recipe.medicineCommonName=medicine.commonName;
        recipe.medicineProductName=medicine.productName;
        recipe.saleUnit=medicine.zxdw;
        recipe.unitPrice=medicine.zfbl;
        tvMedName.setText(medicine.productName);
        loadData();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String type=data.getStringExtra(SelectFragment.KEY_SELECT);
            CommEnum item=(CommEnum)data.getSerializableExtra(SelectFragment.KEY_DICT);
            if(type.equals(MEDICAINE_HZ)){

                tvHz.setText(item.getItemName());
                recipe.usage=tvTakingWay.getText().toString()+"/"+tvHz.getText().toString()+"/每次"+tvDose.getText().toString();
            }else if(type.equals(MEDICAINE_USING)){
//                recipe.
                tvTakingWay.setText(item.getItemName());
                recipe.usage=tvTakingWay.getText().toString()+"/"+tvHz.getText().toString()+"/每次"+tvDose.getText().toString();
            }else if(type.equals(MEDICAINE_USING_AMOUNT)){
                recipe.count=Integer.parseInt(item.getEnumDict());
                tvCount.setText(item.getItemName());
            }else if(type.equals(MEDICAINE_USING_COUNT_DAYLY)){
                tvDose.setText(item.getItemName());
                recipe.usage=tvTakingWay.getText().toString()+"/"+tvHz.getText().toString()+"/每次"+tvDose.getText().toString();
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.create_prescription_title);
    }


    private void commit(){

        EventBus.getDefault().post(recipe);

        Intent intent = new Intent();
        _mActivity.setResult(RESULT_OK, intent);
        _mActivity.finish();
    }

    private void loadData() {
        ApiFactory.getPrescriptionApi().getMedicalInsurances(MEDICAINE_COMMON_MEMO)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<BaseResp<List<CommEnum>>>() {
              @Override
              public void onCompleted() {

              }

              @Override
              public void onError(Throwable e) {
                  ViewUtil.showMessage(e.getMessage());
              }

              @Override
              public void onNext(BaseResp<List<CommEnum>> resp) {
                  processResponse(resp);
              }
          });
    }

    private void processResponse(BaseResp<List<CommEnum>> resp) {
        if (!resp.isSuccess()) {
            mRecyclerView.getErrorView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadData();
            });
            mRecyclerView.showError();
            return;
        }

        List<CommEnum> duties =resp.getData();
        if (duties == null || duties.isEmpty()) {
            mRecyclerView.getEmptyView().setOnClickListener(v -> {
                mRecyclerView.showProgress();
                loadData();
            });
            mRecyclerView.showEmpty();
            return;
        }

        mRecyclerView.showRecycler();
        mAdapter.clear();
        mAdapter.addAll(duties);
    }

    private class SelectViewHolder extends BaseViewHolder<CommEnum> {

        private TextView mTitleView;

        public SelectViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_medicine_memo);
            mTitleView = $(R.id.tv_title);
        }

        @Override
        public void setData(CommEnum data) {
            super.setData(data);
            mTitleView.setText(data.getItemName());
        }
    }

}
