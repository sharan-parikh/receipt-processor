"use client"

import type React from "react"
import { useAuth } from "@clerk/nextjs"
import { useRouter, usePathname } from "next/navigation"
import { useEffect } from "react"

export function AuthGuard({ children }: { children: React.ReactNode }) {
  const { isLoaded, isSignedIn } = useAuth()
  const router = useRouter()

  useEffect(() => {
    const currentPath = usePathname();
    if (isLoaded && !isSignedIn && currentPath !== "/sign-in" && currentPath !== "/sign-up") {
      router.push("/sign-in")
    }
  }, [isLoaded, isSignedIn, router])

  if (!isLoaded) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (!isSignedIn) {
    return null
  }

  return <>{children}</>
}
