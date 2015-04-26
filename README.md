# MedicalApplicition
湘雅医疗数据 （基于android平台医疗数据采集系统）
##项目整体介绍
  * 旨在为医生打造医疗数据信息采集平台<br>
  * 实现随时随地上传文字，图片，视频，音频等病人记录信息<br>
  * 主要功能架构图<br>
<div class='raw'>
 <img src='https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication/app.png' height="300px" width="500px" style='border: #f1f1f1 solid 1px'/>
</div>

##项目主体采用fragmentactivity实现fragment切换
  * 需要了解fragment工作机制和生命周期<br>
mFragments = new Fragment[3];<br>
fragmentManager = getSupportFragmentManager();<br>
mFragments[0] = fragmentManager.findFragmentById(R.id.fragment_main);<br>
mFragments[1] = fragmentManager.findFragmentById(R.id.fragment_file);<br>
mFragments[2] = fragmentManager.findFragmentById(R.id.fragment_mine);<br>
fragmentTransaction = fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);<br>
fragmentTransaction.show(mFragments[0]).commit();<br>
  * 实现效果图<br>
<div class='row'>
        <img src='https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication/mainPage.png' width="250px" style='border: #f1f1f1 solid 1px'/>
        <img src='https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication/filePage.png' width="250px" style='border: #f1f1f1 solid 1px'/>
        <img src='https://github.com/sjaiwl/image_folder/blob/master/MedicalApplication/minePage.png' width="250px" style='border: #f1f1f1 solid 1px'/>
    </div>
