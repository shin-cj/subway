package Shop;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import Share.UserInfo;

public class ShopDataManager {
    
    private static ShopDataManager instance = new ShopDataManager();
    
    // 💡 상점 DB 및 사용자 데이터
    private Map<String, ItemInfo> shopDB = new LinkedHashMap<>();
    private Map<String, Integer> cart = new HashMap<>();
    private Map<String, Integer> orderHistory = new HashMap<>();
    private Set<String> wishlist = new LinkedHashSet<>();
    private Set<String> completedOrders = new LinkedHashSet<>();
    
    
    public ShopDataManager() {
//        shopDB.put("전설의 검", new ItemInfo("전설의 검", 3000, "레저/스포츠", "/testIMGjpg.jpg"));
//        shopDB.put("빨간 물약", new ItemInfo("빨간 물약", 500, "디저트", "/testIMG2.jpg"));
//        shopDB.put("가죽 갑옷", new ItemInfo("가죽 갑옷", 2000, "뷰티", "/testIMG3.jpg"));
//        shopDB.put("마법의 지팡이", new ItemInfo("마법의 지팡이", 4500, "디지털/가전", "/testIMG4.jpg"));
//        shopDB.put("귀환 주문서", new ItemInfo("귀환 주문서", 1000, "패션", "/testIMG5.jpg"));
//        shopDB.put("박카스", new ItemInfo("박카스", 3000, "음료", "/testIMG6.png"));
//        shopDB.put("녹차", new ItemInfo("녹차", 4100, "음료", "/testIMG7.jpg"));
    String[][] rawData = {
    	//--- [음료] ---
    		{"녹차", "4100", "음료", "/IMG/testIMG1.jpg"},
    		{"아메리카노", "4500", "음료", "/IMG/testIMG2.jpg"},
    		{"카페라떼", "5000", "음료", "/IMG/testIMG3.jpg"},
    		{"자몽에이드", "5500", "음료", "/IMG/testIMG4.jpg"},
    		{"레몬티", "4800", "음료", "/IMG/testIMG5.jpg"},
    		{"딸기스무디", "6200", "음료", "/IMG/testIMG6.jpg"},
    		{"망고주스", "5800", "음료", "/IMG/testIMG7.jpg"},
    		{"바닐라쉐이크", "6300", "음료", "/IMG/testIMG8.jpg"},
    		{"콜드브루", "4900", "음료", "/IMG/testIMG9.jpg"},
    		{"복숭아아이스티", "4300", "음료", "/IMG/testIMG10.jpg"},
    		// 디저트
    		{"초코케이크", "6500", "디저트", "/IMG/testIMG11.jpg"},
    		{"치즈케이크", "6900", "디저트", "/IMG/testIMG12.jpg"},
    		{"마카롱세트", "7200", "디저트", "/IMG/testIMG13.jpg"},
    		{"크루아상", "3800", "디저트", "/IMG/testIMG14.jpg"},
    		{"티라미수", "6800", "디저트", "/IMG/testIMG15.jpg"},
    		{"블루베리머핀", "4200", "디저트", "/IMG/testIMG16.jpg"},
    		{"에그타르트", "3500", "디저트", "/IMG/testIMG17.jpg"},
    		{"허니브레드", "7900", "디저트", "/IMG/testIMG18.jpg"},
    		{"와플", "6100", "디저트", "/IMG/testIMG19.jpg"},
    		{"딸기롤케이크", "7300", "디저트", "/IMG/testIMG20.jpg"},
    		// 레저/스포츠
    		{"요가매트", "29000", "레저/스포츠", "/IMG/testIMG21.jpg"},
    		{"덤벨세트", "45000", "레저/스포츠", "/IMG/testIMG22.jpg"},
    		{"축구공", "32000", "레저/스포츠", "/IMG/testIMG23.jpg"},
    		{"배드민턴라켓", "38000", "레저/스포츠", "/IMG/testIMG24.jpg"},
    		{"캠핑랜턴", "27000", "레저/스포츠", "/IMG/testIMG25.jpg"},
    		{"등산배낭", "69000", "레저/스포츠", "/IMG/testIMG26.jpg"},
    		{"자전거헬멧", "54000", "레저/스포츠", "/IMG/testIMG27.jpg"},
    		{"수영고글", "21000", "레저/스포츠", "/IMG/testIMG28.jpg"},
    		{"줄넘기", "12000", "레저/스포츠", "/IMG/testIMG29.jpg"},
    		{"테니스공세트", "16000", "레저/스포츠", "/IMG/testIMG30.jpg"},
    		// 뷰티
    		{"수분크림", "24000", "뷰티", "/IMG/testIMG31.jpg"},
    		{"선크림", "18000", "뷰티", "/IMG/testIMG32.jpg"},
    		{"립틴트", "13000", "뷰티", "/IMG/testIMG33.jpg"},
    		{"쿠션팩트", "32000", "뷰티", "/IMG/testIMG34.jpg"},
    		{"핸드크림", "9500", "뷰티", "/IMG/testIMG35.jpg"},
    		{"클렌징폼", "14000", "뷰티", "/IMG/testIMG36.jpg"},
    		{"바디로션", "22000", "뷰티", "/IMG/testIMG37.jpg"},
    		{"헤어에센스", "17500", "뷰티", "/IMG/testIMG38.jpg"},
    		{"향수", "59000", "뷰티", "/IMG/testIMG39.jpg"},
    		{"마스크팩세트", "11000", "뷰티", "/IMG/testIMG40.jpg"},
    		// 디지털/가전
    		{"무선이어폰", "89000", "디지털/가전", "/IMG/testIMG41.jpg"},
    		{"블루투스스피커", "67000", "디지털/가전", "/IMG/testIMG42.jpg"},
    		{"기계식키보드", "99000", "디지털/가전", "/IMG/testIMG43.jpg"},
    		{"무선마우스", "35000", "디지털/가전", "/IMG/testIMG44.jpg"},
    		{"스마트워치", "159000", "디지털/가전", "/IMG/testIMG45.jpg"},
    		{"보조배터리", "29000", "디지털/가전", "/IMG/testIMG46.jpg"},
    		{"에어프라이어", "119000", "디지털/가전", "/IMG/testIMG47.jpg"},
    		{"전기포트", "39000", "디지털/가전", "/IMG/testIMG48.jpg"},
    		{"미니가습기", "26000", "디지털/가전", "/IMG/testIMG49.jpg"},
    		{"로봇청소기", "289000", "디지털/가전", "/IMG/testIMG50.jpg"},
    		// 패션
    		{"오버핏후드티", "39000", "패션", "/IMG/testIMG51.jpg"},
    		{"데님팬츠", "49000", "패션", "/IMG/testIMG52.jpg"},
    		{"롱코트", "129000", "패션", "/IMG/testIMG53.jpg"},
    		{"니트스웨터", "54000", "패션", "/IMG/testIMG54.jpg"},
    		{"운동화", "89000", "패션", "/IMG/testIMG55.jpg"},
    		{"크로스백", "46000", "패션", "/IMG/testIMG56.jpg"},
    		{"볼캡", "22000", "패션", "/IMG/testIMG57.jpg"},
    		{"원피스", "67000", "패션", "/IMG/testIMG58.jpg"},
    		{"가죽지갑", "58000", "패션", "/IMG/testIMG59.jpg"},
    		{"실버목걸이", "35000", "패션", "/IMG/testIMG60.jpg"}
    };
    
    
    for (String[] data : rawData) {
        String name = data[0];
        int price = Integer.parseInt(data[1]); // 글자("2500")를 숫자(2500)로 변환
        String type = data[2];
        String imgPath = data[3];
        

        // 반복문이 돌아가면서 ItemInfo를 자동으로 생성해 shopDB에 저장합니다.
        shopDB.put(name, new ItemInfo(name, price, type, imgPath));
    }
    
    }

    public static ShopDataManager getInstance() {
        return instance;
    }
    
    // 💡 UserInfo.currentUser와 포인트 연동
    public int getMyPrice() { 
        if (UserInfo.currentUser != null) {
            return UserInfo.currentUser.getPoints();
        }
        return 0;
    }
    
    public void setMyPrice(int myPrice) {
        if (UserInfo.currentUser != null) {
            UserInfo.currentUser.setPoints(myPrice);
        }
    }
    
    public void toggleWishlist(String itemName) {
        if (wishlist.contains(itemName)) {
            wishlist.remove(itemName);
        } else {
            wishlist.add(itemName);
        }
    }
    
    public Map<String, ItemInfo> getShopDB() { return shopDB; }
    public Map<String, Integer> getCart() { return cart; }
    public Map<String, Integer> getOrderHistory() { return orderHistory; }
    public Set<String> getWishlist() { return wishlist; }
    public Set<String> getCompletedOrders() { return completedOrders; }
    
    public void addToCart(String itemName) {
        cart.put(itemName, cart.getOrDefault(itemName, 0) + 1);
    }

    @Override
    public String toString() {
        return "보유 포인트 : " + getMyPrice() + "p";
    }
}