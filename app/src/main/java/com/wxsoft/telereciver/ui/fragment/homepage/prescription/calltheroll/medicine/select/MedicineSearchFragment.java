package com.wxsoft.telereciver.ui.fragment.homepage.prescription.calltheroll.medicine.select;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.entity.BaseResp;
import com.wxsoft.telereciver.entity.prescription.Medicine;
import com.wxsoft.telereciver.http.ApiFactory;
import com.wxsoft.telereciver.ui.base.BaseFragment;
import com.wxsoft.telereciver.ui.base.FragmentArgs;
import com.wxsoft.telereciver.ui.base.FragmentContainerActivity;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.util.List;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MedicineSearchFragment extends BaseFragment {

    public static void launch(Activity from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HAS_RESULT, false);
        FragmentContainerActivity.launch(from, MedicineSearchFragment.class, args);
    }

    public static void launchForResult(Fragment from) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_HAS_RESULT, true);
        FragmentContainerActivity.launchForResult(from, MedicineSearchFragment.class, args, REQUEST_SEARCH_MEDICINE);
    }

    private boolean need;
    private static final String FRAGMENT_ARGS_HAS_RESULT = "FRAGMENT_ARGS_HAS_RESULT";
    public static final int REQUEST_SEARCH_MEDICINE = 131;
    public static final String KEY_MEDICINE = "KEY_MEDICINE";

    private static final String FRAGMENT_ARGS_NEED_SURE = "FRAGMENT_ARGS_NEED_SURE";


    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<Medicine> mAdapter;
    private String mQueryText;
    private boolean hasResult;

    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        hasResult = getArguments().getBoolean(FRAGMENT_ARGS_HAS_RESULT);
        need = getArguments().getBoolean(FRAGMENT_ARGS_NEED_SURE);
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem( R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.select__medicine_search_input_hint));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQueryText = query;
                mRecyclerView.showProgress();
                searchPatient();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
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
        activity.getSupportActionBar().setTitle("");
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

    private void searchPatient() {
        ApiFactory.getPrescriptionApi().getMedicinesByKey(mQueryText)
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
