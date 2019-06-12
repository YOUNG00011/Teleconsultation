package com.wxsoft.teleconsultation.ui.fragment.homepage.register.callandroll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Hospital;
import com.wxsoft.teleconsultation.entity.register.ScheduDateItem;
import com.wxsoft.teleconsultation.entity.register.ScheduDateMap;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.ui.fragment.homepage.register.RegisterSureFragment;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectTimeFragment extends BaseFragment {

    public static void launch(Fragment from, ScheduDateMap map,String date) {
        FragmentArgs args=new FragmentArgs();
        args.add(KEY_DOCTOR,new Gson().toJson(map));
        args.add(SHEDUINGDATE,date);
        FragmentContainerActivity.launchForResult(from, SelectTimeFragment.class, args,REQUEST_SELECT_SHEDUINGDATE );
    }

    public static Calendar cal = Calendar.getInstance();
    public static String[] weekdays;

    public static final int REQUEST_SELECT_SHEDUINGDATE = 68;
    public static final String KEY_DOCTOR = "KEY_DOCTOR";
    public static final String SHEDUINGDATE = "SHEDUING_DATE";
    private ScheduDateMap map;
    private String date_day;

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<ScheduDateItem> mAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.comm_recycler_view;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        weekdays=new String[]{getResources().getString(R.string.sun),getResources().getString(R.string.mon),getResources().getString(R.string.tue)
                ,getResources().getString(R.string.wen),getResources().getString(R.string.thu),getResources().getString(R.string.fri),getResources().getString(R.string.san)};
        String smap=getArguments().getString(KEY_DOCTOR);
        if(smap!=null){
            map=(new Gson()).fromJson(smap,ScheduDateMap.class);
        }
        date_day=getArguments().getString(SHEDUINGDATE);
        setupToolbar();
        setupRecyclerView();
        if (date_day!=null && map!=null) {
            loadData();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RegisterSureFragment.REQUEST_SURE_SHEDUINGDATE) {
                if (data != null) {
                    boolean doctor = data.getBooleanExtra(RegisterSureFragment.KEY_WORK_DONE, false);
                    Intent intent = new Intent();
                    intent.putExtra(RegisterSureFragment.KEY_WORK_DONE, doctor);
                    _mActivity.setResult(RESULT_OK, intent);
                    _mActivity.finish();
                }
            }
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.choose_schude_item);
        setHasOptionsMenu(true);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(_mActivity, R.color.comm_list_divider_color), DensityUtil.dip2px(_mActivity, 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);
//
        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<ScheduDateItem>(_mActivity) {

            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(parent );
            }
        });
//
        mAdapter.setOnItemClickListener(position ->  {
            RegisterSureFragment.launch(this,map, mAdapter.getItem(position));
        });


    }

    private void loadData() {
        ApiFactory.getRegisterApi().getSchedulingItemCountByDoc(map.doctorId,date_day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResp<List<ScheduDateItem>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ViewUtil.showMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(BaseResp<List<ScheduDateItem>> resp) {
                        processResponse(resp);
                    }
                });
    }

    private void processResponse(BaseResp<List<ScheduDateItem>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }
        mAdapter.addAll(resp.getData());

        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return View.inflate(_mActivity, R.layout.header_schude_item, null);
            }

            @Override
            public void onBindView(View headerView) {

                ((TextView) ButterKnife.findById(headerView, R.id.money)).setText(String.valueOf(map.registerFee));
            }
        });
    }

    private void processQueryResponse(BaseResp<List<ScheduDateItem>> resp) {
        if (!resp.isSuccess()) {
            ViewUtil.showMessage(resp.getMessage());
            return;
        }

        mAdapter.clear();
        mAdapter.addAll(resp.getData());
    }

    private static class ViewHolder extends BaseViewHolder<ScheduDateItem> {

        private TextView tvDate;
        private TextView tvWeekday;
        private TextView tvSchudeTime;
        private TextView tvRegister;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.comm_item_sch_item);

            tvDate = $(R.id.tv_date);
            tvWeekday = $(R.id.tv_weekday);
            tvSchudeTime = $(R.id.tv_schude_time);
            tvRegister = $(R.id.btn_register);


        }

        @Override
        public void setData(final ScheduDateItem data) {
            super.setData(data);
            tvDate.setText(data.schedulingDate.substring(5,10));


            tvSchudeTime.setText(data.schedulingStage);
            tvRegister.setText(String.valueOf(data.registerNo));

            try {
                cal.setTime(format.parse(data.schedulingDate.substring(0,10)));
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

                tvWeekday.setText(weekdays[dayOfWeek-1]);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    }
}
