package Lines;

import java.awt.Color;

import javax.swing.SwingUtilities;

import Frame.subwayBase;

//---------------------------------------------------------
//[3호선] subwayApp3.java
//---------------------------------------------------------
public class subwayApp3 extends subwayBase {
 public subwayApp3() {
     super("3호선", 
         new String[]{"대화역", "주엽역", "정발산역", "마두역", "백석역", "대곡역", "화정역", "원당역", "원흥역", "삼송역",
        		 "지축역", "구파발역", "연신내역", "불광역", "녹번역", "홍제역", "무악재역", "독립문역", "경복궁역",
        		 "안국역", "종로3가역", "을지로3가역", "충무로역", "동대입구역", "약수역", "금호역", "옥수역",
        		 "압구정역", "신사역", "잠원역", "고속터미널역", "교대역", "남부터미널역", "양재역", "매봉역",
        		 "도곡역", "대치역", "학여울역", "대청역", "일원역", "수서역", "가락시장역", "경찰병원역", "오금역"}, 
         new Color(239, 124, 28)); // 3호선 주황색
     add(Share.BottomMenu.addFooterBar(this));
     setVisible(true);
 }
 public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> new subwayApp2().setVisible(true));
 }
}
