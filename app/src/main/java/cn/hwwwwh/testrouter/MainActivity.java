package cn.hwwwwh.testrouter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

import cn.hwwwwh.testrouter.constants.MyRouter;
import cn.hwwwwh.routerlib.RouterManager;
import cn.hwwwwh.routerlib.bean.Router;
import cn.hwwwwh.routerlib.handler.CustomRouterHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText routerUrl = (EditText)findViewById(R.id.router_url);
        final EditText routerName = (EditText)findViewById(R.id.router_name);
        final EditText routerPath = (EditText)findViewById(R.id.router_path);

        Button btnAddRouter = (Button)findViewById(R.id.btn_add_router);
        btnAddRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!routerUrl.getText().toString().trim().isEmpty() && !routerName.getText().toString().trim().isEmpty())
                if(RouterManager.getInstance().addRouter(
                        routerName.getText().toString().trim(), routerUrl.getText().toString().trim(), routerPath.getText().toString().trim()) != null) {
                    Toast.makeText(v.getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final EditText etGoRouter = (EditText)findViewById(R.id.et_go_router);
        Button btnGoRouter = (Button)findViewById(R.id.btn_go_router);
        btnGoRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etGoRouter.getText().toString().trim().equals("")) {
                    RouterManager.getInstance()
                            .withRouterName(etGoRouter.getText().toString().trim())
                            .goRouter();
                }
            }
        });

        Button btnRegister = (Button)findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterManager.getInstance().registerRouterHandler("http", new CustomRouterHandler() {
                    @Override
                    public void onRouterHandler(Router router, Map<String, String> params) {
                        Uri uri = Uri.parse(router.getUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }
        });

        Button btnUnRegister = (Button)findViewById(R.id.btn_un_register);
        btnUnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterManager.getInstance().unRegisterRouterHandler("http");
            }
        });


        Button btnHttp = (Button)findViewById(R.id.btn_http);
        btnHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterManager.getInstance()
                        .withRouterName(MyRouter.httpTestRouter)
                        .goRouter();
            }
        });

        Button button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key1", "value1");
                boolean result = RouterManager.getInstance()
                                    .withRouterName(MyRouter.testRouter)
                                    .withBundle(bundle)
                                    .goRouter(MainActivity.this, 1000);
                if (result)
                    Toast.makeText(v.getContext(), "跳转成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(v.getContext(), "跳转失败", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1000:
                assert data != null;
                Toast.makeText(this.getApplicationContext(), "收到返回数据:" + data.getExtras().getString("key"), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
