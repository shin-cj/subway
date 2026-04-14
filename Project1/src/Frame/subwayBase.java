package Frame;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Font.loadfont;
import InSubway.SubwayManager;
import Share.ModernScrollBarUI;

/**
 * subwayBase: 지하철 노선별 검색 페이지의 기반(Base)이 되는 화면 패널입니다.
 * 1호선~9호선까지 이 화면 틀을 가져다 쓰게 됩니다.
 * 디자인 테마: 일기장 종이 배경, 모눈 격자, 멈춰있는 기차 낙서
 */
public class subwayBase extends JPanel {
	
	// =========================================================
	// 1. 전역 변수 설정 (화면 전체에서 사용할 데이터들)
	// =========================================================
	protected String[] stationData;       // 해당 호선에 속한 '모든 역 이름'을 담는 배열 (자동완성 등에 쓰임)
	protected Color lineColor;            // 해당 호선의 상징 색상 (예: 1호선은 파란색)
	protected String lineName;            // 해당 호선의 이름 (예: "1호선")

	private Set<String> recentSearches;   // 방금 검색했던 '최근 검색어'를 저장하는 꾸러미 (중복 방지를 위해 Set 사용)
	// 프로그램 전체에서 검색 기록을 공유하기 위한 저장소 (호선별로 기록을 따로 보관함)
	private static Map<String, Set<String>> globalSearchHistory = new HashMap<>(); 
	
	private JPanel listContainer;         // 최근 검색 기록 항목들이 세로로 쌓일 도화지(패널)
	protected JTextField startField;      // 사용자가 출발역을 입력할 네모난 입력창
	protected JTextField endField;        // 사용자가 도착역을 입력할 네모난 입력창

	private ImageIcon backIcon;           // 상단 좌측의 '<'(뒤로가기) 버튼에 들어갈 이미지
	private ImageIcon listIcon;           // 입력창 우측의 '≡'(리스트) 버튼에 들어갈 이미지

	// =========================================================
	// 2. 생성자 (화면이 처음 만들어질 때 실행되는 곳)
	// =========================================================
	public subwayBase(String lineName, String[] stationData, Color lineColor) {
		this.lineName = lineName;       // 넘겨받은 호선 이름 저장
		this.stationData = stationData; // 넘겨받은 역 목록 저장
		this.lineColor = lineColor;     // 넘겨받은 호선 색상 저장

		// [폰트 로드 확인] 나눔스퀘어 폰트가 메모리에 안 올라와 있다면 여기서 불러옵니다.
		if (loadfont.NanumEB == null) {
			loadfont.loadFonts();
		}

		// [이미지 로딩] 버튼에 들어갈 아이콘 이미지를 불러오고, 배경과 어울리게 투명도를 낮춥니다(톤 다운).
		try {
			// 뒤로가기 버튼 이미지 불러오기
			URL imgUrl = getClass().getResource("/reply_18032509.png");
			if (imgUrl != null) {
				Image img = ImageIO.read(imgUrl);
				// makeTransparentIcon 메서드를 통해 크기를 35x35로 맞추고 투명도를 60%(0.6f)로 설정
				backIcon = makeTransparentIcon(img, 35, 35, 0.6f);
			}

			// 리스트 메뉴 버튼 이미지 불러오기
			URL listUrl = getClass().getResource("/list_icon.png");
			if (listUrl != null) {
				Image lImg = ImageIO.read(listUrl);
				// 크기를 18x18로 맞추고 투명도를 60%(0.6f)로 설정
				listIcon = makeTransparentIcon(lImg, 18, 18, 0.6f);
			}
		} catch (IOException e) {
			System.err.println("아이콘 이미지를 불러오지 못했습니다. 경로를 확인하세요.");
		}

		// [최근 검색 기록 준비] 해당 호선 이름으로 된 검색 기록 저장소가 없으면 새로 하나 만들어 줍니다.
		if (!globalSearchHistory.containsKey(lineName)) {
			globalSearchHistory.put(lineName, new LinkedHashSet<>());
		}
		// 현재 화면에서 쓸 최근 검색 기록 꾸러미를 연결해줍니다.
		this.recentSearches = globalSearchHistory.get(lineName);

		// [기본 패널 설정]
		setSize(410, 670); // 창의 가로를 410, 세로를 670으로 고정합니다.
		setLayout(null);   // 레이아웃 자동 정렬을 끄고, x/y 좌표로 내 마음대로 배치(Absolute Layout)하겠다는 뜻입니다.
		setOpaque(false);  // 이 패널의 기본 배경을 투명하게 만듭니다. (아래 paintComponent에서 직접 배경을 그리기 위함)
		
		initUI();          // 이제 화면에 버튼과 글씨들을 올려놓는 메서드를 실행합니다!
	}

