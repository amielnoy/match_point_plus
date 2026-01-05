import { conversations } from "@/lib/data";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import Link from "next/link";
import { cn } from "@/lib/utils";
import { Input } from "@/components/ui/input";
import { Search } from "lucide-react";

export default function MessagesPage() {
  return (
    <div className="h-full flex flex-col bg-background">
      <div className="p-4 md:p-6 border-b">
        <h1 className="text-2xl font-headline">הודעות</h1>
        <div className="relative mt-4">
            <Search className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input placeholder="חיפוש שיחות..." className="pr-9" />
        </div>
      </div>
      <div className="flex-1 overflow-auto">
        {conversations.map((convo) => (
          <Link href={`/messages/${convo.id}`} key={convo.id} className="block hover:bg-accent/50 transition-colors">
            <div className="flex items-center gap-4 p-4 md:p-6 border-b">
              {convo.unreadCount > 0 && (
                <div className="w-2 h-2 rounded-full bg-primary flex-shrink-0">
                </div>
              )}
               <div className="flex-1 overflow-hidden">
                <div className="flex justify-between">
                   <p className="text-xs text-muted-foreground flex-shrink-0 ml-2">{convo.lastMessage.timestamp}</p>
                  <h3 className="font-semibold font-headline text-lg">{convo.participant.name}</h3>
                </div>
                <p className={cn(
                    "text-sm text-muted-foreground truncate text-right",
                    convo.unreadCount > 0 && "font-bold text-foreground"
                )}>
                  {convo.lastMessage.text}
                </p>
              </div>
              <Avatar className="h-14 w-14">
                <AvatarImage src={convo.participant.profilePicture} />
                <AvatarFallback>{convo.participant.name.charAt(0)}</AvatarFallback>
              </Avatar>
            </div>
          </Link>
        ))}
         {conversations.length === 0 && (
          <div className="text-center p-10">
            <h3 className="font-headline text-xl">אין הודעות עדיין</h3>
            <p className="text-muted-foreground mt-2">התחילו למצוא התאמות כדי להתחיל שיחות!</p>
          </div>
        )}
      </div>
    </div>
  );
}
