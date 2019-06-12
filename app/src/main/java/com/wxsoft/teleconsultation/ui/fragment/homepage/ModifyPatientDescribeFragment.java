package com.wxsoft.teleconsultation.ui.fragment.homepage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wxsoft.teleconsultation.AppContext;
import com.wxsoft.teleconsultation.R;
import com.wxsoft.teleconsultation.entity.BaseResp;
import com.wxsoft.teleconsultation.entity.Patient;
import com.wxsoft.teleconsultation.http.ApiFactory;
import com.wxsoft.teleconsultation.ui.base.BaseFragment;
import com.wxsoft.teleconsultation.ui.base.FragmentArgs;
import com.wxsoft.teleconsultation.ui.base.FragmentContainerActivity;
import com.wxsoft.teleconsultation.util.ViewUtil;

import butterknife.BindView;
import butterknife.OnTextChanged;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ModifyPatientDescribeFragment extends BaseFragment {

    public static void launch(Fragment from, Patient patient) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_PATIENT, patient);
        FragmentContainerActivity.launchForResult(from, ModifyPatientDescribeFragment.class, args, REQUEST_MODIFY_PATIENT_DESC);
    }

    public static void launch(Activity from, Patient patient) {
        FragmentArgs args = new FragmentArgs();
        args.add(FRAGMENT_ARGS_PATIENT, patient);
        FragmentContainerActivity.launchForResult(from, ModifyPatientDescribeFragment.class, args, REQUEST_MODIFY_PATIENT_DESC);
    }

    private static final String FRAGMENT_ARGS_PATIENT = "FRAGMENT_ARGS_PATIENT";

    public static final int REQUEST_MODIFY_PATIENT_DESC = 105;
    public static final String KEY_DESCRIBE = "KEY_DESCRIBE";
    private static final int INPUT_MAX_LENGTH = 200;

    @BindView(R.id.et_content)
    EditText mContentView;

    @BindView(R.id.tv_count)
    TextView mCountView;

    private Patient mPatient;

    @OnTextChanged(R.id.et_content)
    void textChanged(CharSequence s, int start, int before, int count) {
        mCountView.setText(s.length() + "/" + INPUT_MAX_LENGTH);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modify_patient_describe;
    }

    @Override
    protected void setupViews(View view, Bundle savedInstanceState) {
        setupToolbar();
        mPatient = (Patient) getArguments().getSerializable(FRAGMENT_ARGS_PATIENT);
        String describe = mPatient.getDescribe();
        int currentCount = 0;
        if (!TextUtils.isEmpty(describe)) {
            mContentView.setText(describe);
            mContentView.setSelection(describe.length());
            currentCount = describe.length();
        }
        mCountView.setText(currentCount + "/" + INPUT_MAX_LENGTH);
        mContentView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(INPUT_MAX_LENGTH)});
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_one,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action:
                commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        FragmentContainerActivity activity = (FragmentContainerActivity) getActivity();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(R.string.modify_desc_title);
        setHasOptionsMenu(true);
    }

    private void commit() {
        String content = mContentView.getText().toString();
        ViewUtil.createProgressDialog(_mActivity, "");
        ApiFactory.getPatientManagerApi().savePatientDescription(mPatient.getId(),AppContext.getUser().getDoctId(), content)
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
                            Intent intent = new Intent();
                            intent.putExtra(KEY_DESCRIBE, content);
                            _mActivity.setResult(RESULT_OK, intent);
                            _mActivity.finish();
                        } else {
                            ViewUtil.showMessage(resp.getMessage());
                        }
                    }
                });
    }
}