	/**
	 * [도구 메서드] 이미지에 투명도(Alpha)를 입혀서 아이콘으로 만들어주는 헬퍼 메서드입니다.
	 */
	private ImageIcon makeTransparentIcon(Image img, int width, int height, float alpha) {
		// 지정된 크기의 텅 빈 도화지(BufferedImage)를 만듭니다.
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bimg.createGraphics(); // 도화지에 그림을 그릴 붓(Graphics2D)을 준비합니다.
		// 붓에 투명도(alpha) 잉크를 묻힙니다.
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)); 
		// 그림이 깨지지 않고 부드럽게 줄어들도록 안티앨리어싱 효과를 켭니다.
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// 도화지에 원본 이미지를 지정된 크기로 그립니다.
		g2.drawImage(img, 0, 0, width, height, null);
		g2.dispose(); // 붓을 다 썼으니 정리합니다.
		return new ImageIcon(bimg); // 완성된 이미지를 아이콘으로 포장해서 반환합니다.
	}

	/**
	 * [UI 조립] 화면에 보이는 모든 버튼, 입력창, 글씨들을 만들고 배치하는 핵심 부분입니다.
	 */
	private void initUI() {
		
		// ==========================================
		// [영역 1] 상단 헤더 (뒤로가기 버튼 + 호선 제목)
		// ==========================================
		JPanel header = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				// 헤더 배경을 살짝 투명한 흰색(알파값 230)으로 칠해서 뒤쪽 모눈종이가 살짝 비치게 합니다.
				g.setColor(new Color(255, 255, 255, 230)); 
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		header.setOpaque(false); // 위에서 직접 칠할 거라서 기본 배경은 투명하게 둡니다.
		header.setBounds(0, 0, 410, 75); // 위치: x=0, y=0 / 크기: 너비 410, 높이 75
		// 헤더 맨 밑에만 연한 회색(230,230,230)으로 1px짜리 선을 긋습니다.
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
		add(header); // 화면에 헤더 추가!

		// [뒤로가기 버튼 만들기]
		JButton backBtn = new JButton();
		if (backIcon != null) backBtn.setIcon(backIcon); // 이미지가 잘 불러와졌으면 이미지 적용
		else {
			backBtn.setText("〈"); // 이미지가 없으면 텍스트로 꺽쇠 표시
			backBtn.setFont(new Font(Font.DIALOG, Font.BOLD, 22)); // 폰트 깨짐 방지를 위해 시스템 폰트(DIALOG) 사용
		}
		// 위치 조절: x=15, y=15, 너비=45, 높이=45
		backBtn.setBounds(15, 15, 45, 45);
		backBtn.setContentAreaFilled(false); // 버튼 배경색 안 보이게 (투명)
		backBtn.setBorderPainted(false);     // 버튼 테두리 선 안 보이게
		backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 올리면 손가락 모양으로 변경
		// 버튼을 클릭했을 때의 행동: 현재 창을 끄고 LineSelect(메인 화면)을 불러옵니다.
		backBtn.addActionListener(e -> FrameBase.getInstance(new LineSelect()));
		header.add(backBtn); // 헤더 위에 버튼 추가!

		// [호선 이름 글씨 만들기]
		JLabel titleLabel = new JLabel(lineName);
		titleLabel.setFont(loadfont.NanumEB.deriveFont(32f)); // 나눔스퀘어 ExtraBold, 크기 32
		titleLabel.setForeground(lineColor); // 글자색을 해당 호선의 색상으로 칠함
		// 위치 조절: x=58, y=18, 너비=200, 높이=45
		titleLabel.setBounds(58, 18, 200, 45);
		header.add(titleLabel); // 헤더 위에 글씨 추가!


		// ==========================================
		// [영역 2] 검색 입력 카드 (가운데 하얀 박스)
		// ==========================================
		JPanel inputCard = new JPanel(null);
		// 위치 조절: x=20, y=95, 너비=370, 높이=180
		inputCard.setBounds(20, 95, 370, 180);
		inputCard.setBackground(Color.WHITE); // 배경은 완전한 흰색
		inputCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1)); // 연한 회색 테두리
		add(inputCard); // 화면에 카드 추가!

		// [출발역 입력 파트 만들기]
		// createInputGroup 이라는 밑에 만들어둔 도구를 써서 '라벨 + 텍스트창 + 리스트버튼' 3세트를 한 번에 만듭니다.
		// 20은 inputCard 내부에서의 위에서부터 떨어진 거리(y축)입니다.
		startField = createInputGroup(inputCard, "📍 타는 곳 (출발역)", 20);

		// [출발역/도착역 위치 바꾸는 버튼 (⇅)]
		JButton swapBtn = new JButton("⇅");
		// 위치 조절: x=315, y=75, 너비=40, 높이=40
		swapBtn.setBounds(315, 75, 50, 50);
		swapBtn.setFont(new Font(Font.DIALOG, Font.BOLD, 24)); // 특수기호 깨짐 방지 폰트
		swapBtn.setForeground(new Color(100, 100, 100)); // 연한 회색
		swapBtn.setContentAreaFilled(false);
		swapBtn.setBorder(null);
		swapBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// 클릭 시 행동: startField(출발역) 글자와 endField(도착역) 글자를 서로 교환합니다.
		swapBtn.addActionListener(e -> {
			String temp = startField.getText(); // 출발역 글자를 임시 공간(temp)에 저장
			startField.setText(endField.getText()); // 도착역 글자를 출발역 창에 넣음
			endField.setText(temp); // 임시 공간에 있던 원래 출발역 글자를 도착역 창에 넣음
		});
		inputCard.add(swapBtn); // 입력 카드에 스왑 버튼 추가!

		// [도착역 입력 파트 만들기]
		// 95는 inputCard 내부에서의 위에서부터 떨어진 거리(y축)입니다.
		endField = createInputGroup(inputCard, "🏁 내리는 곳 (도착역)", 95);


		// ==========================================
		// [영역 3] 경로 검색 실행 버튼
		// ==========================================
		JButton searchBtn = new JButton("경로 검색");
		searchBtn.setFont(loadfont.NanumEB.deriveFont(18f)); // 나눔스퀘어 ExtraBold, 크기 18
		// 위치 조절: x=20, y=290, 너비=370, 높이=55
		searchBtn.setBounds(20, 290, 370, 55);
		searchBtn.setBackground(lineColor); // 버튼 배경색을 호선 색상으로 칠함
		searchBtn.setForeground(Color.WHITE); // 글자색은 흰색
		searchBtn.setBorder(null); // 테두리 없음
		searchBtn.setFocusPainted(false); // 클릭했을 때 생기는 점선 테두리 제거
		searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// 클릭 시 행동: 아래 만들어둔 handleSearch() 메서드를 실행합니다. (실제 검색 기능)
		searchBtn.addActionListener(e -> handleSearch());
		add(searchBtn); // 화면에 검색 버튼 추가!


		// ==========================================
		// [영역 4] 최근 검색 기록 리스트
		// ==========================================
		// '최근 검색 기록' 제목 글씨
		JLabel listHeader = new JLabel("최근 검색 기록");
		listHeader.setFont(loadfont.NanumEB.deriveFont(15f));
		// 위치 조절: x=25, y=365, 너비=200, 높이=25
		listHeader.setBounds(25, 365, 200, 25);
		add(listHeader); // 화면에 추가!

		// 최근 검색한 역 이름들이 세로로 차곡차곡 쌓일 도화지(listContainer)
		listContainer = new JPanel();
		listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS)); // 위에서 아래로(Y_AXIS) 쌓이게 설정
		listContainer.setOpaque(false); // 배경을 투명하게 해서 뒤에 모눈종이가 보이게 함

		// 검색 기록이 많아지면 스크롤 할 수 있게 스크롤바(JScrollPane)를 달아줍니다.
		JScrollPane scrollPane = new JScrollPane(listContainer);
		// 스크롤 패널 위치 조절: x=20, y=395, 너비=370, 높이=190
		scrollPane.setBounds(20, 395, 370, 190);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230))); // 연한 회색 테두리
		scrollPane.setOpaque(false); 
		scrollPane.getViewport().setOpaque(false); 
		// 기본 못생긴 스크롤바 대신 직접 만든 예쁜 스크롤바(ModernScrollBarUI)를 끼워 넣습니다.
		scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); // 스크롤바 두께를 8px로 설정
		add(scrollPane); // 화면에 스크롤 패널 추가!

		// 화면이 처음 켜질 때 저장되어 있던 최근 검색 기록들을 화면에 그려주는 메서드 실행
		updateListDisplay(); 


		// ==========================================
		// [영역 5] 하단 공통 메뉴 바
		// ==========================================
		// BottomMenu 클래스에서 만들어주는 하단바를 가져옵니다.
		JPanel footer = Share.BottomMenu.addFooterBar(this);
		// 하단바 위치 조절: 제일 밑바닥에 딱 붙게 (y=600)
		footer.setBounds(0, 600, 410, 70);
		add(footer); // 화면에 하단바 추가!

		repaint(); // 컴포넌트들을 다 배치했으니 화면을 새로고침해서 짠! 하고 보여줍니다.
	}

	/**
	 * [도구 메서드] '라벨 + 텍스트 입력창 + 우측 리스트(≡) 버튼'을 묶음으로 생성해서 돌려주는 메서드입니다.
	 * 출발역, 도착역 입력칸을 만들 때 재사용됩니다.
	 */
	private JTextField createInputGroup(JPanel container, String labelText, int yPos) {
		// 1. 기호(📍,🏁)가 포함된 라벨 생성
		JLabel label = new JLabel(labelText);
		label.setFont(new Font(Font.DIALOG, Font.PLAIN, 13)); // 특수문자 깨짐 방지를 위한 DIALOG 폰트
		label.setForeground(new Color(50, 50, 50));
		// label 위치: x=15, 전달받은 yPos, 너비=200, 높이=20
		label.setBounds(15, yPos, 200, 20);
		container.add(label);

		// 2. 글자를 입력할 수 있는 텍스트 필드 생성
		JTextField textField = new JTextField();
		textField.setFont(loadfont.NanumB.deriveFont(16f)); // 글씨체 지정
		// 텍스트필드 위치 조절: x=15, y는 라벨 밑으로 22px 떨어진 곳, 너비=280, 높이=42
		textField.setBounds(15, yPos + 22, 280, 42); 
		// 테두리 설정 (바깥쪽은 회색 1px 실선, 안쪽은 왼쪽/오른쪽에 10px씩 여백을 주어 글씨가 벽에 안 붙게 함)
		textField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
				BorderFactory.createEmptyBorder(0, 10, 0, 10)));
		container.add(textField);

		// 3. 입력창 맨 오른쪽에 붙는 전체 역 리스트(≡) 팝업 버튼 생성
		JButton listBtn = new JButton();
		if (listIcon != null) {
			listBtn.setIcon(listIcon); // 이미지가 있으면 이미지 적용
		} else {
			listBtn.setText("≡"); // 없으면 텍스트로 대체
			listBtn.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
			listBtn.setForeground(new Color(150, 150, 150));
		}
		
		// 리스트 버튼 위치 조절: 수정 요청하신 대로 x를 298로 약간 당겨서 배치하고, 크기는 가로/세로 34로 앙증맞게 조절
		listBtn.setBounds(298, yPos + 26, 34, 34); 
		listBtn.setContentAreaFilled(false);
		listBtn.setBorder(null);
		listBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		// 클릭 시 행동: 해당 호선의 모든 역을 띄워주는 showStationListModal() 모달 팝업을 엽니다.
		listBtn.addActionListener(e -> showStationListModal(textField)); 
		container.add(listBtn);

		// 4. 사용자가 글자를 칠 때 실시간으로 아래에 역을 추천해주는 '자동완성' 기능을 연결해줍니다.
		setupAutoComplete(textField); 
		
		// 다 만든 텍스트 필드를 반환하여 startField, endField 변수에 쏙 들어가게 합니다.
		return textField;
	}

	/**
	 * [로직] 입력창 옆의 리스트(≡) 버튼을 눌렀을 때, 이 호선의 모든 역 목록을 팝업창(모달)으로 띄워줍니다.
	 */
	private void showStationListModal(JTextField targetField) {
		// 팝업창을 현재 창(parentWindow) 위에 띄우기 위해 현재 창 정보를 가져옵니다.
		Window parentWindow = SwingUtilities.getWindowAncestor(this);
		// 새로운 빈 팝업창(JDialog)을 생성합니다. 모달 속성이라 이 창이 켜져있으면 뒷배경 창을 클릭할 수 없습니다.
		JDialog dialog = new JDialog(parentWindow, "", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setSize(320, 480); // 팝업창 크기
		dialog.setUndecorated(true); // 기본적으로 생기는 윈도우 테두리(X 버튼 등)를 없애버립니다.
		dialog.setBackground(new Color(0, 0, 0, 0)); // 투명하게 처리
		dialog.setLocationRelativeTo(parentWindow); // 팝업창을 부모 창의 정중앙에 띄웁니다.

		// 팝업 안쪽을 채울 메인 패널입니다.
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true)); // 둥근 테두리

		// 팝업 맨 위의 타이틀 라벨
		JLabel titleLabel = new JLabel(lineName + " 전체 역", SwingConstants.CENTER);
		titleLabel.setFont(loadfont.NanumEB.deriveFont(18f));
		titleLabel.setPreferredSize(new Dimension(320, 50)); // 높이 50
		titleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))); // 아랫줄 그어줌
		mainPanel.add(titleLabel, BorderLayout.NORTH); // 북쪽에 부착

		// 역 이름 버튼들이 세로로 주르륵 달릴 리스트 패널
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS)); // 세로 정렬
		listPanel.setBackground(Color.WHITE);

		// 호선의 역 개수만큼 반복문(for)을 돌려서 각각 버튼으로 만듭니다.
		for (String station : stationData) {
			JButton itemBtn = new JButton("  " + station); // 버튼 이름 = "  강남역"
			itemBtn.setMaximumSize(new Dimension(320, 50));
			itemBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
			itemBtn.setHorizontalAlignment(SwingConstants.LEFT); // 글씨를 왼쪽 정렬
			itemBtn.setFont(loadfont.NanumB.deriveFont(16f));
			itemBtn.setBackground(Color.WHITE);
			itemBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 245, 245))); // 버튼끼리 얇은 선으로 구분
			itemBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

			// 특정 역 버튼을 클릭했을 때의 행동!
			itemBtn.addActionListener(e -> {
				targetField.setText(station); // 이전에 선택했던 입력창(targetField)에 역 이름을 쏙 넣어줍니다.
				addRecentSearch(station);     // 검색 기록에도 추가해줍니다.
				dialog.dispose();             // 일을 다 했으니 팝업창을 끕니다.
			});
			listPanel.add(itemBtn); // 만들어진 버튼을 리스트 패널에 붙입니다.
		}

		// 역 개수가 많으면 스크롤해야 하니 스크롤 패널에 담습니다.
		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI()); // 예쁜 스크롤바로 교체
		mainPanel.add(scrollPane, BorderLayout.CENTER); // 중앙에 부착

		// 팝업 맨 밑의 "창 닫기" 버튼
		JButton closeBtn = new JButton("창 닫기");
		closeBtn.setFont(loadfont.NanumB.deriveFont(14f));
		closeBtn.setPreferredSize(new Dimension(320, 45)); // 높이 45
		closeBtn.setBackground(new Color(250, 250, 250)); // 살짝 회색
		closeBtn.addActionListener(e -> dialog.dispose()); // 클릭 시 팝업 끄기
		mainPanel.add(closeBtn, BorderLayout.SOUTH); // 남쪽에 부착

		// 완성된 내용물들을 팝업창에 넣고 화면에 띄웁니다.
		dialog.add(mainPanel);
		dialog.setVisible(true);
	}

	/**
	 * [로직] 사용자가 입력창에 글씨를 한 글자 칠 때마다 밑에 추천 역을 띄워주는 '자동완성' 기능입니다.
	 */
	private void setupAutoComplete(JTextField textField) {
		// 추천 단어들이 뜰 작은 드롭다운 메뉴(JPopupMenu)를 생성합니다.
		JPopupMenu menu = new JPopupMenu();
		menu.setFocusable(false); // 이 메뉴가 떠도 커서(포커스)는 원래 입력창에 남아있게 합니다.
		menu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

		// 실시간으로 역을 찾아서 메뉴를 보여주는 핵심 작업(Task)입니다.
		Runnable showMenuTask = () -> {
			String input = textField.getText().trim(); // 사용자가 현재 입력한 글자
			if (input.isEmpty()) { // 아무것도 안 쳤으면 메뉴 숨기기
				menu.setVisible(false);
				return;
			}
			menu.removeAll(); // 기존에 떠있던 추천 목록 싹 지우기
			int count = 0; // 몇 개 찾았는지 세는 카운터
			
			// 역 목록(stationData) 전체를 뒤져봅니다.
			for (String station : stationData) {
				// 만약 역 이름 안에 사용자가 친 글자가 들어있고, 완전 똑같지는 않다면?
				if (station.contains(input) && !station.equals(input)) {
					addMenuItem(textField, menu, station); // 추천 메뉴에 역 이름을 하나 추가!
					count++;
				}
				if (count >= 8) break; // 최대 8개까지만 보여줍니다. (화면 밖으로 넘어가지 않게)
			}
			
			// 찾은 결과가 1개 이상 있다면?
			if (count > 0) {
				if (!menu.isVisible()) {
					// 메뉴가 안 떠있으면, 텍스트 필드 바로 아래(높이 위치)에 메뉴를 짠 하고 띄웁니다.
					menu.show(textField, 0, textField.getHeight());
				} else {
					// 이미 떠 있으면 갱신만 합니다.
					menu.pack();
				}
				textField.requestFocusInWindow(); // 입력 계속 할 수 있게 커서 강제 유지
			} else {
				menu.setVisible(false); // 못 찾았으면 메뉴 숨기기
			}
		};

		// 텍스트 필드에 글씨가 더해지거나, 지워지거나, 바뀔 때마다 위에서 만든 showMenuTask를 실행합니다.
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { SwingUtilities.invokeLater(showMenuTask); }
			public void removeUpdate(DocumentEvent e) { SwingUtilities.invokeLater(showMenuTask); }
			public void changedUpdate(DocumentEvent e) { SwingUtilities.invokeLater(showMenuTask); }
		});
		// 사용자가 마우스로 입력창을 다시 콕 찍었을 때도 메뉴를 띄울지 검사합니다.
		textField.addCaretListener(e -> SwingUtilities.invokeLater(showMenuTask));
	}

	/**
	 * [도구 메서드] 자동완성 드롭다운 메뉴(JPopupMenu)에 들어갈 한 줄짜리 항목을 만듭니다.
	 */
	private void addMenuItem(JTextField textField, JPopupMenu menu, String station) {
		JMenuItem item = new JMenuItem(station); // 한 줄짜리 메뉴(JMenuItem) 생성
		item.setFont(loadfont.NanumB.deriveFont(14f));
		item.setBackground(Color.WHITE);
		item.setPreferredSize(new Dimension(280, 35)); // 가로 280, 세로 35 크기 지정
		
		// 메뉴 항목을 사용자가 마우스로 콕 클릭했을 때!
		item.addActionListener(e -> {
			textField.setText(station); // 텍스트창에 해당 역 이름 쏙 채우기
			addRecentSearch(station);   // 검색 기록에 추가!
			menu.setVisible(false);     // 메뉴 팝업 닫기
		});
		menu.add(item); // 팝업 메뉴에 항목 부착!
	}

	/**
	 * [핵심 로직] 맨 아래 '경로 검색' 파란색 버튼을 눌렀을 때 실행되는 메서드입니다.
	 */
	private void handleSearch() {
		// 사용자가 입력창에 적은 글자를 가져옵니다.
		String s = startField.getText().trim(); // 출발역
		String d = endField.getText().trim();   // 도착역
		
		// 방어 로직 1: 출발/도착역 이름이 똑같으면 막기
		if (s.equals(d)) {
			JOptionPane.showMessageDialog(this, "출발역과 도착역이 똑같아요!");
			return;
		}
		// 방어 로직 2: 빈칸이면 막기
		if (s.isEmpty() || d.isEmpty()) {
			JOptionPane.showMessageDialog(this, "역 이름을 모두 입력해주세요.");
			return;
		}
		// 방어 로직 3: 실제 디비(SubwayManager)에 없는 이상한 역 이름 치면 막기
		if (!SubwayManager.exists(s) || !SubwayManager.exists(d)) {
			JOptionPane.showMessageDialog(this, "이름을 정확히 입력했는지 확인해주세요.");
			return;
		}
		
		// DB 관리자에게 "출발역에서 도착역까지 가는 길 찾아줘!" 라고 요청합니다.
		List<String> route = SubwayManager.findRoute(s, d);
		
		// 결과가 무사히 도착했다면?
		if (route != null) {
			// 현재 창을 덮어쓰면서(싱글톤), 결과 화면을 보여주는 'SubwayTopview' 화면으로 이동합니다!
			FrameBase.getInstance(new SubwayTopview(lineColor, s, d, route));
		} else {
			// 길을 못 찾았다면 경고창 띄우기
			JOptionPane.showMessageDialog(this, "아직 갈 수 있는 경로를 찾지 못했어요.");
		}
		
		// 검색을 시도한 역들을 최근 검색 기록 리스트에 넣어줍니다.
		addRecentSearch(s); 
		addRecentSearch(d);
	}

	/**
	 * [로직] 검색 기록 저장소에 역 이름을 하나 추가하고, 리스트 화면을 새로고침합니다.
	 */
	private void addRecentSearch(String station) {
		if (station == null || station.trim().isEmpty()) return; // 텅 빈 글자면 취소
		String s = station.trim();
		recentSearches.remove(s); // 예전에 검색했던 역이면 그 기록을 일단 지워버립니다 (중복 방지)
		recentSearches.add(s);    // 그리고 맨 뒤(최신 자리)에 다시 새롭게 추가합니다!
		updateListDisplay();      // 화면 새로고침!
	}

	/**
	 * [로직] 최근 검색 기록 패널에 쌓인 항목들을 전부 지우고, 최신 데이터로 다시 주르륵 그립니다.
	 */
	private void updateListDisplay() {
		listContainer.removeAll(); // 도화지 비우기
		List<String> list = new ArrayList<>(recentSearches);
		
		// 최신순(가장 최근에 검색한 게 맨 위로 오게)으로 보여주기 위해 for문을 거꾸로(-1) 돕니다.
		for (int i = list.size() - 1; i >= 0; i--) {
			// createListRow를 써서 한 줄씩 만들어서 추가합니다.
			listContainer.add(createListRow(list.get(i)));
		}
		listContainer.revalidate(); // 레이아웃 재계산
		listContainer.repaint();    // 화면에 새로 그리기
	}

	/**
	 * [도구 메서드] '최근 검색 기록' 영역에 들어갈 한 줄짜리 항목(라벨 + X버튼) 패널을 만들어 반환합니다.
	 */
	private JPanel createListRow(String name) {
		// 한 줄의 베이스 패널 생성
		JPanel row = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(255, 255, 255, 230)); // 반투명 흰색 바탕
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(370, 45)); // 최대 높이 45 고정
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 245, 245))); // 줄 밑에 연한 선 긋기

		// [시계(🕒) 기호 라벨]
		JLabel nameLabel = new JLabel("  🕒  " + name);
		nameLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14)); // 기호 깨짐 방지를 위해 시스템 폰트 사용
		nameLabel.setForeground(new Color(100, 100, 100)); // 글자색 연한 회갈색
		nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		// 라벨을 클릭했을 때의 행동!
		nameLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// 현재 마우스 커서가 껌뻑거리고 있는 입력창에 해당 역 이름을 바로 쏙 넣어줍니다.
				if (startField.isFocusOwner()) startField.setText(name);
				else if (endField.isFocusOwner()) endField.setText(name);
				else startField.setText(name); // 포커스가 없으면 일단 출발역에 넣어줌
			}
		});

		// [기록 지우기(✕) 버튼]
		JButton deleteBtn = new JButton("✕ ");
		deleteBtn.setFont(new Font(Font.DIALOG, Font.BOLD, 14)); // 기호 깨짐 방지
		deleteBtn.setForeground(new Color(200, 200, 200)); // 연한 회색으로 톤 다운
		deleteBtn.setBorder(null);
		deleteBtn.setContentAreaFilled(false);
		deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		// ✕ 버튼 클릭 시 행동!
		deleteBtn.addActionListener(e -> {
			recentSearches.remove(name); // 저장소에서 해당 역 기록을 파기
			updateListDisplay();         // 리스트 화면 새로고침
		});

		row.add(nameLabel, BorderLayout.CENTER); // 라벨은 왼쪽에 꽉 차게
		row.add(deleteBtn, BorderLayout.EAST);   // 버튼은 맨 우측에
		return row;
	}

	/**
	 * [배경 그리기] JPanel 위에 직접 물감을 칠해서 모눈종이와 낙서 기차를 그리는 미술 시간입니다!
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		// 그림이 계단 현상 없이 부드럽게 보이게 해주는 마법의 설정(안티앨리어싱)
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getWidth(), h = getHeight();
		
		// 1. 바탕색 칠하기 (일기장 연노랑 속지 색상, #FFFDF0)
		g2.setColor(new Color(255, 253, 240));
		g2.fillRect(0, 0, w, h);
		
		// 2. 모눈종이 격자 선 긋기
		g2.setColor(new Color(235, 230, 210)); // 연한 격자 색상
		// 가로로 30px 이동하면서 세로선(|)을 쫙 긋습니다.
		for (int i = 0; i < w; i += 30) g2.drawLine(i, 0, i, h);
		// 세로로 30px 이동하면서 가로선(-)을 쫙 긋습니다.
		for (int j = 0; j < h; j += 30) g2.drawLine(0, j, w, j);
		
		// 3. 기차 낙서 장식하기
		// 기차가 너무 튀면 앞의 글씨가 안 보이니까 투명도를 35%(0.35f)로 확 낮춥니다.
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
		
		int trainX = 80;   // 기차가 그려질 가로 위치 (왼쪽에서 80px)
		int trainY = 480;  // 기차가 그려질 세로 위치 (위에서 480px, 화면 좀 아래쪽)
		
		// (1) 뒤에 달린 네모난 객차 2칸 그리기
		for (int i = 0; i < 2; i++) {
			int carX = trainX + (i * 60); // 60px 간격으로 칸을 붙임
			g2.setColor(lineColor); // 호선별 색상으로 몸통 칠하기
			g2.fillRoundRect(carX, trainY, 52, 35, 10, 10); // 둥근 네모(몸통)
			g2.setColor(new Color(80, 80, 80)); // 짙은 회색으로 바퀴 칠하기
			g2.fillOval(carX + 8, trainY + 30, 12, 12); // 앞바퀴
			g2.fillOval(carX + 32, trainY + 30, 12, 12); // 뒷바퀴
			g2.setColor(Color.GRAY); // 객차끼리 잇는 연결고리
			g2.fillRect(carX + 52, trainY + 20, 8, 4); // 연결고리 네모
		}
		
		// (2) 맨 앞의 기관차 머리 부분 그리기
		int engineX = trainX + 120; // 객차 2칸 뒤에 머리 부착
		g2.setColor(lineColor);
		g2.fillRoundRect(engineX, trainY, 60, 35, 10, 10); // 몸통
		g2.setColor(lineColor.darker()); // 조종석(유리창 있는 쪽)은 살짝 더 진한 색으로 입체감 주기
		g2.fillRoundRect(engineX + 35, trainY - 15, 25, 20, 5, 5); // 조종석 네모
		g2.setColor(Color.WHITE); // 흰색으로 창문 칠하기
		g2.fillRoundRect(engineX + 42, trainY - 10, 12, 10, 3, 3); // 창문 네모
		g2.setColor(new Color(80, 80, 80)); // 기관차 바퀴
		g2.fillOval(engineX + 10, trainY + 30, 14, 14);
		g2.fillOval(engineX + 40, trainY + 30, 14, 14);
		
		// (3) 굴뚝에서 뿜어져 나오는 몽글몽글 연기 그리기 (흰색 동그라미 두 개 겹치기)
		g2.setColor(Color.WHITE); 
		g2.fillOval(engineX + 25, trainY - 35, 20, 15);
		g2.fillOval(engineX + 40, trainY - 45, 25, 20);
		
		g2.dispose(); // 그림 다 그렸으니 붓 정리!
	}
}