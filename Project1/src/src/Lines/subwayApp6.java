package Lines;

import java.awt.Color;

import javax.swing.SwingUtilities;

import Frame.subwayBase;
import InSubway.SubwayManager;

//---------------------------------------------------------
//[6호선] subwayApp6.java
//---------------------------------------------------------
public class subwayApp6 extends subwayBase {
	private static String[] allStationsInLine6 = { "응암역", "역촌역", "불광역", "독바위역", "연신내역", "구산역", "응암역", "새절역", "증산역",
			"디지털미디어시티역", "월드컵경기장역", "마포구청역", "망원역", "합정역", "상수역", "광흥창역", "대흥역", "공덕역", "효창공원앞역", "삼각지역", "녹사평역",
			"이태원역", "한강진역", "버티고개역", "약수역", "청구역", "신당역", "동묘앞역", "창신역", "보문역", "안암역", "고려대역", "월곡역", "상월곡역", "돌곶이역",
			"석계역", "태릉입구역", "화랑대역", "봉화산역", "신내역" };

	public subwayApp6() {
		super("6호선", allStationsInLine6, new Color(205, 124, 47)); // 6호선 황토색
		add(Share.BottomMenu.addFooterBar(this));
		initSubwayGraph();
		setVisible(true);
	}

	private void initSubwayGraph() {
		SubwayManager.registerSection("응암역", "역촌역", "불광역", "독바위역", "연신내역", "구산역", "응암역", "새절역", "증산역", "디지털미디어시티역",
				"월드컵경기장역", "마포구청역", "망원역", "합정역", "상수역", "광흥창역", "대흥역", "공덕역", "효창공원앞역", "삼각지역", "녹사평역", "이태원역", "한강진역",
				"버티고개역", "약수역", "청구역", "신당역", "동묘앞역", "창신역", "보문역", "안암역", "고려대역", "월곡역", "상월곡역", "돌곶이역", "석계역",
				"태릉입구역", "화랑대역", "봉화산역", "신내역");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new subwayApp2().setVisible(true));
	}
}
