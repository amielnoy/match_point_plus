import type { User, Conversation, Message } from '@/lib/types';
import { PlaceHolderImages } from '@/lib/placeholder-images';

const getImage = (id: string) => PlaceHolderImages.find(img => img.id === id)?.imageUrl || 'https://picsum.photos/seed/error/400/600';

export const users: User[] = [
  {
    id: '1',
    name: 'Sophia',
    age: 28,
    location: 'San Francisco, CA',
    bio: 'Lover of art, long walks on the beach, and my golden retriever, Sunny. Looking for someone to share adventures with.',
    interests: ['Art', 'Hiking', 'Dogs', 'Traveling'],
    profilePicture: getImage('profile1'),
    pictures: [getImage('profile1'), getImage('profile3'), getImage('profile6')],
  },
  {
    id: '2',
    name: 'Liam',
    age: 32,
    location: 'New York, NY',
    bio: 'Software engineer by day, musician by night. I can probably beat you at Mario Kart. Let\'s grab a coffee.',
    interests: ['Music', 'Gaming', 'Coffee', 'Technology'],
    profilePicture: getImage('profile2'),
    pictures: [getImage('profile2'), getImage('profile5'), getImage('profile8')],
  },
  {
    id: '3',
    name: 'Olivia',
    age: 25,
    location: 'Austin, TX',
    bio: 'Just a girl who loves tacos, live music, and exploring new cities. Fluent in sarcasm and movie quotes.',
    interests: ['Tacos', 'Live Music', 'Travel', 'Movies'],
    profilePicture: getImage('profile4'),
    pictures: [getImage('profile4'), getImage('profile6'), getImage('profile7')],
  },
  {
    id: '4',
    name: 'Noah',
    age: 30,
    location: 'Chicago, IL',
    bio: 'Fitness enthusiast, aspiring chef, and a firm believer that pineapple belongs on pizza. Change my mind.',
    interests: ['Fitness', 'Cooking', 'Pizza', 'Reading'],
    profilePicture: getImage('profile5'),
    pictures: [getImage('profile5'), getImage('profile2'), getImage('profile8')],
  },
  {
    id: '5',
    name: 'Ava',
    age: 29,
    location: 'Los Angeles, CA',
    bio: 'Actress, yogi, and aspiring plant mom. My ideal date is a farmers market trip followed by cooking a meal together.',
    interests: ['Yoga', 'Acting', 'Plants', 'Cooking'],
    profilePicture: getImage('profile7'),
    pictures: [getImage('profile7'), getImage('profile1'), getImage('profile3')],
  }
];

export const currentUser: User = {
    id: 'current-user',
    name: 'Alex',
    age: 29,
    location: 'San Francisco, CA',
    bio: 'Creative soul exploring the city. I enjoy photography, finding hidden cafes, and witty banter. Looking for a genuine connection.',
    interests: ['Photography', 'Coffee', 'Art', 'Design'],
    profilePicture: getImage('currentUser'),
    pictures: [getImage('currentUser'), 'https://picsum.photos/seed/me2/400/600', 'https://picsum.photos/seed/me3/400/600']
}

export const conversations: Conversation[] = [
    {
        id: '1',
        participant: users[0],
        lastMessage: { text: 'Hey, I love your dog! What\'s his name?', timestamp: '2h ago' },
        unreadCount: 1,
    },
    {
        id: '2',
        participant: users[2],
        lastMessage: { text: 'I\'m also a big fan of tacos! We should get some.', timestamp: '1d ago' },
        unreadCount: 0,
    },
    {
        id: '3',
        participant: users[3],
        lastMessage: { text: 'Pineapple on pizza is a crime, but I\'m willing to forgive you. 😉', timestamp: '3d ago' },
        unreadCount: 0,
    }
];

export const messages: Record<string, Message[]> = {
  '1': [
    { id: 'm1', senderId: '1', text: 'Hey, I love your dog! What\'s his name?', timestamp: '2h ago' },
    { id: 'm2', senderId: 'current-user', text: 'Thanks! His name is Sunny. He\'s the best.', timestamp: '1h ago' },
  ],
  '2': [
    { id: 'm3', senderId: '3', text: 'I\'m also a big fan of tacos! We should get some.', timestamp: '1d ago' },
  ],
  '3': [
    { id: 'm4', senderId: '3', text: 'Pineapple on pizza is a crime, but I\'m willing to forgive you. 😉', timestamp: '3d ago' },
  ],
};
