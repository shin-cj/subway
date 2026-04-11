package Lines;

import java.awt.Color;

import javax.swing.SwingUtilities;

import Frame.subwayBase;

//---------------------------------------------------------
//[7호선] subwayApp7.java
//---------------------------------------------------------
public class subwayApp7 extends subwayBase {
 public subwayApp7() {
     super("7호선", 
         new String[]{"장암역", "도봉산역", "수락산역", "마들역", "노원역", "중계역", "하계역", "공릉역",
        		 "태릉입구역", "먹골역", "중화역", "상봉역", "면목역", "사가정역", "용마산역",
        		 "중곡역", "군자역", "어린이대공원역", "건대입구역", "뚝섬유원지역", "청담역",
        		 "강남구청역", "학동역", "논현역", "반포역", "고속터미널역", "내방역", "이수역",
        		 "남성역", "숭실대입구역", "상도역", "장승배기역", "신대방삼거리역", "보라매역",
        		 "신풍역", "대림역", "남구로역", "가산디지털단지역", "철산역", "광명사거리역",
        		 "천왕역", "온수역",

        		 // 인천 연장 구간
        		 "까치울역", "부천종합운동장역", "춘의역", "신중동역", "부천시청역",
        		 "상동역", "삼산체육관역", "굴포천역", "부평구청역", "산곡역",
        		 "석남역"}, 
         new Color(116, 127, 0)); // 7호선 카키색
     add(Share.BottomMenu.addFooterBar(this));
     setVisible(true);
 }
 public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> new subwayApp2().setVisible(true));
 }
}
