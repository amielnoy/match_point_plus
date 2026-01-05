"use client";

import { useParams } from "next/navigation";
import { conversations, messages as allMessages, currentUser } from "@/lib/data";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { ArrowRight, Send } from "lucide-react";
import Link from "next/link";
import { cn } from "@/lib/utils";

export default function MessageThreadPage() {
  const params = useParams();
  const convoId = params.id as string;
  
  const conversation = conversations.find(c => c.id === convoId);
  const messages = allMessages[convoId] || [];
  
  const participant = conversation?.participant;

  if (!participant) {
    return (
        <div className="h-full flex flex-col items-center justify-center text-center p-4">
             <h2 className="text-xl font-headline">השיחה לא נמצאה</h2>
             <p className="text-muted-foreground">השיחה נמחקה או שאינה קיימת.</p>
             <Button asChild variant="link" className="mt-4">
                 <Link href="/messages">חזרה להודעות</Link>
             </Button>
        </div>
    );
  }

  return (
    <div className="h-full flex flex-col bg-background">
       <header className="flex items-center gap-4 border-b p-4 flex-shrink-0">
        <Link href="/messages" className="md:hidden">
            <Button variant="ghost" size="icon">
                <ArrowRight />
            </Button>
        </Link>
        <div className="flex-1" />
        <div className="text-right">
          <h2 className="font-headline text-lg">{participant.name}</h2>
          <p className="text-sm text-muted-foreground">פעיל/ה עכשיו</p>
        </div>
        <Avatar>
          <AvatarImage src={participant.profilePicture} />
          <AvatarFallback>{participant.name.charAt(0)}</AvatarFallback>
        </Avatar>
      </header>
      <div className="flex-1 overflow-y-auto p-6 space-y-4">
        {messages.map(msg => {
            const isCurrentUser = msg.senderId === currentUser.id;
            const sender = isCurrentUser ? currentUser : participant;
            return (
                 <div key={msg.id} className={cn("flex items-end gap-2", isCurrentUser && "justify-end")}>
                    
                    <div className={cn(
                        "max-w-xs md:max-w-md p-3 rounded-lg shadow-sm",
                        isCurrentUser ? "bg-primary text-primary-foreground rounded-bl-none" : "bg-card border rounded-br-none"
                    )}>
                        <p className="text-sm">{msg.text}</p>
                    </div>
                    {!isCurrentUser && (
                         <Avatar className="h-8 w-8">
                            <AvatarImage src={sender.profilePicture} />
                            <AvatarFallback>{sender.name.charAt(0)}</AvatarFallback>
                        </Avatar>
                    )}
                 </div>
            )
        })}
      </div>
      <footer className="p-4 border-t bg-background">
        <form className="flex items-center gap-2">
            <Button type="submit" size="icon">
                <Send className="h-4 w-4" />
            </Button>
            <Input placeholder="כתוב/י הודעה..." className="bg-card"/>
        </form>
      </footer>
    </div>
  );
}
