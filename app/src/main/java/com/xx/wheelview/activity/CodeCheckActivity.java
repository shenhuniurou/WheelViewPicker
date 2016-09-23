package com.zj.wheelview.activity;

import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zj.wheelview.NetApi;
import com.zj.wheelview.R;
import com.zj.wheelview.utils.CommonUtil;

import org.json.JSONObject;

import butterknife.InjectView;
import cz.msebera.android.httpclient.Header;

public class CodeCheckActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.etCheckCode)
    EditText etCheckCode;
    TextView one, two, three, four, five, six, seven, eight, night, zero, dian, tvCheck;
    ImageView delete, reset;
    @InjectView(R.id.llCodeStateValid)
    LinearLayout llCodeStateValid;
    @InjectView(R.id.llCodeStateInvalid)
    LinearLayout llCodeStateInvalid;

    StringBuffer sb;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_code_check;
    }

    @Override
    protected int getTitleResId() {
        return R.string.title_activity_code_check;
    }

    @Override
    protected void initViews() {
        //禁止系统的软键盘
        etCheckCode.setInputType(InputType.TYPE_NULL);
        sb = new StringBuffer("");
        one = (TextView) findViewById(R.id.one);
        one.setOnClickListener(this);
        two = (TextView) findViewById(R.id.two);
        two.setOnClickListener(this);
        three = (TextView) findViewById(R.id.three);
        three.setOnClickListener(this);
        four = (TextView) findViewById(R.id.four);
        four.setOnClickListener(this);
        five = (TextView) findViewById(R.id.five);
        five.setOnClickListener(this);
        six = (TextView) findViewById(R.id.six);
        six.setOnClickListener(this);
        seven = (TextView) findViewById(R.id.seven);
        seven.setOnClickListener(this);
        eight = (TextView) findViewById(R.id.eight);
        eight.setOnClickListener(this);
        night = (TextView) findViewById(R.id.night);
        night.setOnClickListener(this);
        zero = (TextView) findViewById(R.id.zero);
        zero.setOnClickListener(this);
        delete = (ImageView) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        reset = (ImageView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        dian = (TextView) findViewById(R.id.dian) ;
        dian.setOnClickListener(this);
        tvCheck = (TextView) findViewById(R.id.tvCheck);
        tvCheck.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one:
                sb.append("1");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.two:
                sb.append("2");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.three:
                sb.append("3");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.four:
                sb.append("4");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.five:
                sb.append("5");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.six:
                sb.append("6");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.seven:
                sb.append("7");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.eight:
                sb.append("8");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.night:
                sb.append("9");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.zero:
                sb.append("0");
                etCheckCode.setText(sb.toString());
                break;
            case R.id.delete:
                if(sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    etCheckCode.setText(sb.toString());
                }
                if (sb.length() == 0) {
                    llCodeStateValid.setVisibility(View.GONE);
                    llCodeStateInvalid.setVisibility(View.GONE);
                }
                break;
            case R.id.tvCheck:
                checkPayCode();
                break;
        }
    }

    public void checkPayCode() {
        if ("".equals(sb.toString())) {
            CommonUtil.showToastMessage(CodeCheckActivity.this, "请输入支付码");
            return;
        }
        showWaitDialog();
        NetApi.checkPayCode(CodeCheckActivity.this, sb.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideWaitDialog();
                try {
                    if (responseBody != null) {
                        String result = new String(responseBody);
                        LogUtil.log("result---" + result);
                        JSONObject obj = new JSONObject(result);
                        String resultCode = obj.getString("resultCode");
                        if ("00".equals(resultCode)) {
                            llCodeStateValid.setVisibility(View.VISIBLE);
                            llCodeStateInvalid.setVisibility(View.GONE);
                        }else {
                            llCodeStateValid.setVisibility(View.GONE);
                            llCodeStateInvalid.setVisibility(View.VISIBLE);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideWaitDialog();
                if (responseBody != null) {
                    LogUtil.log("responseBody---" + new String(responseBody));
                }
            }
        });
    }

}
