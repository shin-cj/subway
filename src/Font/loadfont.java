package Font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * 어플리케이션 전역에서 사용할 커스텀 폰트를 로드하고 관리하는 클래스입니다.
 * 특수문자 및 비밀번호 깨짐 방지를 위한 SafeFont 설정을 포함합니다.
 */
public class loadfont {
    // --- 1. 나눔스퀘어 라운드 시리즈 ---
    public static Font NanumEB; // ExtraBold
    public static Font NanumB;  // Bold
    public static Font NanumR;  // Regular
    // 변경
    // --- 2. 타이틀용 어비 세현 서체 ---
    public static Font UhBeeSehyun;
    
    // --- 3. [핵심] 깨짐 방지용 안전 폰트 ---
    // 특수문자(↔, 📍 등)는 시스템 기본 폰트인 'Malgun Gothic'(맑은 고딕)이 가장 안전합니다.
    public static Font SafeFont = new Font("Malgun Gothic", Font.BOLD, 15);
    // 비밀번호 필드의 점(•)은 시스템 기본 SansSerif를 사용해야 깨지지 않습니다.
    public static Font PasswordFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

    // --- 4. 기존 변수 이름 호환용 ---
    public static Font Freesentation9Black;
    public static Font Freesentation8ExtraBold;
    public static Font Freesentation7Bold;

    static {
        loadFonts();
    }

    public static void loadFonts() {
        try {
            // 폰트 파일 로드 (파일경로가 src 바로 아래일 경우 /파일명.ttf)
            NanumEB = loadFontFile("/NanumSquareRoundEB.ttf", 12f);
            NanumB = loadFontFile("/NanumSquareRoundB.ttf", 12f);
            NanumR = loadFontFile("/NanumSquareRoundR.ttf", 12f);
            UhBeeSehyun = loadFontFile("/UhBee Se_hyun Bold.ttf", 12f);
            
            // 기존 변수 호환성 유지
            Freesentation9Black = NanumEB;
            Freesentation8ExtraBold = NanumB;
            Freesentation7Bold = NanumR;

        } catch (Exception e) {
            System.err.println("커스텀 폰트 로드 실패: 기본 안전 서체를 사용합니다.");
            setFallbackFonts();
        }
    }

    private static Font loadFontFile(String path, float size) throws Exception {
        InputStream is = loadfont.class.getResourceAsStream(path);
        if (is == null) {
            System.err.println("폰트 파일을 찾을 수 없음: " + path);
            return SafeFont.deriveFont(size); // 파일 없으면 안전서체로 대체
        }
        Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        return font;
    }

    private static void setFallbackFonts() {
        if (NanumEB == null) NanumEB = SafeFont;
        if (NanumB == null) NanumB = SafeFont;
        if (NanumR == null) NanumR = new Font("Malgun Gothic", Font.PLAIN, 12);
        if (UhBeeSehyun == null) UhBeeSehyun = SafeFont;
        
        if (Freesentation9Black == null) Freesentation9Black = NanumEB;
        if (Freesentation8ExtraBold == null) Freesentation8ExtraBold = NanumB;
        if (Freesentation7Bold == null) Freesentation7Bold = NanumR;
    }
}