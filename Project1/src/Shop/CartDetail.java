package Shop;

import javax.swing.*;
import Share.UserInfo;
import java.awt.*;
import java.net.URL;
import java.util.Map;

public class CartDetail extends JFrame {

	// 🎨 둥근 버튼을 만들어주는 헬퍼 메서드
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
		btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		return btn;
	}

	public CartDetail(ShopDataManager dataManager, Runnable updateMainUI) {
		setTitle("장바구니 상세");
		setSize(400, 500);
		setResizable(false);
		
		// 💡 [수정] 배경 덮어씌우기
		setContentPane(new DiaryBackgroundPanel()); 
		setLayout(new BorderLayout());

		// 💡 [수정] 상단 패널 투명화
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false); // 배경 투명하게!
		topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

		JLabel title = new JLabel("나의 장바구니", SwingConstants.LEFT);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 22));

		// 💡 [수정] 전체 구매 버튼 둥글게!
		JButton buyAllBtn = createRoundedButton("전체 구매", new Color(110, 160, 220), Color.WHITE);
		buyAllBtn.setPreferredSize(new Dimension(90, 35));

		JLabel totalLabel = new JLabel("총 합계: 0 P");
		totalLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		totalLabel.setForeground(new Color(0, 102, 204)); 
		totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); 

		JPanel rightTopPanel = new JPanel(new BorderLayout());
		rightTopPanel.setOpaque(false); // 배경 투명하게!
		rightTopPanel.add(buyAllBtn, BorderLayout.NORTH);
		rightTopPanel.add(totalLabel, BorderLayout.SOUTH);

		topPanel.add(title, BorderLayout.CENTER);
		topPanel.add(rightTopPanel, BorderLayout.EAST); 
		add(topPanel, BorderLayout.NORTH);

		// 💡 [수정] 리스트 패널 투명화 및 안쪽 여백 추가
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setOpaque(false); // 배경 투명하게!
		listPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // 벽과 카드 사이 여백
		
		Map<String, Integer> cart = dataManager.getCart();
		Map<String, ItemInfo> shopDB = dataManager.getShopDB();

		Runnable updateTotalLabel = () -> {
			int total = 0;
			for (Map.Entry<String, Integer> entry : cart.entrySet()) {
				total += shopDB.get(entry.getKey()).getPrice() * entry.getValue();
			}
			totalLabel.setText("총 합계: " + total + " P");
		};
		updateTotalLabel.run(); 

		buyAllBtn.addActionListener(e -> {
			if (cart.isEmpty()) {
				JOptionPane.showMessageDialog(this, "장바구니가 비어 있습니다.", "알림", JOptionPane.WARNING_MESSAGE);
				return;
			}
			int totalQty = 0;
			int totalPrice = 0;
			for (Map.Entry<String, Integer> entry : cart.entrySet()) {
				String itemName = entry.getKey();
				int qty = entry.getValue();
				totalQty += qty;
				totalPrice += (shopDB.get(itemName).getPrice() * qty);
			}

			String confirmMsg = String.format("총 %d개의 상품 (합계 %dP)\n정말 구매하시겠습니까?", totalQty, totalPrice);
			int result = JOptionPane.showConfirmDialog(this, confirmMsg, "구매 확인", JOptionPane.YES_NO_OPTION);
			if (result != JOptionPane.YES_OPTION) return;

			int currentPrice = dataManager.getMyPrice();
			if (currentPrice >= totalPrice) {
				dataManager.setMyPrice(currentPrice - totalPrice);
				if (UserInfo.currentUser != null) {
					String timeStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm"));
					UserInfo.currentUser.addPointHistory("[" + timeStr + "] [장바구니 전체 구매] 총 " + totalQty + "개 결제: -" + totalPrice + " P");
				}

				for (Map.Entry<String, Integer> entry : cart.entrySet()) {
					String itemName = entry.getKey();
					int qty = entry.getValue();
					dataManager.getOrderHistory().put(itemName, dataManager.getOrderHistory().getOrDefault(itemName, 0) + qty);
				}
				cart.clear();
				String msg = String.format("전체 구매가 완료되었습니다! \n\n총 구매 수량 : %d개\n총 결제 금액 : %dP\n남은 잔액 : %dP", totalQty, totalPrice, dataManager.getMyPrice());
				JOptionPane.showMessageDialog(this, msg, "결제 완료", JOptionPane.INFORMATION_MESSAGE);

				if (updateMainUI != null) updateMainUI.run();
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "잔액이 부족합니다. ㅠㅠ\n필요 금액: " + (totalPrice - UserInfo.currentUser.getPoints()) + "P", "구매 실패", JOptionPane.ERROR_MESSAGE);
			}
		});

		// 데이터 채우기
		for (Map.Entry<String, Integer> entry : cart.entrySet()) {
			String itemName = entry.getKey();
			int qty = entry.getValue();
			ItemInfo itemInfo = shopDB.get(itemName); 

			// 💡 [수정] 개별 상품을 하얀색 둥근 카드로 만들기
			JPanel rowPanel = new JPanel(new BorderLayout(15, 10)) {
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
			rowPanel.setOpaque(false); // 기존 배경 날리기
			rowPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12)); // 카드 안쪽 여백
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110)); // 카드가 위아래로 늘어나지 않게 고정

			// 이미지 영역
			URL imgUrl = getClass().getResource(itemInfo.getImgUrl());
			ImageIcon icon = null;
			if (imgUrl != null) {
				ImageIcon originalIcon = new ImageIcon(imgUrl);
				Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				icon = new ImageIcon(scaledImage);
			}

			JLabel imgLabel = (icon != null) ? new JLabel(icon) : new JLabel("No Img", SwingConstants.CENTER);
			imgLabel.setPreferredSize(new Dimension(60, 60));
			imgLabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
			rowPanel.add(imgLabel, BorderLayout.WEST);

			// 상품명 및 수량
			JLabel infoLabel = new JLabel(itemName + " : " + qty + "개");
			infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
			rowPanel.add(infoLabel, BorderLayout.CENTER);

			// 우측 제어 영역
			JPanel rightPanel = new JPanel(new BorderLayout()); 
			rightPanel.setOpaque(false);

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
			btnPanel.setOpaque(false); 

			// 💡 [수정] 둥근 버튼 적용
			JButton removeBtn = createRoundedButton("삭제", new Color(245, 245, 245), Color.DARK_GRAY);
			JButton buyBtn = createRoundedButton("구매", new Color(110, 160, 220), Color.WHITE);

			btnPanel.add(removeBtn);
			btnPanel.add(buyBtn);

			int totalPrice = itemInfo.getPrice() * qty;
			JLabel priceLabel = new JLabel("합계: " + totalPrice + "P");
			priceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
			priceLabel.setForeground(new Color(0, 102, 204)); 
			priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

			rightPanel.add(btnPanel, BorderLayout.NORTH);
			rightPanel.add(priceLabel, BorderLayout.SOUTH);

			removeBtn.addActionListener(e -> {
				dataManager.getCart().remove(itemName);
				listPanel.remove(rowPanel);
				listPanel.revalidate();
				listPanel.repaint();
				updateTotalLabel.run();
				if (dataManager.getCart().isEmpty()) dispose();
			});

			buyBtn.addActionListener(e -> {
				int currentPrice = dataManager.getMyPrice();
				if (currentPrice >= totalPrice) {
					dataManager.setMyPrice(currentPrice - totalPrice);
					if (UserInfo.currentUser != null) {
						String timeStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm"));
						UserInfo.currentUser.addPointHistory("[" + timeStr + "] [" + itemName + "] " + qty + "개 구매: -" + totalPrice + " P");
					}
					dataManager.getOrderHistory().put(itemName, dataManager.getOrderHistory().getOrDefault(itemName, 0) + qty);
					dataManager.getCart().remove(itemName);
					listPanel.remove(rowPanel);
					listPanel.revalidate();
					listPanel.repaint();
					updateTotalLabel.run();

					JOptionPane.showMessageDialog(this, "[" + itemName + "] 결제가 완료되었습니다.\n 남은 잔액: " + dataManager.getMyPrice() + "P");
					if (updateMainUI != null) updateMainUI.run();
					JOptionPane.showMessageDialog(this, "구매 완료!");
					if (dataManager.getCart().isEmpty()) dispose();
				} else {
					JOptionPane.showMessageDialog(this, "잔액이 부족합니다. ㅠㅠ\n필요 금액: " + (totalPrice - UserInfo.currentUser.getPoints()) + "P", "구매 실패", JOptionPane.ERROR_MESSAGE);
				}
			});

			rowPanel.add(rightPanel, BorderLayout.EAST);

			listPanel.add(rowPanel);
			listPanel.add(Box.createVerticalStrut(15)); // 💡 [수정] 카드 사이의 띄어쓰기 여백!
		}

		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setOpaque(false);               
		scrollPane.getViewport().setOpaque(false); 
		scrollPane.setBorder(null);                
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

		JButton closeBtn = new JButton("닫기");
		closeBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		closeBtn.setBackground(Color.WHITE);
		closeBtn.addActionListener(e -> dispose());
		add(closeBtn, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}