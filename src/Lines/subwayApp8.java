package Lines;

import java.awt.Color;

import javax.swing.SwingUtilities;

import Frame.subwayBase;
import InSubway.SubwayManager;

//---------------------------------------------------------
//[8호선] subwayApp8.java
//---------------------------------------------------------
public class subwayApp8 extends subwayBase {

	public static String[] allStationsInLine8 = { "별내역", "다산역", "동구릉역", "구리역", "장자호수공원역", "암사역사공원역", "암사역", "천호역",
			"강동구청역", "몽촌토성역", "잠실역", "석촌역", "송파역", "가락시장역", "문정역", "장지역", "복정역", "남위례역", "산성역", "남한산성입구역", "단대오거리역",
			"신흥역", "수진역", "모란역" };

	public subwayApp8() {
		super("8호선", allStationsInLine8, new Color(233, 2, 119)); // 8호선 분홍색
		initSubwayGraph();
		add(Share.BottomMenu.addFooterBar(this));
		setVisible(true);
	}

	private void initSubwayGraph() {
		SubwayManager.registerSection("별내역", "다산역", "동구릉역", "구리역", "장자호수공원역", "암사역사공원역", "암사역", "천호역", "강동구청역", "몽촌토성역",
				"잠실역", "석촌역", "송파역", "가락시장역", "문정역", "장지역", "복정역", "남위례역", "산성역", "남한산성입구역", "단대오거리역", "신흥역", "수진역",
				"모란역");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new subwayApp2().setVisible(true));
	}
}
