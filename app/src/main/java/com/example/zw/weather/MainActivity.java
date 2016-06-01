package com.example.zw.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RequestQueue mQueue;
    private static final String URL = "http://apis.baidu.com/apistore/weatherservice/citylist";

    private EditText cityname;
    private TextView content;
    private Button search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mQueue = Volley.newRequestQueue(this);
    }

    private void initView() {
        cityname = (EditText) findViewById(R.id.city_name);
        content = (TextView) findViewById(R.id.content);
        search = (Button) findViewById(R.id.search_btn);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityname.getText().toString().trim();
                if (cityName.length() != 0){
                    String url = null;
                    try {
                        //为参数进行utf-8编码
                        url = URL + "?cityname=" + URLEncoder.encode(cityName,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.v(TAG,url);
                    StringRequest stringRequest = new StringRequest(url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    try {
                                        JSONObject res = new JSONObject(s);
                                        if (res.getInt("errNum") == 0){
                                            JSONArray data = res.getJSONArray("retData");
                                            for (int i = 0;i<data.length();i++){
                                                JSONObject info = data.getJSONObject(i);
                                                String province = info.getString("province_cn");
                                                String district = info.getString("district_cn");
                                                String name = info.getString("name_cn");
                                                Log.d(TAG, "onResponse: " + province + district + name);
                                                content.setText(province + district + name);
                                            }
                                        }else {
                                            content.setText(res.getString("errMsg"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.d(TAG, "onErrorResponse: " + volleyError.getMessage());
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String,String> map = new HashMap<String, String>();
                            map.put("Charset", "UTF-8");
                            map.put("apikey","9912f92ac7887dfbfa06f1c22984f60c");
                            return map;
                        }
                    };
                    mQueue.add(stringRequest);
                }
            }
        });
    }
}
