package MyPage;

import java.util.Random;

public class FortuneComent {

    public String[] fortunes = {
        "<html><div style='text-align: center;'>오늘은 지각을 면하겠군요!<br>행운의 역은 입니다.</div></html>",
        "<html><div style='text-align: center;'>환승 게이트에서 운명의 상대를<br>만날지도 모릅니다.</div></html>",
        "<html><div style='text-align: center;'>잃어버린 물건이 생각지도 못한<br>곳에서 나타날 거예요.</div></html>",
        "<html><div style='text-align: center;'>오늘의 운세는 맑음!<br>포인트 적립이 2배가 될 것 같아요.</div></html>",
        "<html><div style='text-align: center;'>오늘은 앉아서 갈 확률이 90%입니다.<br>편안한 이동 되세요!</div></html>"
    };

    public String getRandomFortune() {
        Random rm = new Random();
        int index = rm.nextInt(fortunes.length);
        return fortunes[index];
    }
}