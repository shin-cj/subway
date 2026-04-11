package Shop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;


public class OrderDetail extends JFrame {// 💡 변경점: dataManager와 메인 화면 갱신용 Runnable을 전달받습니다.
	public OrderDetail(ShopDataManager dataManager, Runnable updateMainUI) {
		setTitle("주문 내역 상세");
		setSize(400, 500); // 넉넉하게 사이즈 약간 조정
		setLayout(new BorderLayout());
		setResizable(false);

		// 상단 제목
		JLabel title = new JLabel("📜 나의 주문 내역", SwingConstants.CENTER);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		add(title, BorderLayout.NORTH);

		// 내역 리스트 패널
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBackground(Color.WHITE);

		Map<String, Integer> orderHistory = dataManager.getOrderHistory();
		Map<String, ItemInfo> shopDB = dataManager.getShopDB();

		// 날짜 표시용 포맷 설정 (연/월/일 시간:분)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String currentTime = LocalDateTime.now().format(formatter); // 현재 시간 가져오기

		// 데이터 채우기
		for (Map.Entry<String, Integer> entry : orderHistory.entrySet()) {
			String itemName = entry.getKey();
			int qty = entry.getValue();
			ItemInfo itemInfo = shopDB.get(itemName); // 단가를 알기 위해 정보 가져오기

			JPanel rowPanel = new JPanel(new BorderLayout(10, 10));
			rowPanel.setBorder(
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
							BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

			// [중앙] 정보 영역 (구매 일시 + 상품명/수량)
			JPanel infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			infoPanel.setOpaque(false);

			JLabel dateLabel = new JLabel("구매 일시: " + currentTime);
			dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
			dateLabel.setForeground(Color.GRAY);

			// 💡 상품명 라벨 생성
			JLabel nameLabel = new JLabel(itemName + " : " + qty + "개");
			nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
			nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 올리면 손가락 모양으로 변경

			// 💡 [핵심] 이미지가 글자 오른쪽에 뜨도록 설정 (이걸 안 하면 이미지가 뜰 때마다 글자가 옆으로 밀려납니다!)
			nameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
			nameLabel.setIconTextGap(15); // 글자와 이미지 사이의 간격

			// 💡 조그만 호버용 이미지 세팅 (35x35 사이즈)
			java.net.URL imgUrl = getClass().getResource(itemInfo.getImgUrl());
			ImageIcon hoverIcon = null;
			if (imgUrl != null) {
				ImageIcon originalIcon = new ImageIcon(imgUrl);
				Image scaledImage = originalIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
				hoverIcon = new ImageIcon(scaledImage);
			}

			// 익명 클래스 내부에서 사용하기 위해 final 변수로 복사
			final ImageIcon finalHoverIcon = hoverIcon;

			// 💡 마우스 롤오버 이벤트 달기
			nameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent e) {
					// 마우스가 글자 위로 올라오면 아이콘 세팅
					if (finalHoverIcon != null) {
						nameLabel.setIcon(finalHoverIcon);
					}
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					// 마우스가 글자에서 벗어나면 아이콘 지우기
					nameLabel.setIcon(null);
				}
			});

			infoPanel.add(dateLabel);
			infoPanel.add(Box.createVerticalStrut(5));
			infoPanel.add(nameLabel); // 💡 이벤트가 달린 라벨 추가

			rowPanel.add(infoPanel, BorderLayout.CENTER);

			// [우측] 버튼 영역 (구매 취소)
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
			btnPanel.setOpaque(false);

			JButton cancelBtn = new JButton("구매 취소");
			cancelBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));

			// 구매 취소 및 환불 로직
			cancelBtn.addActionListener(e -> {
				int refundAmount = itemInfo.getPrice() * qty; // 환불될 금액 계산

				// 💡 [추가된 부분] 구매 취소 전 확인 팝업창 띄우기!
				String confirmMsg = String.format("[%s] 상품의 구매를 정말 취소하시겠습니까?\n(환불 예정 금액: %d원)", itemName, refundAmount);
				int result = JOptionPane.showConfirmDialog(this, confirmMsg, "구매 취소 확인", JOptionPane.YES_NO_OPTION);

				// 사용자가 '아니오'를 누르거나 창을 그냥 끄면 여기서 바로 멈춥니다(return).
				if (result != JOptionPane.YES_OPTION) {
					return;
				}

				// --- 💡 여기서부터는 '예'를 눌렀을 때 실행되는 기존 로직입니다 ---
				dataManager.setMyPrice(dataManager.getMyPrice() + refundAmount); // 환불
				orderHistory.remove(itemName); // 데이터 삭제

				// 화면 새로고침
				listPanel.remove(rowPanel);
				listPanel.revalidate();
				listPanel.repaint();

				if (updateMainUI != null) {
					updateMainUI.run();
				}

				JOptionPane.showMessageDialog(this, "[" + itemName + "] 구매가 취소되었습니다.\n환불 금액: " + refundAmount
						+ "원\n현재 잔액: " + dataManager.getMyPrice() + "원", "취소 완료", JOptionPane.INFORMATION_MESSAGE);

				if (orderHistory.isEmpty()) {
					dispose();
				}
			});

			btnPanel.add(cancelBtn);
			rowPanel.add(btnPanel, BorderLayout.EAST);

			listPanel.add(rowPanel);
		}

		// 스크롤 설정
		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

		// 하단 닫기 버튼
		JButton closeBtn = new JButton("닫기");
		closeBtn.addActionListener(e -> dispose());
		add(closeBtn, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}
