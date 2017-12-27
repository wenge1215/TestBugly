package wenge.com.testbugly.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devilwwj.jni.TestJNI;
import com.tencent.bugly.beta.Beta;

import wenge.com.testbugly.R;
import wenge.com.testbugly.utils.BaseUtils;
import wenge.com.testbugly.utils.LoadBugClass;

public class TestHotFix extends AppCompatActivity implements View.OnClickListener {
//    /**
//     * 如果想更新so，可以将System.loadLibrary替换成Beta.loadLibrary
//     */
//    static {
//        Beta.loadLibrary("testSo.jar");
//    }

    private TextView tvCurrentVersion;
    private Button btnShowToast;
    private Button btnLoadPatch;
    private Button btnKillSelf;
    private Button btnLoadLibrary;
    private Button btnDownloadPatch;
    private Button btnUserPatch;
    private Button btnCheckUpgrade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_hot_fix);

        tvCurrentVersion = (TextView) findViewById(R.id.tvCurrentVersion);
        btnShowToast = (Button) findViewById(R.id.btnShowToast);
        btnShowToast.setOnClickListener(this);
        btnKillSelf = (Button) findViewById(R.id.btnKillSelf);
        btnKillSelf.setOnClickListener(this);
        btnLoadPatch = (Button) findViewById(R.id.btnLoadPatch);
        btnLoadPatch.setOnClickListener(this);
        btnLoadLibrary = (Button) findViewById(R.id.btnLoadLibrary);
        btnLoadLibrary.setOnClickListener(this);
        btnDownloadPatch = (Button) findViewById(R.id.btnDownloadPatch);
        btnDownloadPatch.setOnClickListener(this);
        btnUserPatch = (Button) findViewById(R.id.btnPatchDownloaded);
        btnUserPatch.setOnClickListener(this);
        btnCheckUpgrade = (Button) findViewById(R.id.btnCheckUpgrade);
        btnCheckUpgrade.setOnClickListener(this);

        tvCurrentVersion.setText("当前版本：" + BaseUtils.getCurrentVersion(this));
    }


    /**
     * 根据应用patch包
     * <p>
     * 应用patch包前，提示"This is a bug class"
     * 应用patch包之后，提示"The bug has fixed"
     */
    public void testToast() {
        Toast.makeText(this, LoadBugClass.getBugString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShowToast:  // 测试热更新功能
                testToast();
                break;
            case R.id.btnKillSelf: // 杀死进程
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.btnLoadPatch: // 本地加载补丁测试
                Beta.applyTinkerPatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
                break;
            case R.id.btnLoadLibrary: // 本地加载so库测试
                TestJNI testJNI = new TestJNI();
                testJNI.createANativeCrash();
                break;
            case R.id.btnDownloadPatch:
                Beta.downloadPatch();
                break;
            case R.id.btnPatchDownloaded:
                Beta.applyDownloadedPatch();
                break;
            case R.id.btnCheckUpgrade:
                Beta.checkUpgrade();
                break;
            default:
                Toast.makeText(this, "没有符合要求的条件", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
