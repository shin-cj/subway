package Shop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class GifticonPage extends JFrame{

	public GifticonPage(ShopDataManager dataManager) {
		setTitle("기프티콘 보관함");
		setSize(400, 500);
		setLayout(new BorderLayout());
		setResizable(false);

		// 상단 제목
		JLabel title = new JLabel("내 기프티콘", SwingConstants.CENTER);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		add(title, BorderLayout.NORTH);

		// 내역 리스트 패널
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBackground(Color.WHITE);

		Map<String, Integer> orderHistory = dataManager.getOrderHistory();
		Set<String> completedOrders = dataManager.getCompletedOrders();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String currentTime = LocalDateTime.now().format(formatter); 

		for (Map.Entry<String, Integer> entry : orderHistory.entrySet()) {
			String itemName = entry.getKey();
			int qty = entry.getValue();

			JPanel rowPanel = new JPanel(new BorderLayout(10, 10));
			rowPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
					BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

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

			// 💡 이미 사용(구매 완료)된 기프티콘인지 확인
			if (completedOrders.contains(itemName)) {
				// [수정] 글자 대신 '기프티콘 출력' 버튼 생성
				JButton showGifticonBtn = new JButton("기프티콘 출력");
				showGifticonBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
				showGifticonBtn.setBackground(new Color(255, 102, 102)); // 예쁜 포인트 색상 (취향껏 변경 가능!)
				showGifticonBtn.setForeground(Color.WHITE);
				showGifticonBtn.setFocusPainted(false);
				
				// 💡 버튼 클릭 시 팝업 띄우는 이벤트 연결
				showGifticonBtn.addActionListener(e -> {
					showQRCodePopup(itemName);
				});
				
				btnPanel.add(showGifticonBtn);
			} else {
				JButton useBtn = new JButton("구매 완료"); // 기프티콘 사용 버튼
				useBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
				
				useBtn.addActionListener(e -> {
					int result = JOptionPane.showConfirmDialog(this, "[" + itemName + "] 기프티콘을 사용하시겠습니까?", "사용 확인", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						// 💡 상태를 '완료'로 변경!
						completedOrders.add(itemName);
						
						JOptionPane.showMessageDialog(this, "기프티콘 사용이 완료되었습니다!");
						
						// 화면 새로고침
						listPanel.removeAll();
						dispose(); // 창을 닫고 다시 열어서 갱신 (가장 간단한 갱신 방법)
						new GifticonPage(dataManager);
					}
				});
				btnPanel.add(useBtn);
			}
			rowPanel.add(btnPanel, BorderLayout.EAST);
			listPanel.add(rowPanel);
		}

		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);

		JButton closeBtn = new JButton("닫기");
		closeBtn.addActionListener(e -> dispose());
		add(closeBtn, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	
	
	// 💡 [추가] 기프티콘 바코드/QR 팝업창 띄우는 메서드
		private void showQRCodePopup(String itemName) {
			javax.swing.JDialog popup = new javax.swing.JDialog(this, "교환권", true);
			popup.setSize(300, 350);
			popup.setLayout(new java.awt.BorderLayout());
			popup.setLocationRelativeTo(this);
			popup.getContentPane().setBackground(Color.WHITE); // 배경 하얗게 깔끔하게!

			// 1. 상단: 상품명
			JLabel title = new JLabel("[" + itemName + "] 교환권", SwingConstants.CENTER);
			title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
			title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
			popup.add(title, BorderLayout.NORTH);

			// 2. 중앙: QR/바코드 이미지
			JLabel imgLabel = new JLabel();
			imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
			try {
				// 💡 예전에 사용했던 QR코드 이미지 경로! 만약 파일 이름이 다르면 여기를 수정해 줘.
				java.net.URL imgUrl = getClass().getResource("/Img/barcode.jpg"); 
				if (imgUrl != null) {
					javax.swing.ImageIcon originalIcon = new javax.swing.ImageIcon(imgUrl);
					java.awt.Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
					imgLabel.setIcon(new javax.swing.ImageIcon(scaledImage));
				} else {
					imgLabel.setText("QR 이미지 없음 (/Img/QRCode.png 필요)");
				}
			} catch (Exception ex) {
				imgLabel.setText("이미지 오류");
			}
			popup.add(imgLabel, BorderLayout.CENTER);

			// 3. 하단: 가짜 바코드 번호 + 닫기 버튼
			JPanel bottomPanel = new JPanel(new java.awt.BorderLayout());
			bottomPanel.setBackground(Color.WHITE);
			
			// 💡 리얼함을 살리기 위해 12자리의 랜덤 바코드 번호 생성!
			long randomBarcode = (long)(Math.random() * 899999999999L) + 100000000000L; 
			JLabel barcodeNum = new JLabel("바코드 번호: " + randomBarcode, SwingConstants.CENTER);
			barcodeNum.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
			barcodeNum.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
			bottomPanel.add(barcodeNum, BorderLayout.NORTH);

			JButton closeBtn = new JButton("닫기");
			closeBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
			closeBtn.setFocusPainted(false);
			closeBtn.addActionListener(e -> popup.dispose());
			bottomPanel.add(closeBtn, BorderLayout.SOUTH);

			popup.add(bottomPanel, BorderLayout.SOUTH);

			popup.setVisible(true); // 짠! 팝업 띄우기
		}
	
}
