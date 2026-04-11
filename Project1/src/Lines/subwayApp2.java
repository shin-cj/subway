package Lines;

import java.awt.Color;

import javax.swing.SwingUtilities;

import Frame.subwayBase;

//---------------------------------------------------------
//[2호선] subwayApp2.java
//---------------------------------------------------------
public class subwayApp2 extends subwayBase {
 public subwayApp2() {
     super("2호선", 
         new String[]{"시청역", "을지로입구역", "을지로3가역", "을지로4가역", "동대문역사문화공원역", "신당역", "상왕십리역",
        		 "왕십리역", "한양대역", "뚝섬역", "성수역", "건대입구역", "구의역", "강변역", "잠실나루역", "잠실역",
        		 "잠실새내역", "종합운동장역", "삼성역", "선릉역", "역삼역", "강남역", "교대역", "서초역", "방배역",
        		 "사당역", "낙성대역", "서울대입구역", "봉천역", "신림역", "신대방역", "구로디지털단지역", "대림역",
        		 "신도림역", "문래역", "영등포구청역", "당산역", "합정역", "홍대입구역", "신촌역", "이대역",
        		 "아현역", "충정로역",

        		 // 성수 지선 (성수 → 용답)
        		 "용답역", "신답역", "용두역", "신설동역",

        		 // 신도림 지선 (신도림 → 까치산)
        		 "도림천역", "양천구청역", "신정네거리역", "까치산역"}, 
         new Color(0, 162, 71)); // 2호선 초록색
     add(Share.BottomMenu.addFooterBar(this));
     setVisible(true);
 }
 public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> new subwayApp2().setVisible(true));
 }
}
