package com.wxsoft.telereciver.vc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.tup.confctrl.CCAddAttendeeInfo;
import com.huawei.tup.confctrl.CCIPAddr;
import com.huawei.tup.confctrl.ConfctrlAttendeeMediax;
import com.huawei.tup.confctrl.ConfctrlConfEnvType;
import com.huawei.tup.confctrl.ConfctrlIPVersion;
import com.huawei.tup.confctrl.ConfctrlSiteCallTerminalType;
import com.huawei.tup.confctrl.ConfctrlVideoProtocol;
import com.huawei.tup.confctrl.sdk.TupConfBookVcOnPremiseConfInfo;
import com.huawei.tup.login.LoginAuthorizeResult;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.vc.service.conf.ConferenceService;
import com.wxsoft.telereciver.vc.service.login.LoginService;
import com.wxsoft.telereciver.vc.service.login.data.LoginParams;
import com.wxsoft.telereciver.vc.service.notify.VCConfNotify;
import com.wxsoft.telereciver.vc.service.utils.TUPLogUtil;
import com.wxsoft.telereciver.vc.service.utils.Tools;

import java.util.ArrayList;
import java.util.List;


public class CreateConfActivity extends Activity implements VCConfNotify {

    public static void launch(Activity from, ArrayList<String> numbers) {
        Intent intent = new Intent(from, CreateConfActivity.class);
        intent.putExtra(EXTRA_NUMBERS, numbers);
        from.startActivity(intent);
    }

    private static final String EXTRA_NUMBERS = "EXTRA_NUMBERS";
    private static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";

    private TextView tv;
    private LoginParams loginParams = LoginParams.getInstance();
    private int vctype = LoginService.getInstance().getVcType();
    private Button createConfBtn;
    private Button addPatBtn;
    private Button delPatBtn;
    private Button searchPatBtn;
    private Button addDataConfBtn;
    private EditText participantNumEt;
    private EditText confNameEt;
    private EditText confPwdEt;
    private TextView patTv;
    private LoginAuthorizeResult hostedLoginResult = LoginService.getInstance().getHostedLoginResult();

    private List<CCAddAttendeeInfo> lists = new ArrayList<>();
    private List<ConfctrlAttendeeMediax> attendees = new ArrayList<>();

    private int hostedMediaType = 4;

    private int dataValue = 0;

    private ConferenceService conferenceService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createconf);
        ArrayList<String> nums = getIntent().getStringArrayListExtra(EXTRA_NUMBERS);
        conferenceService = ConferenceService.getInstance();
        for (String number : nums) {
            lists.add(numToAttendee(number));
        }
        conferenceService.registerVCConfNotify(this);

        conferenceService.setConfType(ConfctrlConfEnvType.CONFCTRL_E_CONF_ENV_ON_PREMISE_VC);
        conferenceService.setConfServer(loginParams.getRegisterServerIp(), Integer.parseInt(loginParams.getServerPort()));
        conferenceService.setAuthAccountInfo(loginParams.getSipImpi(), loginParams.getVoipPassword());

        new Handler().postDelayed(() -> {
            String confName = "video";
            String confPwd = "123";

            String number = loginParams.getSipImpi();
            TupConfBookVcOnPremiseConfInfo confInfo = new TupConfBookVcOnPremiseConfInfo();
            confInfo.setSitecallType(0);

            confInfo.setServerAddr(new CCIPAddr(loginParams.getRegisterServerIp(), ConfctrlIPVersion.CC_IP_V4));
            confInfo.setLocalAddr(new CCIPAddr(Tools.getLocalIp(), ConfctrlIPVersion.CC_IP_V4));
            CCAddAttendeeInfo attendeeInfo = new CCAddAttendeeInfo();
            CCIPAddr ccipAddr = new CCIPAddr();
            ccipAddr.setIp(Tools.getLocalIp());
            ccipAddr.setIpVer(ConfctrlIPVersion.CC_IP_V4);
            attendeeInfo.setTerminalIpAddr(ccipAddr);
            attendeeInfo.setTerminalType(ConfctrlSiteCallTerminalType.CC_sip);
            attendeeInfo.setSiteBandwidth(1920);
            attendeeInfo.setNumber(number);
            attendeeInfo.setNumberLen(number.length());
            attendeeInfo.setTerminalId(number);
            attendeeInfo.setTerminalIdLength(number.length());
            attendeeInfo.setUri(number + "@" + loginParams.getRegisterServerIp());
            confInfo.setCcAddterminalInfo(lists);

            confInfo.setSitenumber(lists.size());
            confInfo.setPwdLen(6); //max length is 6
            confInfo.setConfName(confName);
            confInfo.setConfNameLen(confName.length());
            confInfo.setPucPwd(confPwd);
            confInfo.setSitecallMode(0); //CC_SITE_CALL_MODE_REPORT
            confInfo.setHasDataConf(dataValue);
            confInfo.setVideoProto(ConfctrlVideoProtocol.CC_VIDEO_PROTO_BUTT);
            int ret = conferenceService.bookOnPremiseReservedConf(confInfo);
            TUPLogUtil.i("ReservedConf", "result=" + ret);
        }, 1000);
    }


    private CCAddAttendeeInfo numToAttendee(String num) {
        CCAddAttendeeInfo attendeeInfo = new CCAddAttendeeInfo();
        attendeeInfo.setTerminalType(ConfctrlSiteCallTerminalType.CC_sip);
        attendeeInfo.setSiteBandwidth(1920);
        attendeeInfo.setNumber(num);
        attendeeInfo.setNumberLen(num.length());
        attendeeInfo.setTerminalId(num);
        attendeeInfo.setTerminalIdLength(num.length());
        attendeeInfo.setUri(num + "@" + loginParams.getRegisterServerIp());
        return attendeeInfo;
    }

    @Override
    public void onBookReservedConfResult(int result)
    {
        if (result == 0)
        {
            finish();
        }
        else
        {
            CallActivity.getInstance().finish();
            finish();
            Toast.makeText(CreateConfActivity.this, "create conf failed,reason:" + result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CallActivity.getInstance().finish();
        finish();
    }
}
