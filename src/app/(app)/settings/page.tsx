"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Slider } from "@/components/ui/slider";
import { Card, CardContent } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { currentUser } from "@/lib/data";
import { toast } from "@/hooks/use-toast";
import Image from "next/image";
import { Trash2, Plus } from "lucide-react";
import { Badge } from "@/components/ui/badge";

const profileFormSchema = z.object({
  name: z.string().min(2, "Name must be at least 2 characters."),
  bio: z.string().max(300, "Bio cannot exceed 300 characters.").min(10, "Bio must be at least 10 characters."),
  location: z.string(),
  interests: z.array(z.string()).max(8, "You can add up to 8 interests."),
});

const preferencesFormSchema = z.object({
    ageRange: z.array(z.number()).length(2),
    distance: z.number().min(1).max(100),
});

type ProfileFormValues = z.infer<typeof profileFormSchema>;
type PreferencesFormValues = z.infer<typeof preferencesFormSchema>;

export default function SettingsPage() {
  const profileForm = useForm<ProfileFormValues>({
    resolver: zodResolver(profileFormSchema),
    defaultValues: {
      name: currentUser.name,
      bio: currentUser.bio,
      location: currentUser.location,
      interests: currentUser.interests,
    },
  });
  
  const preferencesForm = useForm<PreferencesFormValues>({
    resolver: zodResolver(preferencesFormSchema),
    defaultValues: {
        ageRange: [25, 35],
        distance: 25,
    }
  });

  function onProfileSubmit(data: ProfileFormValues) {
    toast({
      title: "Profile Updated",
      description: "Your changes have been saved successfully.",
    });
  }
  
  function onPreferencesSubmit(data: PreferencesFormValues) {
    toast({
      title: "Preferences Saved",
      description: "Your matching preferences have been updated.",
    });
  }
  
  return (
    <div className="container mx-auto max-w-4xl py-8 px-4 md:px-6">
      <h1 className="text-3xl font-headline mb-6">Settings</h1>
      <Tabs defaultValue="profile" className="w-full">
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="profile">Profile</TabsTrigger>
          <TabsTrigger value="photos">Photos</TabsTrigger>
          <TabsTrigger value="preferences">Preferences</TabsTrigger>
        </TabsList>
        <TabsContent value="profile" className="mt-6">
          <Card>
            <CardContent className="pt-6">
              <Form {...profileForm}>
                <form onSubmit={profileForm.handleSubmit(onProfileSubmit)} className="space-y-8">
                  <FormField
                    control={profileForm.control}
                    name="name"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Name</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={profileForm.control}
                    name="bio"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>About Me</FormLabel>
                        <FormControl>
                          <Textarea rows={5} {...field} />
                        </FormControl>
                         <FormDescription>
                            A short and sweet bio to attract your spark.
                        </FormDescription>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={profileForm.control}
                    name="location"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Location</FormLabel>
                        <FormControl>
                          <Input {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormItem>
                     <FormLabel>Interests</FormLabel>
                     <div className="flex flex-wrap gap-2">
                        {profileForm.getValues('interests').map(interest => (
                            <Badge key={interest} variant="secondary">{interest}</Badge>
                        ))}
                     </div>
                     <FormDescription>Edit your interests to improve matches.</FormDescription>
                  </FormItem>
                  <Button type="submit">Save Changes</Button>
                </form>
              </Form>
            </CardContent>
          </Card>
        </TabsContent>
        <TabsContent value="photos" className="mt-6">
            <Card>
                <CardContent className="pt-6">
                    <p className="text-sm text-muted-foreground mb-4">Manage your profile pictures. Your first picture is your main one.</p>
                     <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                        {currentUser.pictures.map((pic, index) => (
                            <Card key={index} className="overflow-hidden aspect-square relative group shadow-sm">
                                <Image src={pic} alt={`Photo ${index + 1}`} fill className="object-cover" data-ai-hint="person lifestyle" />
                                <div className="absolute inset-0 bg-black/20 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                                    <Button variant="destructive" size="icon"><Trash2 className="h-4 w-4" /></Button>
                                </div>
                            </Card>
                        ))}
                        <Card className="aspect-square flex items-center justify-center border-dashed hover:border-primary hover:text-primary transition-colors">
                             <Button variant="ghost" className="flex flex-col h-24 w-24">
                                <Plus className="h-6 w-6 mb-1"/>
                                Add Photo
                             </Button>
                        </Card>
                    </div>
                </CardContent>
            </Card>
        </TabsContent>
        <TabsContent value="preferences" className="mt-6">
          <Card>
            <CardContent className="pt-6">
              <Form {...preferencesForm}>
                <form onSubmit={preferencesForm.handleSubmit(onPreferencesSubmit)} className="space-y-8">
                  <FormField
                    control={preferencesForm.control}
                    name="ageRange"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Age Range</FormLabel>
                        <FormControl>
                          <Slider
                            min={18}
                            max={70}
                            step={1}
                            value={field.value}
                            onValueChange={field.onChange}
                          />
                        </FormControl>
                        <div className="flex justify-between text-sm text-muted-foreground">
                            <span>{field.value[0]}</span>
                            <span>{field.value[1]}</span>
                        </div>
                      </FormItem>
                    )}
                  />
                   <FormField
                    control={preferencesForm.control}
                    name="distance"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Maximum Distance</FormLabel>
                        <FormControl>
                          <Slider
                            min={1}
                            max={100}
                            step={1}
                            value={[field.value]}
                            onValueChange={(value) => field.onChange(value[0])}
                          />
                        </FormControl>
                        <div className="text-sm text-muted-foreground text-center">
                            {field.value} miles
                        </div>
                      </FormItem>
                    )}
                  />
                  <Button type="submit">Save Preferences</Button>
                </form>
              </Form>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
