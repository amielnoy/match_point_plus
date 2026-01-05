import { Sparkles } from "lucide-react";
import { cn } from "@/lib/utils";

export function Logo({ className }: { className?: string }) {
  return (
    <div className={cn("flex items-center justify-end gap-2", className)}>
      <h1 className="text-2xl font-headline text-primary font-bold">
        MatchSpark
      </h1>
      <Sparkles className="w-6 h-6 text-primary" />
    </div>
  );
}
