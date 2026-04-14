package InSubway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;

import Frame.SubwayTopview;

public class MetroLine {

	private List<String> station;

	public MetroLine(String[] station) {
		this.station = new ArrayList<>(Arrays.asList(station));
	}

	public void trainStart(String startStation, String endStation, JLabel display, SubwayTopview topView) {

		int startIndex = station.indexOf(startStation);
		int endIndex = station.indexOf(endStation);

		// 예외 처리 : 입력한 역이 존재하지 않을 때
		if (startIndex == -1 || endIndex == -1) {
			System.out.println("존재하지 않는 역이 있습니다.");
			return;
		} else if (startIndex == endIndex) {
			System.out.println("출발지와 목적지가 같습니다.");
			return;
		}
		int nextWay = (startIndex < endIndex) ? 1 : -1;
		int beforeWay = -nextWay;
		final int[] thisStation = { startIndex };

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable runnable = () -> {
			try {
				int curr = thisStation[0];
				int nextIdx = curr + nextWay;
				int prevIdx = curr + beforeWay;
				String currentStationName = station.get(curr);

				boolean hasNext = (nextIdx >= 0 && nextIdx < station.size());
				boolean hasPrev = (prevIdx >= 0 && prevIdx < station.size());

				String message = "";
				if (hasPrev && hasNext) {
					// 중간 역
					message = station.get(prevIdx) + " >> " + station.get(curr) + " >> " + station.get(nextIdx);
				} else if (hasNext) {
					// 기점 역 (뒤에 역이 없음)
					message = "   >> " + station.get(curr) + " >> " + station.get(nextIdx);

				} else if (hasPrev) {
					// 종점 역 (앞에 역이 없음)
					message = station.get(prevIdx) + " >> " + station.get(curr) + " >>   ";
				}

				final String finalMsg = message;

				// [핵심] 2. SubwayTopview에 현재 역이 바뀌었음을 알림
				// UI 쓰레드에서 안전하게 업데이트하기 위해 invokeLater 사용
				// [수정] UI 업데이트 시 null 체크 추가 (안전장치)
				javax.swing.SwingUtilities.invokeLater(() -> {
					if (display != null)
						display.setText(finalMsg);
					if (topView != null)
						topView.setCurrentStation(currentStationName);
				});

				if (!hasNext) {
					System.out.println("운행 종료 (종점 도착)");
					scheduler.shutdown();
				} else {
					thisStation[0] += nextWay; // 다음 역으로 이동
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		};

		scheduler.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);

	}

	/*
	 * public static void init() {
	 * 
	 * //1호선 인천행 String[] line1 = {"연천", "전곡", "청산", "소요산", "동두천", "보산", "동두천중앙",
	 * "지행", "덕정", "덕계", "양주", "녹양", "가능", "의정부", "회룡", "망월사", "도봉산", "도봉", "방학",
	 * "창동", "녹천", "월계", "광운대", "석계", "신이문", "외대앞", "회기", "청량리", "제기동", "신설동",
	 * "동묘앞", "동대문", "종로5가", "종로3가", "종각", "시청", "서울역", "남영", "용산", "노량진", "대방",
	 * "신길", "영등포", "신도림", "구로", "구일", "개봉", "오류동", "온수", "역곡", "소사", "부천", "중동",
	 * "송내", "부개", "부평", "백운", "동암", "간석", "주안", "도화", "제물포", "도원", "동인천", "인천"};
	 * //2호선 순환선 String[] line2 = {"시청", "을지로입구", "을지로3가", "을지로4가", "동대문역사문화공원",
	 * "신당", "상왕십리", "왕십리", "한양대", "뚝섬", "성수", "건대입구", "구의", "강변", "잠실나루", "잠실",
	 * "잠실새내", "종합운동장", "삼성", "선릉", "역삼", "강남", "교대", "서초", "방배", "사당", "낙성대",
	 * "서울대입구", "봉천", "신림", "신대방", "구로디지털단지", "대림", "신도림", "문래", "영등포구청", "당산",
	 * "합정", "홍대입구", "신촌", "이대", "아현", "충정로"}; //3호선 String[] line3 = { "대화", "주엽",
	 * "정발산", "마두", "백석", "대곡", "화정", "원당", "원흥", "삼송", "지축", "구파발", "연신내", "불광",
	 * "녹번", "홍제", "무악재", "독립문", "경복궁", "안국", "종로3가", "을지로3가", "충무로", "동대입구", "약수",
	 * "금호", "옥수", "압구정", "신사", "잠원", "고속터미널", "교대", "남부터미널", "양재", "매봉", "도곡",
	 * "대치", "학여울", "대청", "일원", "수서", "가락시장", "경찰병원", "오금"}; // 4호선 (진접 ~ 오이도)
	 * String[] line4 = { "진접", "오남", "별내별가람", "불암산", "상계", "노원", "창동", "쌍문", "수유",
	 * "미아", "미아사거리", "길음", "성신여대입구", "한성대입구", "혜화", "동대문", "동대문역사문화공원", "충무로",
	 * "명동", "회현", "서울역", "숙대입구", "삼각지", "신용산", "이촌", "동작", "이수", "사당", "남태령",
	 * "선바위", "경마공원", "대공원", "과천", "정부과천청사", "인덕원", "평촌", "범계", "금정", "산본", "수리산",
	 * "대야미", "반월", "상록수", "한대앞", "중앙", "고잔", "초지", "안산", "신길온천", "정왕", "오이도"};
	 * 
	 * // 5호선 (방화 ~ 하남검단산) String[] line5 = { "방화", "개화산", "김포공항", "송정", "마곡", "발산",
	 * "우장산", "화곡", "까치산", "신정", "목동", "오목교", "양평", "영등포구청", "영등포시장", "신길", "여의도",
	 * "여의나루", "마포", "공덕", "애오개", "충정로", "서대문", "광화문", "종로3가", "을지로4가", "동대문역사문화공원",
	 * "청구", "신금호", "행당", "왕십리", "마장", "답십리", "장한평", "군자", "아차산", "광나루", "천호", "강동",
	 * "길동", "굽은다리", "명일", "고덕", "상일동", "강일", "미사", "하남풍산", "하남시청", "하남검단산"};
	 * 
	 * // 6호선 (응암 ~ 신내) String[] line6 = { "응암", "역촌", "불광", "독바위", "연신내", "구산",
	 * "새절", "증산", "디지털미디어시티", "월드컵경기장", "마포구청", "망원", "합정", "상수", "광흥창", "대흥",
	 * "공덕", "효창공원앞", "삼각지", "녹사평", "이태원", "한강진", "버티고개", "약수", "청구", "신당", "동묘앞",
	 * "창신", "보문", "안암", "고려대", "월곡", "상월곡", "돌곶이", "석계", "태릉입구", "화랑대", "봉화산", "신내"
	 * };
	 * 
	 * // 7호선 (장암 ~ 석남) String[] line7 = { "장암", "도봉산", "수락산", "마들", "노원", "중계",
	 * "하계", "공릉", "태릉입구", "먹골", "중화", "상봉", "면목", "사가정", "용마산", "중곡", "군자",
	 * "어린이대공원", "건대입구", "자양", "청담", "강남구청", "학동", "논현", "반포", "고속터미널", "내방", "이수",
	 * "남성", "숭실대입구", "상도", "장승배기", "신대방삼거리", "보라매", "신풍", "대림", "남구로", "가산디지털단지",
	 * "철산", "광명사거리", "천왕", "온수", "까치울", "부천종합운동장", "춘의", "신중동", "부천시청", "상동",
	 * "삼산체육관", "굴포천", "부평구청", "산곡", "석남" };
	 * 
	 * // 8호선 (별내 ~ 모란) String[] line8 = { "별내", "다산", "동구릉", "구리", "장자호수공원",
	 * "암사역사공원", "암사", "천호", "강동구청", "몽촌토성", "잠실", "석촌", "송파", "가락시장", "문정", "장지",
	 * "복정", "남위례", "산성", "남한산성입구", "단대오거리", "신흥", "수진", "모란"};
	 * 
	 * // 9호선 (개화 ~ 중앙보훈병원) String[] line9 = { "개화", "김포공항", "공항시장", "신방화", "마곡나루",
	 * "양천향교", "가양", "증미", "등촌", "염창", "신목동", "선유도", "당산", "국회의사당", "여의도", "샛강",
	 * "노량진", "노들", "흑석", "동작", "구반포", "신반포", "고속터미널", "사평", "신논현", "언주", "선정릉",
	 * "삼성중앙", "봉은사", "종합운동장", "삼전", "석촌고분", "석촌", "송파나루", "한성백제", "올림픽공원", "둔촌오륜",
	 * "중앙보훈병원"};
	 * 
	 * }
	 */

}
