export type User = {
  id: string;
  name: string;
  age: number;
  location: string;
  bio: string;
  interests: string[];
  profilePicture: string; // URL to image
  pictures: string[];
};

export type Match = {
  user: User;
  compatibilityScore: number;
  aiSummary: string;
};

export type Conversation = {
  id: string;
  participant: User;
  lastMessage: {
    text: string;
    timestamp: string;
  };
  unreadCount: number;
};

export type Message = {
    id: string;
    senderId: string;
    text: string;
    timestamp: string;
};
