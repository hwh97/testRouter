package cn.hwwwwh.routermodule2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class Test2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        TextView tv1 = (TextView) findViewById(R.id.tv1);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            Log.d("line", "====================");
            for (String key: bundle.keySet()) {
                Log.i("Bundle Content", "Key=" + key + ", content=" +bundle.getString(key));
            }
            Log.d("line", "====================");
            tv1.setText("activity2: " + bundle.getString("key1"));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("key", "from test2");
            setResult(1000, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
