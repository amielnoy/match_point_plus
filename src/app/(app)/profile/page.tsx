import Image from "next/image";
import { currentUser } from "@/lib/data";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { MapPin, Edit } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import Link from "next/link";

export default function ProfilePage() {
  const user = currentUser;

  return (
    <div className="container mx-auto max-w-4xl py-8 px-4 md:px-6">
      <Card className="overflow-hidden shadow-lg">
        <div className="md:flex md:flex-row-reverse">
          <div className="md:flex-shrink-0">
             <Image
                src={user.profilePicture}
                alt={user.name}
                width={400}
                height={600}
                className="h-96 w-full object-cover md:h-full md:w-64"
                data-ai-hint="person portrait"
              />
          </div>
          <div className="p-8 flex-1">
            <div className="flex justify-between items-start">
                 <Button asChild variant="outline">
                    <Link href="/settings">
                        <Edit className="ml-2 h-4 w-4" /> עריכת פרופיל
                    </Link>
                </Button>
                <div className="text-right">
                    <h2 className="text-4xl font-headline text-primary">{user.name}, {user.age}</h2>
                    <div className="flex items-center justify-end gap-2 text-muted-foreground mt-1">
                      <span>{user.location}</span>
                      <MapPin className="w-4 h-4"/>
                    </div>
                </div>
            </div>
            
            <div className="mt-6 text-right">
                <h3 className="font-headline text-xl">קצת עליי</h3>
                <p className="text-muted-foreground mt-2 font-body">{user.bio}</p>
            </div>

            <div className="mt-6 text-right">
                <h3 className="font-headline text-xl">תחומי עניין</h3>
                <div className="flex flex-wrap gap-2 mt-2 justify-end">
                    {user.interests.map((interest) => (
                      <Badge key={interest} variant="secondary">
                        {interest}
                      </Badge>
                    ))}
                </div>
            </div>

          </div>
        </div>
      </Card>
      
      <div className="mt-8 text-right">
        <h3 className="font-headline text-2xl mb-4">התמונות שלי</h3>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {user.pictures.map((pic, index) => (
                <Card key={index} className="overflow-hidden aspect-[2/3] relative group shadow-md">
                    <Image
                        src={pic}
                        alt={`תמונה ${index + 1}`}
                        fill
                        className="object-cover transition-transform duration-300 group-hover:scale-105"
                        data-ai-hint="person lifestyle"
                    />
                </Card>
            ))}
        </div>
      </div>
    </div>
  );
}
