package com.java.zhangyiwei_chengjiawen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

public class NewsShowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newsshow_main);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
//        String info = "{\"image\":\"[]\",\"publishTime\":\"2019-07-02 17:39:00\",\"keywords\":[{\"score\":1.0,\"word\":\"朝鲜半岛\"},{\"score\":1.0,\"word\":\"王毅\"},{\"score\":0.6166724321145293,\"word\":\"世界传递\"},{\"score\":0.5497448050872961,\"word\":\"习近平主席\"},{\"score\":0.35424381046082315,\"word\":\"好消息\"},{\"score\":0.3408993308057868,\"word\":\"政治解决\"},{\"score\":0.3408993308057868,\"word\":\"半岛问题\"},{\"score\":0.15700442565535255,\"word\":\"最高领导人\"},{\"score\":0.15484242450077249,\"word\":\"日本大阪\"},{\"score\":0.15484242450077249,\"word\":\"问题政治\"},{\"score\":0.15344141602390313,\"word\":\"正确方向\"},{\"score\":0.14753254361494564,\"word\":\"原则立场\"},{\"score\":0.10421906702046244,\"word\":\"新华社北京\"},{\"score\":0.09903826754839416,\"word\":\"大方向\"}],\"language\":\"zh-CN\",\"video\":\"\",\"title\":\"王毅：期待朝鲜半岛向世界传递更多的好消息\",\"when\":[{\"score\":1.0,\"word\":\"2019-07-02 00:00:00\"},{\"score\":0.6470587740307496,\"word\":\"2019-06-30 00:00:00\"},{\"score\":0.5882352363692079,\"word\":\"2019-07-02 17:39:00\"}],\"content\":\"新华社北京7月2日电 国务委员兼外交部长王毅2日在北京同墨西哥外长埃布拉德共见记者时应询表示，这些天，围绕朝鲜半岛问题，各方开展新一轮良性互动。习近平主席对朝鲜进行成功国事访问，两国最高领导人确认坚持半岛问题政治解决的大方向。刚刚在日本大阪举行的中美元首会晤中，习近平主席向特朗普总统介绍了中方在半岛问题上的原则立场，推动美方显示灵活，与朝方相向而行，包括适时缓解对朝鲜的制裁，通过对话找到解决彼此关切的办法。习近平主席与文在寅总统也交换了意见，支持韩方继续为此做出努力。\\n王毅说，6月30日，我们看到美朝领导人跨过军事边界线，在板门店举行第三次会晤，并就重启对话达成共识。这是美朝两国领导人首次在象征对抗分裂的板门店握手相谈。这一幕是回应民心所盼之举，也是顺应时代潮流之举，表明两国领导人都有志于通过对话谈判寻求共识，解决问题。这是朝着正确方向迈出的重要一步，中方对此表示欢迎和支持，期待今后朝鲜半岛向世界传递更多的好消息。\\n王毅说，世界已经进入21世纪，半岛却仍存有冷战残余，各方之间的敌视和互不信任一直制约着半岛和平稳定和无核化进程。这种不正常的现象早该结束了。有句话说的好，世上无难事，只要肯登攀。半岛局势如今再度出现难得的和平机遇。我们希望美朝双方能将两国领导人的政治意愿尽快转化为对话谈判的实质进展，与各方一道，就实现美朝领导人新加坡会晤时达成的四大目标，特别是实现半岛无核化、构建半岛和平机制作出共同努力，按照分阶段、同步走的思路加快推进政治解决进程，早日实现半岛的长治久安。\",\"url\":\"http://www.gov.cn/guowuyuan/2019-07/02/content_5405381.htm\",\"persons\":[{\"count\":3,\"linkedURL\":\"http://xlore.org/instance/bdi809358\",\"mention\":\"习近平\"},{\"count\":3,\"linkedURL\":\"http://xlore.org/instance/bdi5444585\",\"mention\":\"王毅\"},{\"count\":1,\"linkedURL\":\"\",\"mention\":\"时应询\"},{\"count\":1,\"linkedURL\":\"http://xlore.org/instance/bdi3821432\",\"mention\":\"文在寅\"},{\"count\":1,\"linkedURL\":\"\",\"mention\":\"向特朗普\"},{\"count\":1,\"linkedURL\":\"\",\"mention\":\"埃布拉德\"}],\"newsID\":\"2019070801030a9ee3d30c35488d8ccb4937adb2a329\",\"crawlTime\":\"2019-07-07 03:10:29\",\"organizations\":[{\"count\":1,\"linkedURL\":\"\",\"mention\":\"新华社\"}],\"publisher\":\"其他\",\"locations\":[{\"lng\":-102.0,\"count\":1,\"linkedURL\":\"http://xlore.org/instance/bdi2301818\",\"lat\":23.0,\"mention\":\"墨西哥\"},{\"lng\":123.7039,\"count\":2,\"linkedURL\":\"http://xlore.org/instance/bdi4154080\",\"lat\":49.23038,\"mention\":\"朝鲜\"},{\"lng\":103.8,\"count\":1,\"linkedURL\":\"http://xlore.org/instance/bdi3860580\",\"lat\":1.36667,\"mention\":\"新加坡\"},{\"lng\":135.49499,\"count\":1,\"linkedURL\":\"http://xlore.org/instance/bdi2431179\",\"lat\":34.70186,\"mention\":\"大阪\"},{\"lng\":139.75309,\"count\":1,\"linkedURL\":\"http://xlore.org/instance/bdi3961759\",\"lat\":35.68536,\"mention\":\"日本\"},{\"lng\":123.7039,\"count\":2,\"linkedURL\":\"http://xlore.org/instance/bdi4154212\",\"lat\":49.23038,\"mention\":\"朝鲜半岛\"},{\"count\":2,\"linkedURL\":\"http://xlore.org/instance/bdi4393227\",\"mention\":\"板门店\"},{\"lng\":107.688,\"count\":2,\"linkedURL\":\"http://xlore.org/instance/bdi1561329\",\"lat\":27.2752,\"mention\":\"北京\"}],\"where\":[{\"score\":1.0,\"word\":\"朝鲜半岛\"}],\"category\":\"科技\",\"who\":[{\"score\":1.0,\"word\":\"王毅\"}]}";
        String publishTime;
        String title;
        String content;
        String publisher;
        String category;

        ConstraintLayout newsShowMain = findViewById(R.id.newsShowMain);
        ScrollView view = (ScrollView) LayoutInflater.from(this).inflate(R.layout.newsshow_content, null);
        newsShowMain.addView(view);

        try {
            JSONObject jsonObject = new JSONObject(info);
            publishTime = jsonObject.getString("publishTime");
            title = jsonObject.getString("title");
            content = jsonObject.getString("content");
            publisher = jsonObject.getString("publisher");
            category = jsonObject.getString("category");
            ((TextView)view.findViewById(R.id.newsShowContentTitle)).setText(title);
            ((TextView)view.findViewById(R.id.newsShowContentTime)).setText(publishTime);

        } catch (Exception e){}



    }

}
