import { Sparkles } from "lucide-react";
import { cn } from "@/lib/utils";

export function Logo({ className }: { className?: string }) {
  return (
    <div className={cn("flex items-center gap-2", className)}>
      <Sparkles className="w-6 h-6 text-primary" />
      <h1 className="text-2xl font-headline text-primary font-bold">
        MatchSpark
      </h1>
    </div>
  );
}
