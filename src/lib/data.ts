import type { User, Conversation, Message } from '@/lib/types';
import { PlaceHolderImages } from '@/lib/placeholder-images';

const getImage = (id: string) => PlaceHolderImages.find(img => img.id === id)?.imageUrl || 'https://picsum.photos/seed/error/400/600';

export const users: User[] = [
  {
    id: '1',
    name: 'סופיה',
    age: 28,
    location: 'תל אביב-יפו',
    bio: 'אוהבת אמנות, הליכות ארוכות על החוף ואת הגולדן רטריבר שלי, סאני. מחפשת מישהו לחלוק איתו הרפתקאות.',
    interests: ['אמנות', 'טיולים', 'כלבים', 'נסיעות'],
    profilePicture: getImage('profile1'),
    pictures: [getImage('profile1'), getImage('profile3'), getImage('profile6')],
  },
  {
    id: '2',
    name: 'ליאם',
    age: 32,
    location: 'ירושלים',
    bio: 'מהנדס תוכנה ביום, מוזיקאי בלילה. כנראה אנצח אותך במריו קארט. בוא/י נשתה קפה.',
    interests: ['מוזיקה', 'גיימינג', 'קפה', 'טכנולוגיה'],
    profilePicture: getImage('profile2'),
    pictures: [getImage('profile2'), getImage('profile5'), getImage('profile8')],
  },
  {
    id: '3',
    name: 'אוליביה',
    age: 25,
    location: 'חיפה',
    bio: 'סתם בחורה שאוהבת טאקו, מוזיקה חיה ולחקור ערים חדשות. דוברת סרקזם וציטוטים מסרטים שוטף.',
    interests: ['טאקו', 'הופעות חיות', 'טיולים', 'סרטים'],
    profilePicture: getImage('profile4'),
    pictures: [getImage('profile4'), getImage('profile6'), getImage('profile7')],
  },
  {
    id: '4',
    name: 'נוח',
    age: 30,
    location: 'באר שבע',
    bio: 'חובב כושר, שף שאפתן, ומאמין גדול שאננס שייך על פיצה. שכנע/י אותי אחרת.',
    interests: ['כושר', 'בישול', 'פיצה', 'קריאה'],
    profilePicture: getImage('profile5'),
    pictures: [getImage('profile5'), getImage('profile2'), getImage('profile8')],
  },
  {
    id: '5',
    name: 'אווה',
    age: 29,
    location: 'ראשון לציון',
    bio: 'שחקנית, יוגיסטית ואמא שאפתנית לצמחים. הדייט האידיאלי שלי הוא ביקור בשוק איכרים ואז לבשל ארוחה ביחד.',
    interests: ['יוגה', 'משחק', 'צמחים', 'בישול'],
    profilePicture: getImage('profile7'),
    pictures: [getImage('profile7'), getImage('profile1'), getImage('profile3')],
  }
];

export const currentUser: User = {
    id: 'current-user',
    name: 'אלכס',
    age: 29,
    location: 'תל אביב-יפו',
    bio: 'נפש יצירתית שמגלה את העיר. אני נהנה/ת מצילום, מציאת בתי קפה נסתרים ושיחות שנונות. מחפש/ת קשר אמיתי.',
    interests: ['צילום', 'קפה', 'אמנות', 'עיצוב'],
    profilePicture: getImage('currentUser'),
    pictures: [getImage('currentUser'), 'https://picsum.photos/seed/me2/400/600', 'https://picsum.photos/seed/me3/400/600']
}

export const conversations: Conversation[] = [
    {
        id: '1',
        participant: users[0],
        lastMessage: { text: 'היי, אני אוהבת את הכלב שלך! איך קוראים לו?', timestamp: 'לפני שעתיים' },
        unreadCount: 1,
    },
    {
        id: '2',
        participant: users[2],
        lastMessage: { text: 'גם אני חובבת טאקו גדולה! אנחנו צריכים ללכת לאכול.', timestamp: 'אתמול' },
        unreadCount: 0,
    },
    {
        id: '3',
        participant: users[3],
        lastMessage: { text: 'אננס על פיצה זה פשע, אבל אני מוכנה לסלוח לך. 😉', timestamp: 'לפני 3 ימים' },
        unreadCount: 0,
    }
];

export const messages: Record<string, Message[]> = {
  '1': [
    { id: 'm1', senderId: '1', text: 'היי, אני אוהבת את הכלב שלך! איך קוראים לו?', timestamp: 'לפני שעתיים' },
    { id: 'm2', senderId: 'current-user', text: 'תודה! קוראים לו סאני. הוא הכי טוב.', timestamp: 'לפני שעה' },
  ],
  '2': [
    { id: 'm3', senderId: '3', text: 'גם אני חובבת טאקו גדולה! אנחנו צריכים ללכת לאכול.', timestamp: 'אתמול' },
  ],
  '3': [
    { id: 'm4', senderId: '3', text: 'אננס על פיצה זה פשע, אבל אני מוכנה לסלוח לך. 😉', timestamp: 'לפני 3 ימים' },
  ],
};
