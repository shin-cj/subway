package Lines;

import java.awt.Color;

import javax.swing.SwingUtilities;

import Frame.subwayBase;
import InSubway.SubwayManager;

//---------------------------------------------------------
//[9호선] subwayApp9.java
//---------------------------------------------------------
public class subwayApp9 extends subwayBase {
	public static String[] allStationsInLine9 = { "개화역", "김포공항역", "공항시장역", "신방화역", "마곡나루역", "양천향교역", "가양역", "증미역",
			"등촌역", "염창역", "신목동역", "선유도역", "당산역", "국회의사당역", "여의도역", "샛강역", "노량진역", "노들역", "흑석역", "동작역", "구반포역", "신반포역",
			"고속터미널역", "사평역", "신논현역", "언주역", "선정릉역", "삼성중앙역", "봉은사역", "종합운동장역", "삼전역", "석촌고분역", "석촌역", "송파나루역", "한성백제역",
			"올림픽공원역", "둔촌오륜역", "중앙보훈병원역" };

	public subwayApp9() {
		super("9호선", allStationsInLine9, // 역 이름
				new Color(189, 176, 146)); // 9호선 금색
		initSubwayGraph();
		add(Share.BottomMenu.addFooterBar(this));
		setVisible(true);
	}

	private void initSubwayGraph() {
		SubwayManager.registerSection("개화역", "김포공항역", "공항시장역", "신방화역", "마곡나루역", "양천향교역", "가양역", "증미역", "등촌역", "염창역",
				"신목동역", "선유도역", "당산역", "국회의사당역", "여의도역", "샛강역", "노량진역", "노들역", "흑석역", "동작역", "구반포역", "신반포역", "고속터미널역",
				"사평역", "신논현역", "언주역", "선정릉역", "삼성중앙역", "봉은사역", "종합운동장역", "삼전역", "석촌고분역", "석촌역", "송파나루역", "한성백제역",
				"올림픽공원역", "둔촌오륜역", "중앙보훈병원역");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new subwayApp1().setVisible(true));
	}
}