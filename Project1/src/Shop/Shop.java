package Shop;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.*;

import Frame.FrameBase;
import Frame.ModernScrollBarUI;
import Share.UserInfo;

public class Shop extends JPanel {
	private ShopDataManager dataManager = ShopDataManager.getInstance();
	private JPanel listPanel;
	private String selectedCategory;

	private Map<String, Integer> shopDB = new LinkedHashMap<>();
	private JLabel myMoneyLabel;

	private CartDetail cartDetailWindow = null;
	private OrderDetail orderDetailWindow = null;

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
		btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
		return btn;
	}

	// 💡 [수정] Shop 자체 배경을 단색 크림색으로 그리기
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(new Color(255, 253, 240)); // 일기장 단색
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.dispose();
	}

	public Shop(String category) {
		this.selectedCategory = category;
		setLayout(new BorderLayout());
		setOpaque(false); // 배경 직접 그리기 위해 투명화

		// 상단 패널
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		topLeftPanel.setOpaque(false);

		JButton backBtn = new JButton();
		try {
			java.net.URL imgUrl = getClass().getResource("/reply_18032509.png");
			if (imgUrl != null) {
				ImageIcon originalIcon = new ImageIcon(imgUrl);
				Image scaledImage = originalIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
				backBtn.setIcon(new ImageIcon(scaledImage));
			} else {
				backBtn.setText("〈");
				backBtn.setFont(new Font("맑은 고딕", Font.BOLD, 22));
				backBtn.setForeground(new Color(150, 150, 150));
			}
		} catch (Exception ex) {
			backBtn.setText("〈");
			backBtn.setFont(new Font("맑은 고딕", Font.BOLD, 22));
		}
		backBtn.setContentAreaFilled(false);
		backBtn.setBorderPainted(false);
		backBtn.setFocusPainted(false);
		backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		backBtn.setMargin(new Insets(0, 0, 0, 5));

		myMoneyLabel = new JLabel("내 잔액: " + dataManager.getMyPrice() + " P");
		myMoneyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));

		topLeftPanel.add(backBtn);
		topLeftPanel.add(myMoneyLabel);

		JPanel topBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		topBtnPanel.setOpaque(false);

		// 💡 [수정] 상단 버튼도 둥글게!
		JButton cartBtnTop = createRoundedButton("장바구니", new Color(240, 240, 240), Color.BLACK);
		JButton orderHistoryBtn = createRoundedButton("주문 내역", new Color(240, 240, 240), Color.BLACK);
