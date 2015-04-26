# MedicalApplicition
湘雅医疗数据 （基于android平台医疗数据采集系统)

[Download APK](https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication.apk)

##项目整体介绍
  * 旨在为医生打造医疗数据信息采集平台<br>
  * 实现随时随地上传文字，图片，视频，音频等病人记录信息<br>
  * 主要功能架构图<br>
<div class='raw'>
 <img src='https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication/app.png' height="300px" width="500px" style='border: #f1f1f1 solid 1px'/>
</div>

##项目主体采用fragmentactivity实现fragment切换
  * **需要了解fragment工作机制和生命周期<br>**
```xml
mFragments = new Fragment[3];
fragmentManager = getSupportFragmentManager();
mFragments[0] = fragmentManager.findFragmentById(R.id.fragment_main);
mFragments[1] = fragmentManager.findFragmentById(R.id.fragment_file);
mFragments[2] = fragmentManager.findFragmentById(R.id.fragment_mine);
fragmentTransaction = fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);
fragmentTransaction.show(mFragments[0]).commit();
```
  * **实现效果图<br>**
<div class='row'>
        <img src='https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication/mainPage.png' width="250px" style='border: #f1f1f1 solid 1px'/>
        <img src='https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication/filePage.png' width="250px" style='border: #f1f1f1 solid 1px'/>
        <img src='https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication/minePage.png' width="250px" style='border: #f1f1f1 solid 1px'/>
    </div>

###主体中的关键技术
  * **首页的listview实现了下拉刷新和点击加载更多功能，在listview中定义刷新和加载的接口，在activity中实现接口。**
```
	public interface IXListViewListener {
		//刷新
		public void onRefresh();
		//加载更多
		public void onLoadMore();
	}
```
  * **首页获取数据请求，采用Volley框架，获取服务器返回的json数据后，使用fastjson解析。解析是直接使用静态类解析数组数据。**
```
private void getData(final int method) { // method=1 重新生成list method=2 增长list
        doctor_id = UserInfo.user.getDoctor_id();
        String url = Configuration.get_allPatientUrl + "?index="
                + index + "&&doctor_id=" + doctor_id;
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jar = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<PatientInfo> list = JSON.parseArray(
                                response.toString(), PatientInfo.class);
                        if (method == 1) {
                            patientInfoList.clear();
                        }
                        for (int i = 0; i < list.size(); i++) {
                            patientInfoList.add(list.get(i));
                        }
                        if (!patientInfoList.isEmpty()) {
                            lastActivityId = patientInfoList.get(patientInfoList.size() - 1).getId();
                        }
                        if (method == 1) {
                            stopRefreshLoad();
                        } else {
                            stopMoreLoad();
                        }
                        mainIndexAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "病人列表获取失败", Toast.LENGTH_SHORT).show();
                        stopMoreLoad();
                    }
                });
        mRequestQueue.add(jar);
    }
```
  * **文件页面中，主要使用可下拉刷新的ExpandableListView,重写ExpandableListView实现下拉的监听。**

  * **我的页面主要就是一个圆形的图片控件，继承MaskedImage实现CircularLoginImage。**

### 对于多媒体数据实时上传
  * **调用系统接口，实现视频，音频，图片的拍摄和录制，同时指定文件的保存路径，以便上传后清除缓存。**
  * **上传时将媒体文件转换为file文件，采用AsyncHttpClient发送请求，将数据提交到服务器。**
```
    //    /*
    //    * 上传文件
    //	  */
    @SuppressLint("ShowToast")
    public void upload(File file, Integer type) {
        RequestParams params = new RequestParams();
        try {
            params.put("doctor_id", UserInfo.user.getDoctor_id().toString());
            params.put("suffer_id", patientInfo.getId().toString());
            params.put("resource_type", type.toString());
            params.put("resource_size", UsedTools.generateFileSize(file));
            params.put("resource_category", category);
            params.put("resource_url", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = Configuration.newResourceUrl;
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @SuppressLint("ShowToast")
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    successResponse = response.get("success").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (successResponse.equals("1")) {
                    Toast.makeText(UploadRecord.this, "上传成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UploadRecord.this, "上传失败", Toast.LENGTH_LONG).show();
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                Toast.makeText(UploadRecord.this, "网络访问异常,请重试", Toast.LENGTH_LONG).show();

            }
        });
    }
```
* **上传前还会对多媒体数据进行压缩，以减轻服务器的压力。**

### 其他功能
* 短信验证找回密码
* 登录，注册
* 修改个人信息
* 上传和修改头像
* 在线播放上传的视频和音频

### 引用的外部jar文件
* [fastjson.jar](https://github.com/sjaiwl/MedicalApplicition/blob/master/app/libs/fastjson.jar)
* [gson-2.1.jar](https://github.com/sjaiwl/MedicalApplicition/blob/master/app/libs/gson-2.1.jar)
* [volley.jar](https://github.com/sjaiwl/MedicalApplicition/blob/master/app/libs/volley.jar)
* [SMSSDK-1.1.9.jar](https://github.com/sjaiwl/MedicalApplicition/blob/master/app/libs/SMSSDK-1.1.9.jar)

