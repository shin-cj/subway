package Lines;

import java.awt.Color;

import javax.swing.SwingUtilities;

import Frame.subwayBase;

//---------------------------------------------------------
//[5호선] subwayApp5.java
//---------------------------------------------------------
public class subwayApp5 extends subwayBase {
 public subwayApp5() {
     super("5호선", 
         new String[]{"방화역", "개화산역", "김포공항역", "송정역", "마곡역", "발산역", "우장산역", "화곡역",
        		 "까치산역", "신정역", "목동역", "오목교역", "양평역", "영등포구청역", "영등포시장역",
        		 "신길역", "여의도역", "여의나루역", "마포역", "공덕역", "애오개역", "충정로역",
        		 "서대문역", "광화문역", "종로3가역", "을지로4가역", "동대문역사문화공원역",
        		 "청구역", "신금호역", "행당역", "왕십리역", "마장역", "답십리역", "장한평역",
        		 "군자역", "아차산역", "광나루역", "천호역", "강동역",

        		 // 상일동 방향
        		 "길동역", "굽은다리역", "명일역", "고덕역", "상일동역",

        		 // 하남 방향 (하남선)
        		 "강일역", "미사역", "하남풍산역", "하남시청역", "하남검단산역"}, 
         new Color(153, 108, 172)); // 5호선 보라색
     add(Share.BottomMenu.addFooterBar(this));
     setVisible(true);
 }
 public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> new subwayApp2().setVisible(true));
 }
}
