import { SignIn } from '@clerk/nextjs'

export default function SignInPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 px-4">
      <SignIn 
        appearance={{
          elements: {
            socialButtonsBlockButton: {
              display: 'none'
            }
          }
        }}
      />
    </div>
  )
} 