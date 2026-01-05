"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Heart,
  MessageSquare,
  User,
  Settings,
  PanelRight,
} from "lucide-react";

import {
  Sidebar,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
  SidebarMenuButton,
  SidebarProvider,
  SidebarInset,
  SidebarFooter,
  SidebarTrigger,
  useSidebar,
} from "@/components/ui/sidebar";
import { Logo } from "@/components/logo";
import { Avatar, AvatarFallback, AvatarImage } from "./ui/avatar";
import { currentUser } from "@/lib/data";
import { Separator } from "./ui/separator";
import { Button } from "./ui/button";

const navItems = [
  { href: "/matches", icon: Heart, label: "התאמות" },
  { href: "/messages", icon: MessageSquare, label: "הודעות" },
];

function HeaderContent() {
    const { state, isMobile } = useSidebar();
    const showTrigger = isMobile || state === 'collapsed';

    return (
        <header className="flex h-14 items-center gap-4 border-b bg-background px-4 md:px-6 justify-between">
           <div className="flex-1">
             {!showTrigger && <div className="md:hidden"><Logo /></div>}
             {isMobile && <Logo />}
          </div>
          {showTrigger && (
            <SidebarTrigger asChild>
                <Button variant="ghost" size="icon">
                    <PanelRight className="h-6 w-6" />
                    <span className="sr-only">פתח תפריט</span>
                </Button>
            </SidebarTrigger>
          )}
        </header>
    )
}


export function AppLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();

  return (
    <SidebarProvider>
      <Sidebar side="right">
        <SidebarHeader>
          <Logo />
        </SidebarHeader>
        <SidebarMenu>
          {navItems.map((item) => (
            <SidebarMenuItem key={item.href}>
              <SidebarMenuButton
                  asChild
                  isActive={pathname.startsWith(item.href)}
                  tooltip={item.label}
                >
                  <Link href={item.href}>
                    <span>{item.label}</span>
                    <item.icon />
                  </Link>
                </SidebarMenuButton>
            </SidebarMenuItem>
          ))}
        </SidebarMenu>
        <SidebarFooter className="mt-auto">
          <Separator className="my-2" />
          <SidebarMenu>
            <SidebarMenuItem>
                <SidebarMenuButton
                    asChild
                    isActive={pathname === "/profile"}
                    tooltip="הפרופיל שלי"
                >
                   <Link href="/profile">
                    <span>הפרופיל שלי</span>
                    <Avatar className="h-7 w-7">
                        <AvatarImage src={currentUser.profilePicture} alt={currentUser.name} />
                        <AvatarFallback>{currentUser.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                  </Link>
                </SidebarMenuButton>
            </SidebarMenuItem>
            <SidebarMenuItem>
                <SidebarMenuButton
                  asChild
                  isActive={pathname === "/settings"}
                  tooltip="הגדרות"
                >
                  <Link href="/settings">
                    <span>הגדרות</span>
                    <Settings />
                  </Link>
                </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarFooter>
      </Sidebar>
      <SidebarInset>
        <HeaderContent />
        {children}
      </SidebarInset>
    </SidebarProvider>
  );
}
