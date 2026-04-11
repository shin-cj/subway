package Font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;

public class loadfont {
    // 폰트 변수 선언
    public static Font Freesentation9Black;
    public static Font Freesentation8ExtraBold;
    public static Font Freesentation7Bold;

    static {
        loadFonts();
    }

    public static void loadFonts() {
        try {
            // 1. 파일 경로 설정 (파일명과 매칭 확인!)
            File blackFile = new File("Freesentation9Black.ttf");
            File extraFile = new File("Freesentation8ExtraBold.ttf");
            File boldFile = new File("Freesentation7Bold.ttf");

            // 2. 폰트 생성 및 등록
            if (blackFile.exists()) {
                Freesentation9Black = Font.createFont(Font.TRUETYPE_FONT, blackFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Freesentation9Black);
            }
            if (extraFile.exists()) {
                Freesentation8ExtraBold = Font.createFont(Font.TRUETYPE_FONT, extraFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Freesentation8ExtraBold);
            }
            if (boldFile.exists()) {
                Freesentation7Bold = Font.createFont(Font.TRUETYPE_FONT, boldFile);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Freesentation7Bold);
            }

            // 만약 로드 실패 시를 대비한 기본 폰트 설정
            if (Freesentation9Black == null) Freesentation9Black = new Font("SansSerif", Font.BOLD, 12);
            if (Freesentation8ExtraBold == null) Freesentation8ExtraBold = new Font("SansSerif", Font.BOLD, 12);
            if (Freesentation7Bold == null) Freesentation7Bold = new Font("SansSerif", Font.BOLD, 12);

        } catch (Exception e) {
            System.out.println("폰트 로드 실패: 기본 서체를 사용합니다.");
        }
    }
}