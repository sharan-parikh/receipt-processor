import type { Metadata } from "next";
import "@/app/globals.css";
import {
  ClerkProvider,
} from '@clerk/nextjs'

export const metadata: Metadata = {
  title: "Receipt Processor",
  description: "Process receipts and earn points",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <ClerkProvider>
    <html lang="en">
      <body className="antialiased">
        {/* <SignedIn>
          <UserButton />
        </SignedIn> */}
        {children}
      </body>
    </html>
    </ClerkProvider>
  );
}
