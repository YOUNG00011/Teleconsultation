package com.wxsoft.teleconsultation.ui.fragment.homepage.prescription;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.easyrecyclerview.decoration.StickyHeaderDecoration;
import com.wxsoft.teleconsultation.AppConstant;
import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.entity.PatientTag;
import com.wxsoft.teleconsultation.entity.prescription.OnlinePrescription;
import com.wxsoft.teleconsultation.entity.requestbody.QueryRequestBody;
import com.wxsoft.teleconsultation.entity.responsedata.QueryResponseData;
import com.wxsoft.teleconsultation.entity.transfertreatment.TreatMent;
import com.wxsoft.teleconsultation.event.TreatMentStateChangeEvent;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.fragment.homepage.transfertreatment.TransferTreatmentDetailFragment;
import com.wxsoft.teleconsultation.util.AppUtil;
import com.wxsoft.teleconsultation.util.DateUtil;
import com.wxsoft.teleconsultation.util.DensityUtil;
import com.wxsoft.teleconsultation.util.ViewUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PrescriptionListFragment extends BaseFragment {

    public static PrescriptionListFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRAS_KEY_POSITION, position);
        PrescriptionListFragment fragment = new PrescriptionListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private List<String> statuses=new ArrayList<>();
    private static final String EXTRAS_KEY_POSITION = "EXTRAS_KEY_POSITION";

    @BindView(R.id.recycler_view)
    EasyRecyclerView mRecyclerView;

    private RecyclerArrayAdapter<OnlinePrescription> mAdapter;
    private int mCurrentPosition = 0;
    private int statusIndex = 0;
    private int mPage = 1;
    String[] filters ;

    @OnClick(R.id.fabtn_filter)
    void filterClick() {



       int index = statusIndex;

       new MaterialDialog.Builder(_mActivity)
                .items( filters )
                .itemsCallbackSingleChoice(index, (dialog, view, which, text) -> {
                    statusIndex =which;
                    showRefreshing(true);
                    mPage = 1;
                    mAdapter.clear();
                    loadData();
                    return true;
                })
                .show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_apply;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        statuses.add(null);
        statuses.add("602-0001");
        statuses.add("602-0002");
        statuses.add("602-0003");
        statuses.add("602-0004");
        statuses.add("602-0005");


        filters= new String[]{
                getString(R.string.transfer_treatment_list_filter_text_1),
                getString(R.string.transfer_treatment_list_filter_text_2),
                getString(R.string.transfer_treatment_list_filter_text_3),
                getString(R.string.transfer_treatment_list_filter_text_4),
                getString(R.string.transfer_treatment_list_filter_text_5),
                getString(R.string.transfer_treatment_list_filter_text_6)
        };
        mCurrentPosition = getArguments().getInt(EXTRAS_KEY_POSITION);
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
        setupRecyclerView();
    }


    @Subscribe
    public void load(Object object){
        if(object instanceof TreatMentStateChangeEvent)
            loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
    }

    private void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));
        DividerDecoration itemDecoration = new DividerDecoration(ContextCompat.getColor(getActivity(), R.color.comm_list_divider_color), DensityUtil.dip2px(getActivity(), 0.5f), 0, 0);
        mRecyclerView.addItemDecoration(itemDecoration);


        StickyHeaderDecoration decoration = new StickyHeaderDecoration(new StickyHeaderAdapter(_mActivity));
        decoration.setIncludeHeader(false);
        mRecyclerView.addItemDecoration(decoration);

        mRecyclerView.setRefreshingColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        mRecyclerView.setAdapterWithProgress(mAdapter = new RecyclerArrayAdapter<OnlinePrescription>(_mActivity) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyApplyViewHolder(parent, _mActivity);
            }
        });

        mRecyclerView.setRefreshListener(() -> {
            mPage = 1;
            mAdapter.clear();
            loadData();
        });

        mAdapter.setMore(R.layout.comm_load_more, () -> {
            mPage ++;
            loadData();
        });

        mAdapter.setOnItemClickListener(position -> {
            String clinicId = mAdapter.getItem(position).id;
            TransferTreatmentDetailFragment.launch(_mActivity, clinicId,mCurrentPosition==0);

        });

        loadData();
    }

    private void loadData() {
        String queryType = mCurrentPosition==0?"0":"1";
        String status = statuses.get(statusIndex);
        String doctId = AppContext.getUser().getDoctId();
        QueryRequestBody body = QueryRequestBody.getTransferTreatmentRequestBody(doctId, queryType, status, AppConstant.SIZE_OF_PAGE, mPage);
//        ApiFactory.getPrescriptionApi().getPrescription(body)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<BaseResp<QueryResponseData< OnlinePrescription>>>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mAdapter.getCount() == 0) {
//                            ((TextView) ButterKnife.findById(mRecyclerView.getErrorView(), R.id.message_info)).setText(e.getMessage());
//                            mRecyclerView.showError();
//                            mRecyclerView.getErrorView().setOnClickListener(v -> {
//                                mRecyclerView.showProgress();
//                                loadData();
//                            });
//                        } else {
//                            showRefreshing(false);
//                            ViewUtil.showMessage(e.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onNext(BaseResp<QueryResponseData<OnlinePrescription>> resp) {
//                        showRefreshing(false);
////                        String s=resp.getData().toString();
////                        Log.i(s,s);
//                        processResponse(resp);
//                    }
//                });
    }

    private void processResponse(BaseResp<QueryResponseData<OnlinePrescription>> resp) {
        showRefreshing(false);
        if (!resp.isSuccess()) {
            if (mAdapter.getAllData().isEmpty()) {
                mRecyclerView.getErrorView().setOnClickListener(v -> {
                    mRecyclerView.showProgress();
                    mPage=1;
                    loadData();
                });
            } else {
                ViewUtil.showMessage("加载失败");
            }
            return;
        }else{

            List<OnlinePrescription> clinics = resp.getData().getResultData();
            if (clinics == null || clinics.isEmpty()) {
                if (mAdapter.getAllData().isEmpty()) {
                    mRecyclerView.getEmptyView().setOnClickListener(v -> {
                        mPage=1;
                        mRecyclerView.showProgress();
                        loadData();
                    });
                    mRecyclerView.showEmpty();
                } else {
                    if (mPage == 1) {
                        mAdapter.clear();
                        mRecyclerView.showEmpty();
                    } else {
                        mAdapter.stopMore();
                    }
                }
                return;
            }


            List<OnlinePrescription> targetClinics = new ArrayList<>();
            if (mPage > 1 && !mAdapter.getAllData().isEmpty()) {
                targetClinics.addAll(mAdapter.getAllData());
            }

            targetClinics.addAll(clinics);


            Collections.sort(targetClinics,(obj1,obj2)->{

                int i=obj1.status.compareTo(obj2.status);
                if(i==0){
                    return -1*(obj1.createdDate.compareTo(obj2.createdDate));
                }else{
                    return i;
                }
            });
            mRecyclerView.showRecycler();
            mAdapter.clear();
            mAdapter.addAll(targetClinics);



        }
    }


    private void showRefreshing(final boolean refresh) {
        mRecyclerView.getSwipeToRefresh().post(() -> {
            mRecyclerView.getSwipeToRefresh().setRefreshing(refresh);
        });
    }

    private class MyApplyViewHolder extends BaseViewHolder<TreatMent> {

        private TextView mGroupTitleView;
        private ImageView mAvatarView;
        private TextView mNameView;
        private TextView mGenderView;
        private TextView mAgeView;
        private TextView mHealthView;
        private LinearLayout mTagsView;
        private TextView mDescribeView;
        private TextView mTransToView;
        private TextView mTimeView;
        private TextView mStatusView;
        private Context mContext;

        public MyApplyViewHolder(ViewGroup parent, Context context) {
            super(parent, R.layout.item_prescription_manager1);

            mContext = context;

            mGroupTitleView = $(R.id.tv_group_title);
            mAvatarView = $(R.id.iv_patient_avatar);
            mNameView = $(R.id.tv_patient_name);
            mGenderView = $(R.id.tv_gender);
            mAgeView = $(R.id.tv_age);
            mHealthView = $(R.id.tv_health);
            mTagsView = $(R.id.ll_tag);
            mDescribeView = $(R.id.tv_describe_text);
            mTransToView = $(R.id.tv_transto);
            mTimeView = $(R.id.tv_time);
            mStatusView = $(R.id.tv_status_name);
        }

        @Override
        public void setData(TreatMent data) {
            super.setData(data);

            try {


                Patient patient = data.patient;
                mNameView.setText(patient.getName());
                mAvatarView.setImageResource(patient.getAvatarDrawableRes());
                mGenderView.setText(patient.getFriendlySex());
                mAgeView.setText(String.valueOf(patient.getAge()));
                mHealthView.setText(patient.getMedicalInsuranceName());
                mTransToView.setText(getResources().getString(R.string.transfer_treatment_transto)+data.acceptDoctorName+" "+data.acceptDoctor.getDepartmentName()+" "+data.acceptDoctor.getHospitalName());
                mDescribeView.setText(data.describe);
                mTagsView.removeAllViews();
                List<PatientTag> patientTags = patient.getPatientTags();
                if (patientTags == null || patientTags.isEmpty()) {
                    mTagsView.setVisibility(View.INVISIBLE);
                } else {
                    mTagsView.setVisibility(View.VISIBLE);
                    for (PatientTag patientTag : patientTags) {
                        mTagsView.addView(AppUtil.getTagTextView(mContext, patientTag.getTagName()));
                    }
                }

                mStatusView.setText(data.statusName);

                mTimeView.setText(DateUtil.getCustomTimeStamp(getActivity(),data.createdDate.substring(0,19).replace("T"," "),null));
            } catch (Exception e) {
                e.printStackTrace();
                int i = 0;
            }

        }
    }


    public class StickyHeaderAdapter implements StickyHeaderDecoration.IStickyHeaderAdapter<StickyHeaderAdapter.HeaderHolder> {

        private LayoutInflater mInflater;

        public StickyHeaderAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }


        @Override
        public long getHeaderId(int position) {

            try {


                boolean t=mAdapter.getItem(position).status.equals("602-0001")||mAdapter.getItem(position).status.equals("602-0002");
                return t?0:1;
            }catch (Exception e) {
                Log.i("position",String.valueOf(position));
                return -1;
            }

        }

        @Override
        public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
            final View view = mInflater.inflate(R.layout.header_treatment, parent, false);
            return new HeaderHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {



            viewholder.header.setText(getHeaderId(position)==0?"未完结":"已完成");
        }

        class HeaderHolder extends RecyclerView.ViewHolder {
            public TextView header;

            public HeaderHolder(View itemView) {
                super(itemView);
                header = itemView.findViewById(R.id.head);
            }
        }
    }
}
