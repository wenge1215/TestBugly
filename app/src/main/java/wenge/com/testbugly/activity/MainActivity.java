package wenge.com.testbugly.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import wenge.com.testbugly.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test(View view) {
//        String test=null;
//        new Throwable("故意整个异常");
//        test.toString();
        Toast.makeText(this, "测试 walle 多渠道打包，是否ok", Toast.LENGTH_SHORT).show();
    }


    public void update(View view) {
        startActivity(new Intent(this,TestHotFix.class));
    }
}
