import { redirect } from 'next/navigation';

export default function Home() {
  // A real app would check for auth status
  // and redirect to /login or /matches accordingly.
  // For this demo, we'll start at the login page.
  redirect('/login');
}
