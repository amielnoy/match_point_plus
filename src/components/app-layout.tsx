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
} from "@/components/ui/sidebar";
import { Logo } from "@/components/logo";
import { Avatar, AvatarFallback, AvatarImage } from "./ui/avatar";
import { currentUser } from "@/lib/data";
import { Separator } from "./ui/separator";

const navItems = [
  { href: "/matches", icon: Heart, label: "התאמות" },
  { href: "/messages", icon: MessageSquare, label: "הודעות" },
];

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
              <Link href={item.href} legacyBehavior passHref>
                <SidebarMenuButton
                  isActive={pathname.startsWith(item.href)}
                  tooltip={item.label}
                >
                  <span>{item.label}</span>
                  <item.icon />
                </SidebarMenuButton>
              </Link>
            </SidebarMenuItem>
          ))}
        </SidebarMenu>
        <SidebarFooter className="mt-auto">
          <Separator className="my-2" />
          <SidebarMenu>
            <SidebarMenuItem>
              <Link href="/profile" legacyBehavior passHref>
                <SidebarMenuButton
                    isActive={pathname === "/profile"}
                    tooltip="הפרופיל שלי"
                >
                   <span>הפרופיל שלי</span>
                  <Avatar className="h-7 w-7">
                    <AvatarImage src={currentUser.profilePicture} alt={currentUser.name} />
                    <AvatarFallback>{currentUser.name.charAt(0)}</AvatarFallback>
                  </Avatar>
                </SidebarMenuButton>
              </Link>
            </SidebarMenuItem>
            <SidebarMenuItem>
              <Link href="/settings" legacyBehavior passHref>
                <SidebarMenuButton
                  isActive={pathname === "/settings"}
                  tooltip="הגדרות"
                >
                  <span>הגדרות</span>
                  <Settings />
                </SidebarMenuButton>
              </Link>
            </SidebarMenuItem>
          </SidebarMenu>
        </SidebarFooter>
      </Sidebar>
      <SidebarInset>
        <header className="flex h-14 items-center gap-4 border-b bg-background px-4 md:hidden">
           <div className="flex-1">
            <Logo />
          </div>
          <SidebarTrigger>
            <PanelRight className="h-6 w-6" />
            <span className="sr-only">פתח תפריט</span>
          </SidebarTrigger>
        </header>
        {children}
      </SidebarInset>
    </SidebarProvider>
  );
}
