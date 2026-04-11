package Lines;

import java.awt.Color;

import javax.swing.SwingUtilities;

import Frame.subwayBase;

//---------------------------------------------------------
//[4호선] subwayApp4.java
//---------------------------------------------------------
public class subwayApp4 extends subwayBase {
 public subwayApp4() {
     super("4호선", 
         new String[]{"진접역", "오남역", "별내별가람역", "당고개역", "상계역", "노원역", "창동역", "쌍문역", "수유역",
        		 "미아역", "미아사거리역", "길음역", "성신여대입구역", "한성대입구역", "혜화역", "동대문역",
        		 "동대문역사문화공원역", "충무로역", "명동역", "회현역", "서울역", "숙대입구역", "삼각지역",
        		 "신용산역", "이촌역", "동작역", "총신대입구(이수)역", "사당역",

        		 // 과천선 구간
        		 "남태령역", "선바위역", "경마공원역", "대공원역", "과천역", "정부과천청사역", "인덕원역", "평촌역",
        		 "범계역", "금정역",

        		 // 안산선 구간
        		 "산본역", "수리산역", "대야미역", "반월역", "상록수역", "한대앞역", "중앙역", "고잔역", "초지역",
        		 "안산역", "신길온천역", "정왕역", "오이도역"}, 
         new Color(0, 165, 224)); // 4호선 하늘색
     add(Share.BottomMenu.addFooterBar(this));
     setVisible(true);
 }
 public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> new subwayApp2().setVisible(true));
 }
}
