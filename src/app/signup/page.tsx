'use client'

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Sparkles } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function SignupPage() {
  const router = useRouter();

  const handleSignup = (e: React.FormEvent) => {
    e.preventDefault();
    // In a real app, you'd handle form validation and user creation here.
    // For this demo, we'll just redirect to the matches page.
    router.push('/matches');
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-background p-4">
      <Card className="w-full max-w-sm">
        <CardHeader className="text-center">
          <div className="flex justify-center items-center gap-2 mb-2">
            <Sparkles className="w-8 h-8 text-primary" />
            <h1 className="text-4xl font-headline text-primary">MatchSpark</h1>
          </div>
          <CardTitle className="font-headline text-2xl">יצירת חשבון</CardTitle>
          <CardDescription>הצטרפו ומצאו את הניצוץ שלכם עוד היום.</CardDescription>
        </CardHeader>
        <form onSubmit={handleSignup}>
          <CardContent className="space-y-4 text-right">
            <div className="space-y-2">
              <Label htmlFor="name">שם</Label>
              <Input id="name" type="text" placeholder="השם שלך" required />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">אימייל</Label>
              <Input id="email" type="email" placeholder="you@example.com" required dir="ltr" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">סיסמה</Label>
              <Input id="password" type="password" required dir="ltr" />
            </div>
             <div className="space-y-2">
              <Label htmlFor="confirm-password">אימות סיסמה</Label>
              <Input id="confirm-password" type="password" required dir="ltr" />
            </div>
          </CardContent>
          <CardFooter className="flex flex-col gap-4">
            <Button type="submit" className="w-full font-headline">הרשמה</Button>
            <p className="text-xs text-muted-foreground">
              יש לכם כבר חשבון? <Link href="/login" className="text-primary hover:underline">כניסה</Link>
            </p>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
}
