package com.wxsoft.teleconsultation.ui.fragment.homepage.prescription.calltheroll.medicine.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.prescription.Medicine;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MedicineListFragment extends BaseFragment {


    public static void launchForResult(Activity from, String type, String typeName) {
        FragmentArgs args = new FragmentArgs();
        args.add(KEY_TYPE, type);
        args.add(KEY_TYPE_NAME, typeName);
        FragmentContainerActivity.launchForResult(from, MedicineListFragment.class, args, REQUEST_SEARCH_MEDICINE);
    }

    private String type,typeName;
    private static final String FRAGMENT_ARGS_HAS_RESULT = "FRAGMENT_ARGS_HAS_RESULT";
    public static final int REQUEST_SEARCH_MEDICINE = 131;
    public static final String KEY_MEDICINE = "KEY_MEDICINE";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String KEY_TYPE_NAME = "KEY_TYPE_NAME";

    private static final String FRAGMENT_ARGS_NEED_SURE = "FRAGMENT_ARGS_NEED_SURE";


    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Medicine> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        type = getArguments().getString(KEY_TYPE);
        typeName = getArguments().getString(KEY_TYPE_NAME);
        setupToolbar();
        setupRecyclerView();

        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SEARCH_MEDICINE) {
                if (data != null) {
                    Intent intent = new Intent();
                    _mActivity.setResult(RESULT_OK, intent);
                    _mActivity.finish();
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(typeName);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);

        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<Medicine>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MedicineViewHolder(parent);
            }
        });

        mRecyclerView.showEmpty();
    }

    private void loadData() {
        ApiFactory.getPrescriptionApi().getMedicines(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<Medicine>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<Medicine>> resp) {

                        processSearchResponse(resp);
                    }
                });
    }

    private void processSearchResponse(BaseResp<List<Medicine>> resp) {
        mAdapter.clear();
        List<Medicine> medicines = resp.getData();
        if (medicines == null || medicines.isEmpty()) {

            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.showEmpty();
            } else {
                mAdapter.stopMore();
            }
            return;
        }


        mAdapter.addAll(medicines);
    }

    private class MedicineViewHolder extends BaseViewHolder<Medicine> {

        private TextView mPriceView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private Button mAdd;
        private TextView mAgeView;
        private LinearLayout mTagsView;

        public MedicineViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_medicine);
            mAvatarView = $(R.id.iv_med_avatar);
            mNameView = $(R.id.tv_med_name);
            mPriceView = $(R.id.tv_med_price);
            mAdd = $(R.id.add_to_prescription);

            mAdd.setOnClickListener(view -> {

                Intent intent = new Intent();
                _mActivity.setResult(RESULT_OK, intent);
                _mActivity.finish();
                PrescriptionThingFragment.launch(_mActivity,mAdapter.getItem(getDataPosition()));

            });
        }

        @Override
        public void setData(Medicine data) {
            super.setData(data);
            mNameView.setText(data.productName);
//            mPriceView.setText(data.p);
//            mAdd.s?etText(data.cjd?m);
        }
    }
}
