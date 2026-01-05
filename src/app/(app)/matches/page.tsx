"use client";

import Image from "next/image";
import { useState, useEffect } from "react";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
  type CarouselApi,
} from "@/components/ui/carousel";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Heart, MapPin, Sparkles, X } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { users as initialUsers, currentUser } from "@/lib/data";
import type { User } from "@/lib/types";
import { profileSummaryForMatching } from "@/ai/flows/profile-summary-for-matching";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Skeleton } from "@/components/ui/skeleton";
import { toast } from "@/hooks/use-toast";

export default function MatchesPage() {
  const [users, setUsers] = useState(initialUsers);
  const [api, setApi] = useState<CarouselApi>();
  const [aiSummaries, setAiSummaries] = useState<Record<string, string | null>>({});
  const [isLoadingSummary, setIsLoadingSummary] = useState(false);

  const handleSwipe = (direction: "like" | "pass") => {
    if (api) {
        if(api.selectedScrollSnap() === users.length -1){
            toast({
                title: "That's everyone for now!",
                description: "Check back later for new potential matches.",
            })
        }
      api.scrollNext();
    }
  };
  
  const getAiSummary = async (matchUser: User) => {
    if (aiSummaries[matchUser.id]) {
      return;
    }
    setIsLoadingSummary(true);
    try {
      const summary = await profileSummaryForMatching({
        userProfile: JSON.stringify(currentUser),
        matchProfile: JSON.stringify(matchUser),
      });
      setAiSummaries(prev => ({ ...prev, [matchUser.id]: summary.summary }));
    } catch (error) {
      console.error("Error fetching AI summary:", error);
      setAiSummaries(prev => ({ ...prev, [matchUser.id]: "Could not generate compatibility summary." }));
    } finally {
      setIsLoadingSummary(false);
    }
  };

  if (!users.length) {
    return (
      <div className="flex flex-col items-center justify-center h-full text-center">
        <h2 className="text-2xl font-headline mb-2">That's everyone for now!</h2>
        <p className="text-muted-foreground">Check back later for new potential matches.</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto max-w-sm py-8 flex flex-col items-center">
      <Carousel setApi={setApi} className="w-full">
        <CarouselContent>
          {users.map((user) => (
            <CarouselItem key={user.id}>
              <Card className="overflow-hidden shadow-2xl shadow-primary/10 border-none">
                <CardContent className="p-0 relative">
                  <Image
                    src={user.profilePicture}
                    alt={user.name}
                    width={500}
                    height={750}
                    className="w-full h-[550px] object-cover rounded-lg"
                    priority
                    data-ai-hint="person portrait"
                  />
                  <div className="absolute bottom-0 left-0 w-full h-2/3 bg-gradient-to-t from-black/90 to-transparent p-6 flex flex-col justify-end rounded-b-lg">
                    <h2 className="text-4xl font-headline text-white">{user.name}, {user.age}</h2>
                    <div className="flex items-center gap-2 text-white/80 mt-1">
                      <MapPin className="w-4 h-4"/>
                      <span>{user.location}</span>
                    </div>
                    <p className="text-white/90 mt-4 line-clamp-2 font-body">{user.bio}</p>
                     <div className="flex flex-wrap gap-2 mt-4">
                        {user.interests.slice(0, 4).map((interest) => (
                          <Badge key={interest} variant="secondary" className="bg-white/20 text-white border-transparent backdrop-blur-sm">
                            {interest}
                          </Badge>
                        ))}
                      </div>
                  </div>
                   <Dialog onOpenChange={(open) => open && getAiSummary(user)}>
                      <DialogTrigger asChild>
                         <Button variant="ghost" size="icon" className="absolute top-4 right-4 bg-black/30 hover:bg-black/50 text-white rounded-full">
                           <Sparkles className="w-5 h-5" />
                         </Button>
                      </DialogTrigger>
                      <DialogContent>
                        <DialogHeader>
                          <DialogTitle className="font-headline text-2xl flex items-center gap-2">
                             <Sparkles className="w-5 h-5 text-primary"/>
                            Compatibility Sparks
                          </DialogTitle>
                          <DialogDescription>
                            AI-powered insights into your potential connection with {user.name}.
                          </DialogDescription>
                        </DialogHeader>
                        {isLoadingSummary ? (
                          <div className="space-y-2 mt-4">
                            <Skeleton className="h-4 w-full" />
                            <Skeleton className="h-4 w-full" />
                            <Skeleton className="h-4 w-3/4" />
                          </div>
                        ) : (
                          <p className="text-sm text-muted-foreground mt-4 font-body">{aiSummaries[user.id]}</p>
                        )}
                      </DialogContent>
                    </Dialog>
                </CardContent>
              </Card>
            </CarouselItem>
          ))}
        </CarouselContent>
      </Carousel>

      <div className="flex justify-center items-center gap-8 mt-8">
        <Button
          variant="outline"
          size="icon"
          className="w-20 h-20 rounded-full border-4 text-muted-foreground hover:bg-accent/20"
          onClick={() => handleSwipe("pass")}
        >
          <X className="w-10 h-10" />
        </Button>
        <Button
          size="icon"
          className="w-24 h-24 rounded-full bg-primary/20 text-primary border-4 border-primary/80 hover:bg-primary/30"
          onClick={() => handleSwipe("like")}
        >
          <Heart className="w-12 h-12" />
        </Button>
      </div>
    </div>
  );
}
