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
    		{"녹차", "4100", "음료", "/IMG/GreenTea.jpg"},
    		{"아메리카노", "4500", "음료", "/IMG/IceAmericano.jpg"},
    		{"카페라떼", "5000", "음료", "/IMG/CafeLatte.jpg"},
    		{"자몽에이드", "5500", "음료", "/IMG/jamongAde.jpg"},
    		{"레몬티", "4800", "음료", "/IMG/RemonTea.jpg"},
    		{"딸기스무디", "6200", "음료", "/IMG/StrawBerry.png"},
    		{"망고주스", "5800", "음료", "/IMG/MangoJuice.jpg"},
    		{"바닐라쉐이크", "6300", "음료", "/IMG/BanilaShake.jpg"},
    		{"콜드브루", "4900", "음료", "/IMG/ColdBrew.png"},
    		{"복숭아아이스티", "4300", "음료", "/IMG/PeachIceTea.jpg"},
    		// 디저트
    		{"초코케이크", "6500", "디저트", "/IMG/ChocolateCake.jpg"},
    		{"치즈케이크", "6900", "디저트", "/IMG/CheeseCake.jpg"},
    		{"마카롱세트", "7200", "디저트", "/IMG/MacarongSet.jpg"},
    		{"크루아상", "3800", "디저트", "/IMG/Croasang.jpg"},
    		{"티라미수", "6800", "디저트", "/IMG/Tiramisu.jpg"},
    		{"블루베리머핀", "4200", "디저트", "/IMG/BlueBerryMurpin.jpg"},
    		{"에그타르트", "3500", "디저트", "/IMG/EggTart.jpg"},
    		{"허니브레드", "7900", "디저트", "/IMG/HoneyBread.jpg"},
    		{"와플", "6100", "디저트", "/IMG/Waffle.jpg"},
    		{"딸기롤케이크", "7300", "디저트", "/IMG/StrawBerryRollCake.jpg"},
    		// 레저/스포츠
    		{"요가매트", "29000", "레저/스포츠", "/IMG/YogaMat.jpg"},
    		{"덤벨세트", "45000", "레저/스포츠", "/IMG/DumbellSet.jpg"},
    		{"축구공", "32000", "레저/스포츠", "/IMG/SoccerBall.jpg"},
    		{"배드민턴라켓", "38000", "레저/스포츠", "/IMG/Badminton.jpg"},
    		{"캠핑랜턴", "27000", "레저/스포츠", "/IMG/CampingRanton.jpg"},
    		{"등산배낭", "69000", "레저/스포츠", "/IMG/MountainBag.jpg"},
    		{"자전거헬멧", "54000", "레저/스포츠", "/IMG/CycleHelmet.jpg"},
    		{"수영고글", "21000", "레저/스포츠", "/IMG/SwimingGoggle.jpg"},
    		{"줄넘기", "12000", "레저/스포츠", "/IMG/Junumgi.jpg"},
    		{"테니스공세트", "16000", "레저/스포츠", "/IMG/TennisBallSet.jpg"},
    		// 뷰티
    		{"수분크림", "24000", "뷰티", "/IMG/SubunCream.jpg"},
    		{"선크림", "18000", "뷰티", "/IMG/SunCream.jpg"},
    		{"립틴트", "13000", "뷰티", "/IMG/RipTeent.jpg"},
    		{"쿠션팩트", "32000", "뷰티", "/IMG/CousionFact.jpg"},
    		{"핸드크림", "9500", "뷰티", "/IMG/HandCream.jpg"},
    		{"클렌징폼", "14000", "뷰티", "/IMG/Form.jpg"},
    		{"바디로션", "22000", "뷰티", "/IMG/BodyRousion.jpg"},
    		{"헤어에센스", "17500", "뷰티", "/IMG/Hari.jpg"},
    		{"향수", "59000", "뷰티", "/IMG/Perfum.jpg"},
    		{"마스크팩세트", "11000", "뷰티", "/IMG/MaskPak.jpg"},
    		// 디지털/가전
    		{"무선이어폰", "89000", "디지털/가전", "/IMG/EarPhone.jpg"},
    		{"블루투스스피커", "67000", "디지털/가전", "/IMG/BluetuthSpeaker.jpg"},
    		{"기계식키보드", "99000", "디지털/가전", "/IMG/KeyBorad.jpg"},
    		{"무선마우스", "35000", "디지털/가전", "/IMG/Mouse.jpg"},
    		{"스마트워치", "159000", "디지털/가전", "/IMG/SmartWatch.jpg"},
    		{"보조배터리", "29000", "디지털/가전", "/IMG/Barttery.jpg"},
    		{"에어프라이어", "119000", "디지털/가전", "/IMG/Airfryer.jpg"},
    		{"전기포트", "39000", "디지털/가전", "/IMG/ElectronicPort.jpg"},
    		{"미니가습기", "26000", "디지털/가전", "/IMG/Gasuqgi.jpg"},
    		{"로봇청소기", "289000", "디지털/가전", "/IMG/RobotCleaning.jpg"},
    		// 패션
    		{"오버핏후드티", "39000", "패션", "/IMG/OverfitHoodie.jpg"},
    		{"데님팬츠", "49000", "패션", "/IMG/DanimPants.jpg"},
    		{"롱코트", "129000", "패션", "/IMG/Longcoat.jpg"},
    		{"니트스웨터", "54000", "패션", "/IMG/KnitSweat.jpg"},
    		{"운동화", "89000", "패션", "/IMG/RunnigShoes.jpg"},
    		{"크로스백", "46000", "패션", "/IMG/CrossBag.jpg"},
    		{"볼캡", "22000", "패션", "/IMG/BallCap.jpg"},
    		{"원피스", "67000", "패션", "/IMG/Onepice.jpg"},
    		{"가죽지갑", "58000", "패션", "/IMG/Wallet.jpg"},
    		{"실버목걸이", "35000", "패션", "/IMG/MokGuri.jpg"}
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