//		cartBtnTop.setPreferredSize(new Dimension(100, 30));
//		orderHistoryBtn.setPreferredSize(new Dimension(100, 30));

		topBtnPanel.add(cartBtnTop);
		topBtnPanel.add(orderHistoryBtn);

		topPanel.add(topLeftPanel, BorderLayout.WEST);
		topPanel.add(topBtnPanel, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		// 💡 [수정] 리스트 패널 투명화 및 여백 추가
		listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setOpaque(false);
		listPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

		refreshProductList();

		JPanel wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.setOpaque(false);
		wrapperPanel.add(listPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(wrapperPanel);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		add(scrollPane, BorderLayout.CENTER);

		backBtn.addActionListener(e -> FrameBase.getInstance(new CategoryPage()));

		cartBtnTop.addActionListener(e -> {
			if (dataManager.getCart().isEmpty()) {
				JOptionPane.showMessageDialog(this, "장바구니가 비어 있습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			} else {
				if (cartDetailWindow == null || !cartDetailWindow.isVisible()) {
					cartDetailWindow = new CartDetail(dataManager,
							() -> myMoneyLabel.setText("내 잔액: " + dataManager.getMyPrice() + " P"));
				} else {
					cartDetailWindow.toFront();
				}
			}
		});

		orderHistoryBtn.addActionListener(e -> {
			if (dataManager.getOrderHistory().isEmpty()) {
				JOptionPane.showMessageDialog(this, "주문 내역이 없습니다.", "알림", JOptionPane.WARNING_MESSAGE);
			} else {
				if (orderDetailWindow == null || !orderDetailWindow.isVisible()) {
					orderDetailWindow = new OrderDetail(dataManager,
							() -> myMoneyLabel.setText("내 잔액: " + dataManager.getMyPrice() + " P"));
				} else {
					orderDetailWindow.toFront();
				}
			}
		});
		add(Share.BottomMenu.addFooterBar(this), BorderLayout.SOUTH);
		setVisible(true);
	}

	private void refreshProductList() {
		listPanel.removeAll();

		Map<String, ItemInfo> shopDB = dataManager.getShopDB();
		Set<String> wishItems = dataManager.getWishlist();

		for (String itemName : wishItems) {
			ItemInfo item = shopDB.get(itemName);
			if (item != null && (selectedCategory == null || item.getType().equals(selectedCategory))) {
				listPanel.add(createProductPanel(item));
				listPanel.add(Box.createVerticalStrut(15)); // 💡 카드 사이 띄어쓰기!
			}
		}

		for (Map.Entry<String, ItemInfo> entry : shopDB.entrySet()) {
			if (!wishItems.contains(entry.getKey())) {
				ItemInfo item = entry.getValue();
				if (selectedCategory == null || item.getType().equals(selectedCategory)) {
					listPanel.add(createProductPanel(item));
					listPanel.add(Box.createVerticalStrut(15)); // 💡 카드 사이 띄어쓰기!
				}
			}
		}

		listPanel.revalidate();
		listPanel.repaint();
	}

	private JPanel createProductPanel(ItemInfo item) {
		// 💡 1. 카드 내부 여백과 요소 사이의 갭을 살짝 줄여서 공간 확보 (15 -> 10)
		JPanel panel = new JPanel(new BorderLayout(10, 10)) {
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
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 내부 여백 다이어트
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

		// 💡 2. 쏠림 현상 방지를 위해 '왼쪽 정렬'로 확실하게 고정!
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);

		String imgPath = item.getImgUrl();
		ImageIcon img = null;
		if (imgPath != null && !imgPath.trim().isEmpty()) {
			java.net.URL imgURL = getClass().getResource(imgPath);
			if (imgURL != null) {
				img = new ImageIcon(imgURL);
				Image scaledImg = img.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
				img = new ImageIcon(scaledImg);
			}
		}

		JLabel imageLabel = (img != null) ? new JLabel(img, SwingConstants.CENTER)
				: new JLabel("이미지 없음", SwingConstants.CENTER);
		imageLabel.setPreferredSize(new Dimension(100, 100));
		imageLabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setOpaque(false);

		JPanel topDataPanel = new JPanel(new BorderLayout());
		topDataPanel.setOpaque(false);

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setOpaque(false);
		textPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		JLabel nameLabel = new JLabel(item.getName());
		nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel priceLabel = new JLabel("가격: " + item.getPrice() + " P");
		priceLabel.setForeground(new Color(100, 100, 100));
		priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		textPanel.add(Box.createVerticalStrut(5));
		textPanel.add(nameLabel);
		textPanel.add(Box.createVerticalStrut(8));
		textPanel.add(priceLabel);

		// 💡 3. 수량 패널 조절 (버튼 크기를 28x28로 살짝 줄임)
		JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		qtyPanel.setOpaque(false);
		JLabel qtyLabel = new JLabel("수량:");
		qtyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		qtyPanel.add(qtyLabel);

		final int[] currentQty = { 1 };

		JLabel qtyValueLabel = new JLabel(String.valueOf(currentQty[0]), SwingConstants.CENTER);
		qtyValueLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		qtyValueLabel.setPreferredSize(new Dimension(20, 25));

		JButton minusBtn = createRoundedButton("-", new Color(240, 240, 240), Color.DARK_GRAY);
		minusBtn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		minusBtn.setPreferredSize(new Dimension(20, 20)); // 크기 최적화
		minusBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));

		JButton plusBtn = createRoundedButton("+", new Color(240, 240, 240), Color.DARK_GRAY);
		plusBtn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		plusBtn.setPreferredSize(new Dimension(20, 20)); // 크기 최적화
		plusBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));

		// 💡 [수정] 꾹 누르면 연속으로 작동하는 스마트한 마우스 이벤트 & 타이머 적용!

				// ⏱️ 1. 마이너스(-) 자동 반복 타이머 (100ms=0.1초마다 실행)
				javax.swing.Timer minusTimer = new javax.swing.Timer(100, e -> {
					if (currentQty[0] > 1) {
						currentQty[0]--;
						qtyValueLabel.setText(String.valueOf(currentQty[0]));
					}
				});
				minusTimer.setInitialDelay(400); // 0.4초 동안 꾹 누르고 있어야 쭈르륵 시작!

				minusBtn.addMouseListener(new java.awt.event.MouseAdapter() {
					@Override
					public void mousePressed(java.awt.event.MouseEvent e) {
						// 클릭하자마자 즉시 1번 감소
						if (currentQty[0] > 1) {
							currentQty[0]--;
							qtyValueLabel.setText(String.valueOf(currentQty[0]));
						}
						minusTimer.start(); // 꾹 누르고 있으면 타이머 발동
					}
					@Override
					public void mouseReleased(java.awt.event.MouseEvent e) {
						minusTimer.stop(); // 마우스에서 손을 떼면 정지!
					}
					@Override
					public void mouseExited(java.awt.event.MouseEvent e) {
						minusTimer.stop(); // 마우스가 버튼 밖으로 삐져나가도 정지! (안전장치)
					}
				});

				javax.swing.Timer plusTimer = new javax.swing.Timer(100, e -> {
					if (currentQty[0] < 99) {
						currentQty[0]++;
						qtyValueLabel.setText(String.valueOf(currentQty[0]));
					}
				});
				plusTimer.setInitialDelay(400);

				plusBtn.addMouseListener(new java.awt.event.MouseAdapter() {
					@Override
					public void mousePressed(java.awt.event.MouseEvent e) {
						// 클릭하자마자 즉시 1번 증가
						if (currentQty[0] < 99) {
							currentQty[0]++;
							qtyValueLabel.setText(String.valueOf(currentQty[0]));
						}
						plusTimer.start();
					}
					@Override
					public void mouseReleased(java.awt.event.MouseEvent e) {
						plusTimer.stop();
					}
					@Override
					public void mouseExited(java.awt.event.MouseEvent e) {
						plusTimer.stop();
					}
				});

		qtyPanel.add(minusBtn);
		qtyPanel.add(qtyValueLabel);
		qtyPanel.add(plusBtn);

		topDataPanel.add(textPanel, BorderLayout.CENTER);
		topDataPanel.add(qtyPanel, BorderLayout.EAST);

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		btnPanel.setOpaque(false);

		// 💡 4. 하단 버튼 글씨 다이어트! (가장 중요: 가로 폭 초과 방지)
		JButton wishBtn = createRoundedButton("♥ 찜", new Color(245, 245, 245), Color.DARK_GRAY);
		JButton cartBtn = createRoundedButton("담기", new Color(240, 240, 240), Color.BLACK);
		JButton buyBtn = createRoundedButton("구매", new Color(110, 160, 220), Color.WHITE);

		btnPanel.add(wishBtn);
		btnPanel.add(cartBtn);
		btnPanel.add(buyBtn);

		contentPanel.add(topDataPanel, BorderLayout.CENTER);
		contentPanel.add(btnPanel, BorderLayout.SOUTH);

		panel.add(imageLabel, BorderLayout.WEST);
		panel.add(contentPanel, BorderLayout.CENTER);

		boolean isWished = dataManager.getWishlist().contains(item.getName());
		if (isWished)
			wishBtn.setForeground(Color.RED);

		wishBtn.addActionListener(e -> {
			dataManager.toggleWishlist(item.getName());
			refreshProductList();
		});

		cartBtn.addActionListener(e -> {
			int selectedQty = currentQty[0];
			Map<String, Integer> cart = dataManager.getCart();
			cart.put(item.getName(), cart.getOrDefault(item.getName(), 0) + selectedQty);
			JOptionPane.showMessageDialog(this, "[" + item.getName() + "] " + selectedQty + "개를 장바구니에 담았습니다. 🛒");

			currentQty[0] = 1;
			qtyValueLabel.setText("1");
		});

		buyBtn.addActionListener(e -> {
			int selectedQty = currentQty[0];
			int currentPrice = dataManager.getMyPrice();
			int totalPrice = item.getPrice() * selectedQty;

			if (currentPrice >= totalPrice) {
				dataManager.setMyPrice(currentPrice - totalPrice);

				if (UserInfo.currentUser != null) {
					String timeStr = java.time.LocalDateTime.now()
							.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm"));
					UserInfo.currentUser.addPointHistory("[" + timeStr + "] [" + item.getName() + "] " + selectedQty
							+ "개 구매: -" + totalPrice + " P");
				}

				dataManager.getOrderHistory().put(item.getName(),
						dataManager.getOrderHistory().getOrDefault(item.getName(), 0) + selectedQty);
				myMoneyLabel.setText("내 잔액: " + dataManager.getMyPrice() + " P");
				JOptionPane.showMessageDialog(this,
						"[" + item.getName() + "] " + selectedQty + "개 결제가 완료되었습니다! 🎉\n결제 금액: " + totalPrice + " P");

				currentQty[0] = 1;
				qtyValueLabel.setText("1");
			} else {
				JOptionPane.showMessageDialog(this,
						"잔액이 부족합니다. ㅠㅠ\n필요 금액: " + (totalPrice - UserInfo.currentUser.getPoints()) + " P", "구매 실패",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		return panel;
	}
}