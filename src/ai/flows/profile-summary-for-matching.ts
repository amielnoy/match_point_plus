'use server';

/**
 * @fileOverview Summarizes user profiles for matching purposes.
 *
 * - profileSummaryForMatching - A function that generates a summary of two user profiles for matching.
 * - ProfileSummaryForMatchingInput - The input type for the profileSummaryForMatching function.
 * - ProfileSummaryForMatchingOutput - The return type for the profileSummaryForMatching function.
 */

import {ai} from '@/ai/genkit';
import {z} from 'genkit';

const ProfileSummaryForMatchingInputSchema = z.object({
  userProfile: z.string().describe('The user profile information.'),
  matchProfile: z.string().describe('The profile information of a potential match.'),
});
export type ProfileSummaryForMatchingInput = z.infer<typeof ProfileSummaryForMatchingInputSchema>;

const ProfileSummaryForMatchingOutputSchema = z.object({
  summary: z.string().describe('A summary of the two profiles highlighting common interests and compatibility factors.'),
});
export type ProfileSummaryForMatchingOutput = z.infer<typeof ProfileSummaryForMatchingOutputSchema>;

export async function profileSummaryForMatching(input: ProfileSummaryForMatchingInput): Promise<ProfileSummaryForMatchingOutput> {
  return profileSummaryForMatchingFlow(input);
}

const prompt = ai.definePrompt({
  name: 'profileSummaryForMatchingPrompt',
  input: {schema: ProfileSummaryForMatchingInputSchema},
  output: {schema: ProfileSummaryForMatchingOutputSchema},
  prompt: `You are a matching expert. You will be given two user profiles and you will generate a summary of the two profiles highlighting common interests and compatibility factors.

User Profile: {{{userProfile}}}
Match Profile: {{{matchProfile}}}`,
});

const profileSummaryForMatchingFlow = ai.defineFlow(
  {
    name: 'profileSummaryForMatchingFlow',
    inputSchema: ProfileSummaryForMatchingInputSchema,
    outputSchema: ProfileSummaryForMatchingOutputSchema,
  },
  async input => {
    const {output} = await prompt(input);
    return output!;
  }
);
