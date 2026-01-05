'use server';

/**
 * @fileOverview Suggests profile improvements to increase match potential.
 *
 * - suggestProfileImprovements - A function that takes user profile data and suggests improvements.
 * - SuggestProfileImprovementsInput - The input type for the suggestProfileImprovements function.
 * - SuggestProfileImprovementsOutput - The return type for the suggestProfileImprovements function.
 */

import {ai} from '@/ai/genkit';
import {z} from 'genkit';

const SuggestProfileImprovementsInputSchema = z.object({
  profileSummary: z
    .string()
    .describe('A summary of the user profile, including interests, photos, and personal information.'),
  matchingPreferences: z
    .string()
    .describe('The user matching preferences based on age, interests and location'),
});
export type SuggestProfileImprovementsInput = z.infer<typeof SuggestProfileImprovementsInputSchema>;

const SuggestProfileImprovementsOutputSchema = z.object({
  suggestedImprovements: z.string().describe('Suggestions to improve the user profile for better matches.'),
});
export type SuggestProfileImprovementsOutput = z.infer<typeof SuggestProfileImprovementsOutputSchema>;

export async function suggestProfileImprovements(input: SuggestProfileImprovementsInput): Promise<SuggestProfileImprovementsOutput> {
  return suggestProfileImprovementsFlow(input);
}

const prompt = ai.definePrompt({
  name: 'suggestProfileImprovementsPrompt',
  input: {schema: SuggestProfileImprovementsInputSchema},
  output: {schema: SuggestProfileImprovementsOutputSchema},
  prompt: `You are an expert dating profile consultant. Analyze the user's profile and preferences and provide actionable suggestions to improve their profile and attract better matches. Respond in Hebrew.

User Profile Summary: {{{profileSummary}}}

Matching Preferences: {{{matchingPreferences}}}

Based on this information, suggest improvements to the user's profile:
`, safetySettings: [
    {
      category: 'HARM_CATEGORY_HATE_SPEECH',
      threshold: 'BLOCK_ONLY_HIGH',
    },
    {
      category: 'HARM_CATEGORY_DANGEROUS_CONTENT',
      threshold: 'BLOCK_NONE',
    },
    {
      category: 'HARM_CATEGORY_HARASSMENT',
      threshold: 'BLOCK_MEDIUM_AND_ABOVE',
    },
    {
      category: 'HARM_CATEGORY_SEXUALLY_EXPLICIT',
      threshold: 'BLOCK_LOW_AND_ABOVE',
    },
  ],
});

const suggestProfileImprovementsFlow = ai.defineFlow(
  {
    name: 'suggestProfileImprovementsFlow',
    inputSchema: SuggestProfileImprovementsInputSchema,
    outputSchema: SuggestProfileImprovementsOutputSchema,
  },
  async input => {
    const {output} = await prompt(input);
    return output!;
  }
);
