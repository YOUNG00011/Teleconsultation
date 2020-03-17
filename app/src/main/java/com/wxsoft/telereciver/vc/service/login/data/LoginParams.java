package com.wxsoft.telereciver.vc.service.login.data;

import android.text.TextUtils;

import com.wxsoft.telereciver.vc.service.utils.TUPLogUtil;

/**
 * The type Login params data.
 */
public class LoginParams
{
    /**
     * The constant TAG.
     */
    private static final String TAG = LoginParams.class.getSimpleName();
    /**
     * The constant loginParams.
     */
    private static LoginParams loginParams;
    /**
     * The Voip name.
     */
    private String voipName;
    /**
     * The Voip number.
     */
    private String voipNumber;
    /**
     * The Voip password.
     */
    private String voipPassword;
    /**
     * The Proxy server ip.
     */
    private String proxyServerIp;
    /**
     * The Register server ip.
     */
    private String registerServerIp;
    /**
     * The Server port.
     */
    private String serverPort;
    /**
     * The Sip number.
     */
    private String sipNumber;
    /**
     * The Sip impi.
     */
    private String sipImpi;
    /**
     * The Domain.
     */
    private String domain = "";
    /**
     * The User agent.
     */
    private String userAgent = "Huawei TE Mobile";
    /**
     * The Country code.
     */
    private String countryCode = "";
    /**
     * The Outgoing access code.
     */
    private String outgoingAccessCode = "";
    /**
     * The Sip uri.
     */
    private String sipURI = "";
    /**
     * The Local ip address.
     */
    private String localIpAddress = "";

    /**
     * Instantiates a new Login params data.
     */
    private LoginParams()
    {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized LoginParams getInstance()
    {
        if (loginParams == null)
        {
            loginParams = new LoginParams();
            TUPLogUtil.i(TAG, "loginParams is null, create new ins.");
        }
        return loginParams;
    }

    /**
     * Gets register server ip.
     *
     * @return the register server ip
     */
    public String getRegisterServerIp()
    {
        return registerServerIp;
    }

    /**
     * Gets voip name.
     *
     * @return the voip name
     */
    public String getVoipName()
    {
        if (TextUtils.isEmpty(this.voipName))
        {
            this.voipName = this.voipNumber;
        }
        return this.voipName;
    }

    /**
     * Gets voip password.
     *
     * @return the voip password
     */
    public String getVoipPassword()
    {
        return this.voipPassword;
    }

    /**
     * Sets voip name.
     *
     * @param voipName the voip name
     */
    public void setVoipName(String voipName)
    {
        this.voipName = voipName;
    }

    /**
     * Sets voip password.
     *
     * @param voipPassword the voip password
     */
    public void setVoipPassword(String voipPassword)
    {
        this.voipPassword = voipPassword;
    }

    /**
     * Sets proxy server ip.
     *
     * @param proxyServerIp the proxy server ip
     */
    public void setProxyServerIp(String proxyServerIp)
    {
        this.proxyServerIp = proxyServerIp;
    }

    /**
     * Sets register server ip.
     *
     * @param registerServerIp the register server ip
     */
    public void setRegisterServerIp(String registerServerIp)
    {
        this.registerServerIp = registerServerIp;
    }

    /**
     * Sets server port.
     *
     * @param serverPort the server port
     */
    public void setServerPort(String serverPort)
    {
        this.serverPort = serverPort;
    }

    /**
     * Sets sip uri.
     *
     * @param sipURI the sip uri
     */
    public void setSipURI(String sipURI)
    {
        this.sipURI = sipURI;
    }

    /**
     * Sets local ip address.
     *
     * @param localIpAddress the local ip address
     */
    public void setLocalIpAddress(String localIpAddress)
    {
        this.localIpAddress = localIpAddress;
    }

    /**
     * Gets proxy server ip.
     *
     * @return the proxy server ip
     */
    public String getProxyServerIp()
    {
        return this.proxyServerIp;
    }

    /**
     * Gets server port.
     *
     * @return the server port
     */
    public String getServerPort()
    {
        return this.serverPort;
    }

    /**
     * Gets domain.
     *
     * @return the domain
     */
    public String getDomain()
    {
        return this.domain;
    }

    /**
     * Gets sip uri.
     *
     * @return the sip uri
     */
    public String getSipURI()
    {
        return this.sipURI;
    }

    /**
     * Gets user agent.
     *
     * @return the user agent
     */
    public String getUserAgent()
    {
        return this.userAgent;
    }

    /**
     * Gets country code.
     *
     * @return the country code
     */
    public String getCountryCode()
    {
        return this.countryCode;
    }

    /**
     * Gets outgoing access code.
     *
     * @return the outgoing access code
     */
    public String getOutgoingAccessCode()
    {
        return this.outgoingAccessCode;
    }

    /**
     * Gets local ip address.
     *
     * @return the local ip address
     */
    public String getLocalIpAddress()
    {
        return this.localIpAddress;
    }

    /**
     * Gets sip impi.
     *
     * @return the sip impi
     */
    public String getSipImpi()
    {
        return sipImpi;
    }

    /**
     * Sets sip impi.
     *
     * @param sipImpi the sip impi
     */
    public void setSipImpi(String sipImpi)
    {
        this.sipImpi = sipImpi;
    }


    /**
     * Gets sip number.
     *
     * @return the sip number
     */
    public String getSipNumber()
    {
        return sipNumber;
    }

    /**
     * Sets sip number.
     *
     * @param sipNumber the sip number
     */
    public void setSipNumber(String sipNumber)
    {
        this.sipNumber = sipNumber;
    }


    /**
     * Gets voip number.
     *
     * @return the voip number
     */
    public String getVoipNumber()
    {
        return this.voipNumber;
    }

    /**
     * Sets login params.
     *
     * @param loginParams the login params
     */
    public static void setLoginParams(LoginParams loginParams)
    {
        LoginParams.loginParams = loginParams;
    }

    /**
     * Sets voip number.
     *
     * @param voipNumber the voip number
     */
    public void setVoipNumber(String voipNumber)
    {
        this.voipNumber = voipNumber;
    }


    /**
     * Init data.
     */
    public void initData()
    {
            String tempSipUri = sipURI;
            if (!TextUtils.isEmpty(tempSipUri))
            {
                String[] tempVoipNumber = tempSipUri.split("@");
                this.sipNumber = tempVoipNumber[0];
                if (tempVoipNumber.length > 1)
                {
                    this.domain = tempVoipNumber[1];
                }
            }
            else
            {
                String tempVoipNumber1 = voipNumber;
                if (!TextUtils.isEmpty(tempVoipNumber1) && tempVoipNumber1.contains("@"))
                {
                    String[] numberAndDomain = tempVoipNumber1.split("@");
                    this.sipNumber = numberAndDomain[0];
                    if (numberAndDomain.length > 1)
                    {
                        this.domain = numberAndDomain[1];
                    }
                }
            }
    }


}
