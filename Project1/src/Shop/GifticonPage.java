package Shop;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

public class GifticonPage extends JFrame {

	// 🎨 둥근 버튼을 만들어주는 헬퍼 메서드 (마우스 호버/클릭 리액션 포함)
	private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
		JButton btn = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				Color paintColor = getBackground();
				
				// 마우스 상태에 따른 색상 변화 (리액션)
				if (getModel().isPressed()) {
					paintColor = paintColor.darker(); 
				} else if (getModel().isRollover()) {
					int red = Math.min(255, paintColor.getRed() + 15);
					int green = Math.min(255, paintColor.getGreen() + 15);
					int blue = Math.min(255, paintColor.getBlue() + 15);
					paintColor = new Color(red, green, blue);
				}
				
				g2.setColor(paintColor);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				
				super.paintComponent(g);
				g2.dispose();
			}
		};
		btn.setBackground(bgColor);
		btn.setForeground(fgColor);
		btn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12)); 
		return btn;
	}

	public GifticonPage(ShopDataManager dataManager) {
		setTitle("기프티콘 보관함");
		setSize(400, 500);
		setResizable(false);
		
		// 💡 [적용] 일기장 배경을 화면 전체에 덮어씌우기!
		setContentPane(new DiaryBackgroundPanel()); 
		setLayout(new BorderLayout());

		// 💡 상단 제목 투명화
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		JLabel title = new JLabel("내 기프티콘", SwingConstants.CENTER);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		topPanel.add(title, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		// 💡 내역 리스트 패널 투명화 및 여백 설정
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setOpaque(false); // 배경 투명하게
		listPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		Map<String, Integer> orderHistory = dataManager.getOrderHistory();
		Set<String> completedOrders = dataManager.getCompletedOrders();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String currentTime = LocalDateTime.now().format(formatter); 

		for (Map.Entry<String, Integer> entry : orderHistory.entrySet()) {
			String itemName = entry.getKey();
			int qty = entry.getValue();

			// 💡 [적용] 개별 기프티콘을 하얀색 둥근 카드로 만들기
			JPanel rowPanel = new JPanel(new BorderLayout(10, 10)) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(Color.WHITE);
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // 둥근 모서리
					g2.dispose();
				}
			};
			rowPanel.setOpaque(false);
			rowPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // 카드 내부 여백
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
			rowPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬 고정

			// [중앙] 정보 영역
			JPanel infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			infoPanel.setOpaque(false);

			JLabel dateLabel = new JLabel("구매 일시: " + currentTime);
			dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
			dateLabel.setForeground(Color.GRAY);

			JLabel nameLabel = new JLabel(itemName + " : " + qty + "개");
			nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));

			infoPanel.add(dateLabel);
			infoPanel.add(Box.createVerticalStrut(5));
			infoPanel.add(nameLabel);
			rowPanel.add(infoPanel, BorderLayout.CENTER);

			// [우측] 버튼 영역
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
			btnPanel.setOpaque(false);

			// 이미 사용(구매 완료)된 기프티콘인지 확인
			if (completedOrders.contains(itemName)) {
				// 💡 [적용] '기프티콘 출력' 버튼 둥글고 예쁘게!
				JButton showGifticonBtn = createRoundedButton("기프티콘 출력", new Color(255, 102, 102), Color.WHITE);
				showGifticonBtn.addActionListener(e -> {
					showQRCodePopup(itemName);
				});
				btnPanel.add(showGifticonBtn);
			} else {
				// 💡 [적용] '구매 완료' 버튼 둥글고 예쁘게!
				JButton useBtn = createRoundedButton("구매 완료", new Color(110, 160, 220), Color.WHITE);
				useBtn.addActionListener(e -> {
					int result = JOptionPane.showConfirmDialog(this, "[" + itemName + "] 기프티콘을 사용하시겠습니까?", "사용 확인", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						completedOrders.add(itemName);
						JOptionPane.showMessageDialog(this, "기프티콘 사용이 완료되었습니다!");
						
						// 창 새로고침
						dispose(); 
						new GifticonPage(dataManager);
					}
				});
				btnPanel.add(useBtn);
			}
			rowPanel.add(btnPanel, BorderLayout.EAST);
			
			listPanel.add(rowPanel);
			listPanel.add(Box.createVerticalStrut(15)); // 💡 카드 사이 여백 띄우기
		}

		// 💡 스크롤 패널 투명화
		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

		// 💡 하단 닫기 버튼 영역
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
		bottomPanel.setOpaque(false);
		JButton closeBtn = createRoundedButton("닫기", Color.WHITE, Color.DARK_GRAY);
		closeBtn.setPreferredSize(new Dimension(100, 35));
		closeBtn.addActionListener(e -> dispose());
		bottomPanel.add(closeBtn);
		add(bottomPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	// 기프티콘 바코드/QR 팝업창 띄우는 메서드
	private void showQRCodePopup(String itemName) {
		JDialog popup = new JDialog(this, "교환권", true);
		popup.setSize(300, 350);
		popup.setLayout(new BorderLayout());
		popup.setLocationRelativeTo(this);
		popup.getContentPane().setBackground(Color.WHITE); 

		// 1. 상단: 상품명
		JLabel title = new JLabel("[" + itemName + "] 교환권", SwingConstants.CENTER);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		popup.add(title, BorderLayout.NORTH);

		// 2. 중앙: QR/바코드 이미지
		JLabel imgLabel = new JLabel();
		imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		try {
			java.net.URL imgUrl = getClass().getResource("/Img/barcode.jpg"); 
			if (imgUrl != null) {
				ImageIcon originalIcon = new ImageIcon(imgUrl);
				Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
				imgLabel.setIcon(new ImageIcon(scaledImage));
			} else {
				imgLabel.setText("QR 이미지 없음");
			}
		} catch (Exception ex) {
			imgLabel.setText("이미지 오류");
		}
		popup.add(imgLabel, BorderLayout.CENTER);

		// 3. 하단: 가짜 바코드 번호 + 닫기 버튼
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBackground(Color.WHITE);
		
		long randomBarcode = (long)(Math.random() * 899999999999L) + 100000000000L; 
		JLabel barcodeNum = new JLabel("바코드 번호: " + randomBarcode, SwingConstants.CENTER);
		barcodeNum.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		barcodeNum.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		bottomPanel.add(barcodeNum, BorderLayout.NORTH);

		// 💡 팝업 안의 닫기 버튼도 메인 테마에 맞게 둥글게 적용!
		JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btnWrap.setBackground(Color.WHITE);
		JButton closeBtn = createRoundedButton("닫기", new Color(240, 240, 240), Color.DARK_GRAY);
		closeBtn.setPreferredSize(new Dimension(80, 30));
		closeBtn.addActionListener(e -> popup.dispose());
		btnWrap.add(closeBtn);
		
		bottomPanel.add(btnWrap, BorderLayout.SOUTH);
		popup.add(bottomPanel, BorderLayout.SOUTH);

		popup.setVisible(true); 
	}
}