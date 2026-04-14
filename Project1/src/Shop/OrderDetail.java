package Shop;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.swing.*;

public class OrderDetail extends JFrame {

	// 🎨 둥근 버튼 헬퍼
	private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
		JButton btn = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				Color paintColor = getBackground();
				
				// 💡 마우스 상태에 따른 색상 변화 (리액션 로직 추가!)
				if (getModel().isPressed()) {
					paintColor = paintColor.darker(); // 클릭 시 살짝 어두워짐 (눌린 느낌)
				} else if (getModel().isRollover()) {
					// 마우스 호버 시 살짝 밝아짐 (떠오른 느낌)
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
		return btn;
	}

	public OrderDetail(ShopDataManager dataManager, Runnable updateMainUI) {
		setTitle("주문 내역 상세");
		setSize(400, 500); 
		setResizable(false);
		
		// 💡 [수정] 배경 덮어씌우기
		setContentPane(new DiaryBackgroundPanel()); 
		setLayout(new BorderLayout());

		// 💡 [수정] 상단 제목 투명화
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		JLabel title = new JLabel("나의 주문 내역", SwingConstants.CENTER);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		topPanel.add(title, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

		// 💡 [수정] 리스트 패널 투명화 및 안쪽 여백 추가
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setOpaque(false);
		listPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		Map<String, Integer> orderHistory = dataManager.getOrderHistory();
		Map<String, ItemInfo> shopDB = dataManager.getShopDB();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String currentTime = LocalDateTime.now().format(formatter); 

		for (Map.Entry<String, Integer> entry : orderHistory.entrySet()) {
			String itemName = entry.getKey();
			int qty = entry.getValue();
			ItemInfo itemInfo = shopDB.get(itemName); 

			// 💡 [수정] 개별 주문내역 하얀색 둥근 카드로 만들기
			JPanel rowPanel = new JPanel(new BorderLayout(10, 10)) {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(Color.WHITE);
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
					g2.dispose();
				}
			};
			rowPanel.setOpaque(false);
			rowPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

			JPanel infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			infoPanel.setOpaque(false);

			JLabel dateLabel = new JLabel("구매 일시: " + currentTime);
			dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
			dateLabel.setForeground(Color.GRAY);

			JLabel nameLabel = new JLabel(itemName + " : " + qty + "개");
			nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
			nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
			nameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
			nameLabel.setIconTextGap(15); 

			java.net.URL imgUrl = getClass().getResource(itemInfo.getImgUrl());
			ImageIcon hoverIcon = null;
			if (imgUrl != null) {
				ImageIcon originalIcon = new ImageIcon(imgUrl);
				Image scaledImage = originalIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
				hoverIcon = new ImageIcon(scaledImage);
			}

			final ImageIcon finalHoverIcon = hoverIcon;
			nameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent e) {
					if (finalHoverIcon != null) nameLabel.setIcon(finalHoverIcon);
				}
				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					nameLabel.setIcon(null);
				}
			});

			infoPanel.add(dateLabel);
			infoPanel.add(Box.createVerticalStrut(5));
			infoPanel.add(nameLabel); 
			rowPanel.add(infoPanel, BorderLayout.CENTER);

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
			btnPanel.setOpaque(false);

			if (dataManager.getCompletedOrders().contains(itemName)) {
				JLabel completedLabel = new JLabel("구매 완료가 된 상품 입니다.");
				completedLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
				completedLabel.setForeground(new Color(0, 102, 204));
				btnPanel.add(completedLabel, BorderLayout.CENTER);
			} else {
				// 💡 [수정] 취소 버튼 둥글게!
				JButton cancelBtn = createRoundedButton("구매 취소", new Color(240, 240, 240), Color.DARK_GRAY);
				cancelBtn.addActionListener(e -> {
					int refundAmount = itemInfo.getPrice() * qty; 
					String confirmMsg = String.format("[%s] 상품의 구매를 정말 취소하시겠습니까?\n(환불 예정 금액: %d P)", itemName, refundAmount);
					int result = JOptionPane.showConfirmDialog(this, confirmMsg, "구매 취소 확인", JOptionPane.YES_NO_OPTION);

					if (result != JOptionPane.YES_OPTION) return;

					dataManager.setMyPrice(dataManager.getMyPrice() + refundAmount); 
					orderHistory.remove(itemName); 
					listPanel.remove(rowPanel);
					listPanel.revalidate();
					listPanel.repaint();

					if (updateMainUI != null) updateMainUI.run();

					JOptionPane.showMessageDialog(this, "[" + itemName + "] 구매가 취소되었습니다.\n환불 금액: " + refundAmount + " P\n현재 잔액: " + dataManager.getMyPrice() + " P", "취소 완료", JOptionPane.INFORMATION_MESSAGE);

					if (orderHistory.isEmpty()) dispose();
				});
				btnPanel.add(cancelBtn);
			}
			rowPanel.add(btnPanel, BorderLayout.EAST);
			listPanel.add(rowPanel);
			listPanel.add(Box.createVerticalStrut(15)); // 💡 [수정] 카드 사이 여백 추가!
		}

		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

		JButton closeBtn = new JButton("닫기");
		closeBtn.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		closeBtn.setBackground(Color.WHITE);
		closeBtn.addActionListener(e -> dispose());
		add(closeBtn, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}