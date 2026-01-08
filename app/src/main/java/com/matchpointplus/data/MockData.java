package com.matchpointplus.data;

import com.matchpointplus.models.Match;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockData {
    public static List<Match> getUsers() {
        List<Match> matches = new ArrayList<>();
        
        // Candidates start with is_selected = false so you can use the "+" button to add them
        matches.add(createCandidate("1", "סופיה", 28, "תל אביב", "אוהבת אמנות, הליכות ארוכות על החוף.", Arrays.asList("אמנות", "טיולים"), "https://picsum.photos/seed/profile1/400/600"));
        matches.add(createCandidate("2", "ליאם", 32, "ירושלים", "מהנדס תוכנה ביום, מוזיקאי בלילה.", Arrays.asList("מוזיקה", "גיימינג"), "https://picsum.photos/seed/profile2/400/600"));
        matches.add(createCandidate("3", "אוליביה", 25, "חיפה", "חובבת טאקו, הופעות חיות וחוקרת ערים.", Arrays.asList("טאקו", "טיולים"), "https://picsum.photos/seed/profile4/400/600"));
        matches.add(createCandidate("4", "איתי", 27, "תל אביב", "חובב כושר, מנוי למרתונים.", Arrays.asList("ספורט", "ריצה"), "https://picsum.photos/seed/man4/400/600"));
        matches.add(createCandidate("5", "דניאל", 30, "רמת גן", "גיימר מושבע, עובד בהייטק.", Arrays.asList("גיימינג", "טכנולוגיה"), "https://picsum.photos/seed/man5/400/600"));
        matches.add(createCandidate("6", "יונתן", 24, "הרצליה", "סטודנט לאדריכלות, אוהב לצייר.", Arrays.asList("אמנות", "מוזיקה"), "https://picsum.photos/seed/man6/400/600"));
        matches.add(createCandidate("7", "גיא", 31, "חיפה", "גולש גלים, אוהב את הים.", Arrays.asList("גלישה", "ים"), "https://picsum.photos/seed/man7/400/600"));
        matches.add(createCandidate("8", "עומר", 29, "מודיעין", "חובב טבע וקמפינג.", Arrays.asList("טיולים", "טבע"), "https://picsum.photos/seed/man8/400/600"));
        matches.add(createCandidate("9", "רועי", 26, "גבעתיים", "שף במסעדה, אוהב לבשל.", Arrays.asList("בישול", "יין"), "https://picsum.photos/seed/man9/400/600"));
        matches.add(createCandidate("10", "נועם", 33, "פתח תקווה", "איש משפחה, אוהב ילדים וכלבים.", Arrays.asList("משפחה", "כלבים"), "https://picsum.photos/seed/man10/400/600"));
        matches.add(createCandidate("11", "אורי", 28, "ראשון לציון", "צילום זה החיים שלי.", Arrays.asList("צילום", "אמנות"), "https://picsum.photos/seed/man11/400/600"));
        matches.add(createCandidate("12", "עידן", 25, "ירושלים", "מתרגל יוגה ומדיטציה.", Arrays.asList("יוגה", "מדיטציה"), "https://picsum.photos/seed/man12/400/600"));
        matches.add(createCandidate("13", "תומר", 27, "חולון", "חולה כדורגל, אוהד מושבע.", Arrays.asList("ספורט", "כדורגל"), "https://picsum.photos/seed/man13/400/600"));
        matches.add(createCandidate("14", "מתן", 32, "אשדוד", "מהנדס בניין, אוהב לבנות דברים.", Arrays.asList("נגרות", "הייטק"), "https://picsum.photos/seed/man14/400/600"));
        matches.add(createCandidate("15", "נדב", 23, "באר שבע", "סטודנט להנדסה, אוהב בירה.", Arrays.asList("מוזיקה", "בירה"), "https://picsum.photos/seed/man15/400/600"));
        matches.add(createCandidate("16", "אלון", 35, "תל אביב", "יזם, תמיד חושב על הרעיון הבא.", Arrays.asList("עסקים", "כושר"), "https://picsum.photos/seed/man16/400/600"));
        matches.add(createCandidate("17", "בר", 26, "נתניה", "מדריך צלילה, חצי דג.", Arrays.asList("צלילה", "ים"), "https://picsum.photos/seed/man17/400/600"));
        matches.add(createCandidate("18", "שחר", 29, "רעננה", "מנגן על פסנתר, חובב ג'אז.", Arrays.asList("מוזיקה", "פסנתר"), "https://picsum.photos/seed/man18/400/600"));
        matches.add(createCandidate("19", "עמית", 24, "כפר סבא", "אוהב כלבים, מתנדב במקלט.", Arrays.asList("כלבים", "טבע"), "https://picsum.photos/seed/man19/400/600"));
        matches.add(createCandidate("20", "יהונתן", 31, "אפרת", "מורה להיסטוריה, אוהב ספרים.", Arrays.asList("קריאה", "היסטוריה"), "https://picsum.photos/seed/man20/400/600"));
        matches.add(createCandidate("21", "רון", 27, "נס ציונה", "רוכב על אופנוע, אוהב מהירות.", Arrays.asList("אופנועים", "טיולים"), "https://picsum.photos/seed/man21/400/600"));
        matches.add(createCandidate("22", "ליאור", 30, "אילת", "מדריך טיולים במדבר.", Arrays.asList("מדבר", "טיולים"), "https://picsum.photos/seed/man22/400/600"));
        matches.add(createCandidate("23", "יובל", 28, "תל אביב", "מעצב גרפי, אוהב עיצוב.", Arrays.asList("עיצוב", "אמנות"), "https://picsum.photos/seed/man23/400/600"));

        return matches;
    }

    private static Match createCandidate(String id, String name, int age, String location, String bio, List<String> interests, String url) {
        Match m = new Match(id, name, age, location, bio, interests, url, Arrays.asList(url));
        m.setSelected(false); // Default state: not in the user's list yet
        return m;
    }
}
